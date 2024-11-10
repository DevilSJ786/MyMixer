package com.media.mixer.screens.video.screens

import android.content.Context
import android.net.Uri
import android.util.Log
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.media3.common.C
import androidx.media3.common.MediaItem
import androidx.media3.common.Player
import androidx.media3.common.Timeline
import androidx.media3.common.util.UnstableApi
import androidx.media3.exoplayer.ExoPlayer
import com.media.mixer.core.utils.DefaultSaveLocationKey
import com.media.mixer.core.utils.isSchemaContent
import com.media.mixer.domain.model.Video
import com.media.mixer.domain.model.VideoState
import com.media.mixer.domain.repository.MediaRepository
import com.media.mixer.media.audio.common.MusicState
import com.media.mixer.media.domain.utils.MediaConstants
import com.media.mixer.media.domain.utils.convertToPosition
import com.media.mixer.media.domain.utils.getCurrentTrackIndex
import com.media.mixer.media.media.ResizeMode
import com.media.mixer.media.presentation.UiState
import com.media.mixer.media.presentation.PlayerEvent
import dagger.hilt.android.lifecycle.HiltViewModel
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import javax.inject.Inject

@HiltViewModel
@androidx.annotation.OptIn(UnstableApi::class)
class VideoPlayerViewModel @Inject constructor(
    @ApplicationContext context: Context,
    private val mediaRepository: MediaRepository,
    private val dataStore: DataStore<Preferences>
) : ViewModel() {
    var player: Player? = null
    fun getVideoPlayer() = player
    var currentMedia:Uri?=null

    private val listener = object : Player.Listener {

        override fun onTimelineChanged(timeline: Timeline, reason: Int) {
            // recover the remembered state if media id matched
            Log.d("TAG", "onTimelineChanged: change")
            currentMedia?.let {
                viewModelScope.launch { updateState(it.toString())  }
            }
        }
    }
    private val _musicState = MutableStateFlow(MusicState())
    val musicState = _musicState.asStateFlow()

    private var _videoUiState = MutableStateFlow(UiState())
    val videoUiState: StateFlow<UiState> = _videoUiState.asStateFlow()

    private val _saveLocation = MutableStateFlow("")
    val saveLocation: StateFlow<String> = _saveLocation.asStateFlow()

    init {
        createPlayer(context)
        viewModelScope.launch {
            dataStore.data.collectLatest { preferences ->
                preferences[stringPreferencesKey(DefaultSaveLocationKey)]?.let {
                    _saveLocation.value=it
                }
            }
        }
    }
    fun setDefaultDownloadLocation(s:String) {
        viewModelScope.launch {
            withContext(Dispatchers.IO) {
                dataStore.edit {
                    it[stringPreferencesKey(DefaultSaveLocationKey)] = s
                }
            }
        }
    }
    private fun createPlayer(context: Context) {
        player = ExoPlayer.Builder(context).build()
        player?.apply {
            addListener(listener)
        }
    }

    fun setVideoSize(mode: ResizeMode) {
        _videoUiState.update {
            it.copy(resizeMode = mode)
        }
    }

    var currentPlaybackPosition: Long? = null
    var currentPlaybackSpeed: Float = 1f
    var isPlaybackSpeedChanged: Boolean = false

    private var currentVideoState: VideoState? = null


    suspend fun updateState(uri: String?) {
        currentVideoState = uri?.let { mediaRepository.getVideoState(it) }
        currentPlaybackPosition = currentVideoState?.position
        currentPlaybackSpeed = currentVideoState?.playbackSpeed?:currentPlaybackSpeed
        player?.seekTo(currentPlaybackPosition?:0)
    }


    fun saveState() {
        currentPlaybackPosition =  player!!.currentPosition
        currentPlaybackSpeed = player!!.playbackParameters.speed

        if (!currentMedia!!.isSchemaContent) return

        val newPosition = currentPlaybackPosition.takeIf {
            currentPlaybackPosition!! < player!!.duration - 5L
        } ?: C.TIME_UNSET

        viewModelScope.launch {
            mediaRepository.saveVideoState(
                uri = currentMedia.toString(),
                position = newPosition,
                audioTrackIndex = player!!.getCurrentTrackIndex(C.TRACK_TYPE_AUDIO),
                playbackSpeed = currentPlaybackSpeed
            )
        }
        currentMedia=null
    }

    fun playSongs(
        videos: List<Video>,
        startIndex: Int = MediaConstants.DEFAULT_INDEX,
        startPositionMs: Long = MediaConstants.DEFAULT_POSITION_MS
    ) {
        player?.run {
            clearMediaItems()
            setMediaItems(
                videos.map { MediaItem.fromUri(it.uriString) },
                startIndex,
                startPositionMs
            )
            prepare()
            playWhenReady = true
        }
    }

    fun startVideo(uri: Uri) {
        currentMedia=uri
        player?.run {
            setMediaItem(MediaItem.fromUri(uri))
            prepare()
            playWhenReady = true
        }
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


    private fun skipPrevious() {
        player?.seekToPrevious()
        player?.play()
    }

    private fun playIndex(index: Int) {
        player?.seekTo(index, 0)
    }

    fun play() {
        player?.play()
    }

     fun pause() {
        player?.pause()
    }

    private fun skipNext() {
        player?.seekToNext()
        player?.play()
    }

    private fun skipTo(position: Float) =
        player?.seekTo(convertToPosition(position, musicState.value.duration))

    private fun forward() = player?.seekForward()
    private fun backward() = player?.seekBack()

    fun duration(value: String = "00:15:33"): Int {
        val data = value.split(":")
        return data[1].toInt() + (data[2].toInt() / 60)
    }

    override fun onCleared() {

        super.onCleared()
        saveState()
        player?.removeListener(listener)
        player?.release()
    }
}