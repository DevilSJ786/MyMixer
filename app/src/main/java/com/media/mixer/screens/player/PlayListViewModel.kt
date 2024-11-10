package com.media.mixer.screens.player

import android.content.Context
import androidx.compose.runtime.mutableStateListOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.media3.common.Player
import androidx.media3.common.util.UnstableApi
import com.media.mixer.data.entities.Playlist
import com.media.mixer.data.entities.Song
import com.media.mixer.domain.model.Artist
import com.media.mixer.domain.repository.LocalRepository
import com.media.mixer.domain.usecase.GetSortedAudioUseCase
import com.media.mixer.media.audio.MusicServiceConnection
import com.media.mixer.media.audio.common.MusicState
import com.media.mixer.media.domain.mapper.asMediaItem
import com.media.mixer.media.domain.utils.MediaConstants
import com.media.mixer.media.domain.utils.asPlaybackState
import com.media.mixer.media.domain.utils.convertToPosition
import com.media.mixer.media.domain.utils.orDefaultTimestamp
import com.media.mixer.media.presentation.PlayerEvent
import com.media.mixer.screens.audioplayer.screen.PlaylistState
import com.media.mixer.screens.audioplayer.screen.State
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
@androidx.annotation.OptIn(UnstableApi::class)
class PlayListViewModel @Inject constructor(
    private val musicServiceConnection: MusicServiceConnection,
    private val player: Player,
    getSortedAudioUseCase: GetSortedAudioUseCase,
    private val localRepository: LocalRepository
) : ViewModel() {
         fun getPlayer()=player
    private val _musicState = MutableStateFlow(MusicState())
    val musicState = _musicState.asStateFlow()
    private val allSongs = mutableStateListOf<Song>()

    val list = getSortedAudioUseCase.invoke()
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = emptyList()
        )

    private val listFav = mutableStateListOf<Song>()
    val musicListFav = MutableStateFlow(listFav)

    private val _artists = mutableStateListOf<Artist>()
    val artists = MutableStateFlow(_artists)
    private val _artistMusic = mutableStateListOf<Song>()
    val artistMusic = MutableStateFlow(_artistMusic)

    private val _library = mutableStateListOf<Artist>()
    val libraryList = MutableStateFlow(_library)
    private val _libraryMusic = mutableStateListOf<Song>()
    val libraryMusic = MutableStateFlow(_libraryMusic)

    private val _playList = mutableStateListOf<Playlist>()
    val playList = MutableStateFlow(_playList)

    private val _playlistSong = mutableStateListOf<Song>()
    val playlistSong = MutableStateFlow(_playlistSong)

    private val playingList = mutableStateListOf<Song>()


    private var playlistState = PlaylistState()

    var artist: Artist? = null
    var library: Artist? = null

    private val listener = object : Player.Listener {
        override fun onEvents(player: Player, events: Player.Events) {
            super.onEvents(player, events)
            if (events.containsAny(
                    Player.EVENT_PLAYBACK_STATE_CHANGED,
                    Player.EVENT_MEDIA_METADATA_CHANGED,
                    Player.EVENT_PLAY_WHEN_READY_CHANGED
                )
            ) {
                updateMusicState(player)
            }
        }
    }

    init {
        player.addListener(listener)
        viewModelScope.launch {
            list.collectLatest { songList ->
                if (songList.isNotEmpty()) {
                    allSongs.clear()
                    listFav.clear()
                    listFav.addAll(songList.filter { it.isFev })
                    setArtistList(songList.sortedBy { it.artist })
                    setLibraryList(songList.sortedBy { it.album })
                    allSongs.addAll(songList)
                }
            }
        }
    }

    fun setPlayerListBaseOnState(state: PlaylistState) {
        if (state.from == playlistState.from) {
            if (playingList.isNotEmpty()) playIndex(state.index)
            else {
                playingList.addAll(
                    when (state.from) {
                        State.SONGS -> allSongs
                        State.FAVORITE -> listFav
                        State.ARTIST -> _artistMusic
                        State.LIBRARY -> _libraryMusic
                        State.PLAYLIST -> _playlistSong
                    }
                )
                startPlayer(state.index, playingList)
            }
        } else {
            playlistState = state
            playingList.clear()
            playingList.addAll(
                when (state.from) {
                    State.SONGS -> allSongs
                    State.FAVORITE -> listFav
                    State.ARTIST -> _artistMusic
                    State.LIBRARY -> _libraryMusic
                    State.PLAYLIST -> _playlistSong
                }
            )
            startPlayer(state.index, playingList)
        }
    }

    private fun setLibraryList(media: List<Song>) {
        _library.clear()
        var library = Artist(name = media.first().album, image = media.first().artworkUri)
        _library.add(library)
        media.forEach {
            val temp = Artist(name = it.album, image = it.artworkUri)
            if (temp.name != library.name) {
                library = temp
                _library.add(temp)
            }
        }
    }

    private fun setArtistList(media: List<Song>) {
        _artists.clear()
        var artist = Artist(name = media.first().artist, image = media.first().artworkUri)
        _artists.add(artist)
        media.forEach {
            val temp = Artist(name = it.artist, image = it.artworkUri)
            if (temp.name != artist.name) {
                artist = temp
                _artists.add(temp)
            }
        }
    }

    fun getArtistMusicList(art: Artist) {
        artist = art
        _artistMusic.clear()
        _artistMusic.addAll(allSongs.filter { it.artist == art.name })
    }

    fun getLibraryMusicList(lib: Artist) {
        library = lib
        _libraryMusic.clear()
        _libraryMusic.addAll(allSongs.filter { it.album == lib.name })
    }


    private fun startPlayer(
        startIndex: Int = MediaConstants.DEFAULT_INDEX,
        list: List<Song>
    ) {
        player.clearMediaItems()
        player.setMediaItems(list.map { it.asMediaItem() }, startIndex, 0)
        player.prepare()
        player.play()
    }


    fun onAudioEvent(event: PlayerEvent) {
        when (event) {
            is PlayerEvent.PlayIndex -> playIndex(event.index)
            is PlayerEvent.Play -> play()
            is PlayerEvent.Pause -> pause()
            is PlayerEvent.SkipNext -> skipNext()
            is PlayerEvent.SkipPrevious -> skipPrevious()
            is PlayerEvent.SkipTo -> skipTo(event.value)
            is PlayerEvent.SkipForward -> forward()
            is PlayerEvent.SkipBack -> backward()
        }
    }


    fun release(context: Context) {
        musicServiceConnection.release(context)
    }

    private fun skipPrevious() {
        musicServiceConnection.skipPrevious()
    }

    private fun playIndex(index: Int) {
        musicServiceConnection.playIndex(index)
    }

    fun play() {
        musicServiceConnection.play()
    }

    private fun pause() {
        musicServiceConnection.pause()
    }

    private fun skipNext() {
        musicServiceConnection.skipNext()
    }

    private fun skipTo(position: Float) =
        musicServiceConnection.skipTo(convertToPosition(position, musicState.value.duration))

    private fun forward() = musicServiceConnection.forward()
    private fun backward() = musicServiceConnection.backward()

    fun duration(value: String = "00:15:33"): Int {
        val data = value.split(":")
        return data[1].toInt() + (data[2].toInt() / 60)
    }

    init {
        getPlaylist()
    }

    private fun getPlaylist() {
        viewModelScope.launch(Dispatchers.Main) {
            localRepository.getPlaylist(isVideo = false).collectLatest {
                _playList.clear()
                _playList.addAll(it)
            }
        }
    }

    fun addNewPlaylist(it: String) {
        viewModelScope.launch(Dispatchers.IO) {
            localRepository.insertPlaylist(Playlist(name = it, count = 0, isVideoPlaylist = false))
        }
    }

    fun updateSongFav(id: Int, state: Boolean) {
        viewModelScope.launch {
            localRepository.updateSongFav(id, state)
        }
    }

    fun getAudioList(playlist: Playlist) {
        viewModelScope.launch(Dispatchers.IO) {
            localRepository.getPlaylistSong().collectLatest { songList ->
                _playlistSong.clear()
                _playlistSong.addAll(songList.filter { it.playlistMap[playlist.id] == true })
            }
        }
    }

    fun addSongToPlaylist(song: Song, playlistId: Long) {
        viewModelScope.launch(Dispatchers.IO) {
            val playlist = localRepository.getSinglePlaylist(playlistId)
            val map = song.playlistMap.toMutableMap()
            if (!map.containsKey(playlistId)) {
                map[playlistId] = true
                localRepository.insertSong(song.copy(playlistMap = map))

                localRepository.updatePlaylistCount(
                    id = playlistId,
                    count = playlist?.count?.plus(1) ?: 0
                )
            }
        }
    }

    fun deletePlaylist(playlist: Playlist) {
        viewModelScope.launch {
            localRepository.deletePlaylist(playlist)
        }
    }


    private fun updateMusicState(player: Player) = with(player) {
        allSongs.find { it.id == currentMediaItem?.mediaId?.toInt() }?.let { song ->
            _musicState.update {
                it.copy(
                    currentSong = song,
                    playbackState = playbackState.asPlaybackState(),
                    playWhenReady = playWhenReady,
                    duration = duration.orDefaultTimestamp()
                )
            }
        }
    }

    override fun onCleared() {
        super.onCleared()
        player.removeListener(listener)
        allSongs.clear()
    }
}