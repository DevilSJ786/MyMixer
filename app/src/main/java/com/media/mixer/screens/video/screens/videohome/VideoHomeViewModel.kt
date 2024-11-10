package com.media.mixer.screens.video.screens.videohome

import android.net.Uri
import androidx.compose.runtime.mutableStateListOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.media.mixer.data.entities.Playlist
import com.media.mixer.data.sync.MediaInfoSynchronizer
import com.media.mixer.domain.model.Video
import com.media.mixer.domain.repository.LocalRepository
import com.media.mixer.domain.repository.MediaRepository
import com.media.mixer.domain.usecase.GetPlaylistsUseCase
import com.media.mixer.domain.usecase.GetSortedFoldersUseCase
import com.media.mixer.domain.usecase.GetSortedVideosUseCase
import com.media.mixer.screens.video.screens.FoldersState
import com.media.mixer.screens.video.screens.VideosState
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class VideoHomeViewModel @Inject constructor(
    private val getSortedVideosUseCase: GetSortedVideosUseCase,
    getSortedFoldersUseCase: GetSortedFoldersUseCase,
    private val getPlaylistsUseCase: GetPlaylistsUseCase,
    private val localRepository: LocalRepository,
    private val localMediaRepository: MediaRepository,
    private val mediaInfoSynchronizer: MediaInfoSynchronizer
) : ViewModel() {

    private val _videosFromPath = MutableStateFlow<VideosState>(VideosState.Loading)
    val videosFromPath: StateFlow<VideosState> = _videosFromPath.asStateFlow()
    private var job: Job? = null
    var folderPathValue: String = ""

    val videos = getSortedVideosUseCase.invoke()
        .map { VideosState.Success(it) }
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = VideosState.Loading
        )
    val videosFavorite = getSortedVideosUseCase.invoke()
        .map { videoList -> VideosState.Success(videoList.filter { it.isFev }) }
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = VideosState.Loading
        )


    val foldersState = getSortedFoldersUseCase.invoke()
        .map { FoldersState.Success(it) }
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = FoldersState.Loading
        )


    private val _playList = mutableStateListOf<Playlist>()
    val playList = MutableStateFlow(_playList)

    private val _playlistVideo = MutableStateFlow<VideosState>(VideosState.Loading)
    val playlistVideo: StateFlow<VideosState> = _playlistVideo.asStateFlow()

    fun addToMediaInfoSynchronizer(uri: Uri) {
        viewModelScope.launch {
            mediaInfoSynchronizer.addMedia(uri)
        }
    }

    fun setFolderPath(path: String) {
        if (path.isNotEmpty()) {
            folderPathValue = path
            job?.cancel()
            job = viewModelScope.launch {
                getSortedVideosUseCase.invoke(folderPathValue).collectLatest {
                    _videosFromPath.value = VideosState.Success(it)
                }
            }
        }
    }

    init {
        getPlaylist()
    }

    private fun getPlaylist() {
        viewModelScope.launch(Dispatchers.Main) {
            getPlaylistsUseCase.invoke(isVideo = true).collectLatest {
                _playList.clear()
                _playList.addAll(it)
            }
        }
    }

    fun addNewPlaylist(it: String) {
        viewModelScope.launch(Dispatchers.IO) {
            localRepository.insertPlaylist(Playlist(name = it, count = 0, isVideoPlaylist = true))
        }
    }

    fun updateVideoFav(id: Long, state: Boolean) {
        viewModelScope.launch {
            localMediaRepository.updateVideoFav(id, state)
        }
    }

    fun getVideosOfPlaylist(playlist: Playlist) {
        viewModelScope.launch(Dispatchers.IO) {
            videos.collectLatest { videoState ->
                if (videoState is VideosState.Success) {
                    _playlistVideo.value =
                        VideosState.Success(videoState.data.filter { it.playlistMap[playlist.id] == true })
                }
            }
        }
    }

    fun addVideoToPlaylist(video: Video, playlistId: Long) {
        viewModelScope.launch(Dispatchers.IO) {
            val playlist = localRepository.getSinglePlaylist(playlistId)
            val map = video.playlistMap.toMutableMap()
            if (!map.containsKey(playlistId)) {
                map[playlistId] = true
                localMediaRepository.updateVideoPlaylist(video.id, map)
                localRepository.updatePlaylistCount(
                    id = playlistId,
                    count = playlist?.count?.plus(1) ?: 0
                )
            }
        }
    }

    fun deletePlaylist(playlist: Playlist) {
        viewModelScope.launch(Dispatchers.IO) {
            localRepository.deletePlaylist(playlist)
        }
    }
}