package com.media.mixer.screens.audiocutter

import android.content.Context
import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.media3.common.MediaItem
import androidx.media3.common.Player
import androidx.media3.exoplayer.ExoPlayer
import com.media.mixer.core.utils.StoragePath
import com.media.mixer.core.utils.TransformerState
import com.media.mixer.core.utils.createBundle
import com.media.mixer.core.utils.createExternalFile
import com.media.mixer.core.utils.getAudioEndPoint
import com.media.mixer.core.utils.getAudioMemeType
import com.media.mixer.core.utils.saveAs
import com.media.mixer.core.utils.startExport
import com.media.mixer.data.entities.Song
import com.media.mixer.media.audio.common.MusicState
import com.media.mixer.media.domain.utils.asPlaybackState
import com.media.mixer.media.domain.utils.orDefaultTimestamp
import com.media.mixer.screens.videotoaudio.CutterUiState
import dagger.hilt.android.lifecycle.HiltViewModel
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import java.io.File
import javax.inject.Inject

@HiltViewModel
class AudioCutterViewModel @Inject constructor(@ApplicationContext context: Context) : ViewModel() {
    var player: Player? = null

    var outputMimeType: String = ""
    var outputFileName: String = ""
    var audioPath: String = ""
    var outputAudioPath: String = ""

    private val _musicState = MutableStateFlow(MusicState())
    val musicState = _musicState.asStateFlow()

    private val _transformerState = MutableStateFlow<TransformerState>(TransformerState.Loading)
    val transformerState: StateFlow<TransformerState> = _transformerState.asStateFlow()

    private val _trimState = MutableStateFlow<CutterUiState>(CutterUiState.AudioSelection)
    val trimState = _trimState.asStateFlow()

    var song: Song? = null

    init {
        createPlayer(context)
    }

    fun saveAsFile(context: Context, saveLocation:String){
        viewModelScope.launch {
            saveAs(
                context = context,
                saveLocation = saveLocation,
                filePath = outputAudioPath,
                fileName = outputFileName,
                mimeType =outputMimeType
            )
        }
    }
    fun updateTrimState(state: CutterUiState) {
        _trimState.value = state
    }

    fun startAudioExport(
        context: Context,
        file: String,
        title: String = "Output",
        storagePath: StoragePath,
        format: String,
        start: Long,
        end: Long
    ) {
        outputMimeType = getAudioMemeType(format)
        outputFileName = title + "${(0..9999).random()}"
        startExport(
            uri = file,
            bundle = createBundle(
                removeAudio = false,
                removeVideo = false,
                trimCheckBox = true,
                start = start,
                end = end,
                selectedAudioMimeType = outputMimeType
            ),
            context = context,
            outputFile = createExternalFile(
                fileName =outputFileName+ ".${getAudioEndPoint(format)}" ,
                fileStorage = storagePath.path,
                context = context
            ),
            onStart = {
                Log.d("TAG", "startAudioExport: ")
                _transformerState.value=TransformerState.Loading
            },
            onCompletion = { outputfilePath ->
                delete()
                outputAudioPath = outputfilePath
                Log.i("TAG", "doneAudioExport: $outputfilePath")
                _transformerState.value=TransformerState.Success
                setAndPlay("file://$outputAudioPath")
            },
            onException = {
                _transformerState.value=TransformerState.Error
            })
    }

    private fun createPlayer(context: Context) {
        release()
        player = ExoPlayer.Builder(context).build()
        player?.apply {
            addListener(PlayerListener())
        }
    }

    fun setAndPlay(path: String) {
        player?.apply {
            Log.d("TAG1", "setAndPlay: $path")
            clearMediaItems()
            setMediaItem(MediaItem.fromUri(path))//"file://$path"
            playWhenReady = false
            prepare()
        }
    }


    fun release() {
        player?.release()
        player?.removeListener(PlayerListener())
        player = null
        outputAudioPath = ""
        audioPath = ""
        song = null
    }

    private inner class PlayerListener : Player.Listener {
        override fun onEvents(player: Player, events: Player.Events) {
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

    private fun updateMusicState(player: Player) = with(player) {
        _musicState.update {
            it.copy(
//                currentSong = currentMediaItem.asSong(),
                playbackState = playbackState.asPlaybackState(),
                playWhenReady = playWhenReady,
                duration = duration.orDefaultTimestamp()
            )
        }
    }

    override fun onCleared() {
        super.onCleared()
        release()
    }

    fun delete() {
        val file = File(outputAudioPath)
        if (file.exists()) file.delete()
    }
}