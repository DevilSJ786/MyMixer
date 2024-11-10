package com.media.mixer.screens.ringtones

import android.content.Context
import android.util.Log
import androidx.compose.runtime.mutableStateListOf
import androidx.core.net.toUri
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.media3.common.MediaItem
import androidx.media3.common.Player
import androidx.media3.exoplayer.ExoPlayer
import com.media.mixer.core.utils.StoragePath
import com.media.mixer.core.utils.TransformerState
import com.media.mixer.core.utils.TrimItem
import com.media.mixer.core.utils.createBundle
import com.media.mixer.core.utils.createExternalFile
import com.media.mixer.core.utils.getAudioEndPoint
import com.media.mixer.core.utils.getAudioMemeType
import com.media.mixer.core.utils.startExport
import com.media.mixer.core.utils.startExportForMerger
import com.media.mixer.data.entities.Song
import com.media.mixer.media.audio.common.MusicState
import com.media.mixer.media.domain.utils.asPlaybackState
import com.media.mixer.media.domain.utils.orDefaultTimestamp
import com.media.mixer.screens.videotoaudio.RingtoneUiState
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
class RingtonesViewModel @Inject constructor(@ApplicationContext private val context: Context) :
    ViewModel() {
    var finalOutput: String = ""
    var player: Player? = null
    private var outputMimeType: String = ""
    private var audioPathList = mutableListOf<String>()
    private var outputAudioPath = mutableListOf<File>()

    private val _musicState = MutableStateFlow(MusicState())
    val musicState = _musicState.asStateFlow()

    private val _ringtone = MutableStateFlow<RingtoneUiState>(RingtoneUiState.RingtoneHome)
    val ringtone = _ringtone.asStateFlow()

    private val _transformerState = MutableStateFlow<TransformerState>(TransformerState.Loading)
    val transformerState: StateFlow<TransformerState> = _transformerState.asStateFlow()

    private val _selectedSong = mutableStateListOf<Song>()
    var selectedSong = MutableStateFlow(_selectedSong)
    var currentSong: Song? = null


    init {
        createPlayer(context)
    }

    fun setAudioPath(list: List<Song>) {
        _selectedSong.clear()
        _selectedSong += list
        audioPathList.addAll(list.map { it.mediaUri.toString() })
        Log.d("TAG", "setAudioPath:audioPathList ${audioPathList}")
        setAndPlay(list.first().mediaUri.toString())
    }

    fun updateUiState(state: RingtoneUiState) {
        _ringtone.value = state
    }

    fun startAudioCutting(
        rangeList: List<ClosedFloatingPointRange<Float>>,
        context: Context,
    ) {
        Log.d("TAG", "startAudioCutting: ${rangeList.size}")
        val trimItemLists = mutableListOf<TrimItem>()
        audioPathList.forEachIndexed { index, uri ->
            trimItemLists.add(
                TrimItem(
                    uri,
                    rangeList[index].start.toLong(),
                    rangeList[index].endInclusive.toLong()
                )
            )
        }
        viewModelScope.launch {
            startAudioExport(
                context = context,
                trimItemLists = trimItemLists,
                format = "MP3",
                title = "${currentSong?.title}Merge${System.currentTimeMillis()}",
                storagePath = StoragePath.MARGER
            )
        }
    }


    private fun startAudioExport(
        context: Context,
        trimItemLists: List<TrimItem>,
        title: String = "Output",
        storagePath: StoragePath,
        format: String,
    ) {
        outputMimeType = getAudioMemeType(format)
        startExportForMerger(
            trimItemLists = trimItemLists,
            bundle = createBundle(
                removeAudio = false,
                removeVideo = false,
                selectedAudioMimeType = outputMimeType
            ),
            context = context,
            outputFile = createExternalFile(
                fileName = title + ".${getAudioEndPoint(format)}",
                fileStorage = storagePath.path,
                context = context
            ),
            onStart = {
                Log.d("TAG", "startAudioExport: ")
                _transformerState.value=TransformerState.Loading
            },
            onCompletion = { outputfilePath ->
                delete()
                finalOutput = outputfilePath
                currentSong = currentSong?.copy(mediaUri = File(outputfilePath).toUri())
                Log.i(
                    "TAG",
                    "doneAudioExport: $outputfilePath, currentSong:${File(outputfilePath).toUri()},$outputAudioPath"
                )
                setAndPlay("file://${outputfilePath}")
                _transformerState.value=TransformerState.Success
            },
            onException = {
                _transformerState.value=TransformerState.Error
            })
    }

    fun startAudioExportTrim(
        context: Context,
        file: String,
        title: String = "Output",
        storagePath: StoragePath,
        format: String,
        start: Long,
        end: Long
    ) {
        outputMimeType = getAudioMemeType(format)

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
                fileName = title + "trim${(0..9999).random()}" + ".${getAudioEndPoint(format)}",
                fileStorage = storagePath.path,
                context = context
            ),
            onStart = {
                Log.d("TAG", "startAudioExport: ")
                _transformerState.value=TransformerState.Loading
            },
            onCompletion = { outputfilePath ->
                delete()
                finalOutput = outputfilePath
                currentSong = currentSong?.copy(mediaUri = File(outputfilePath).toUri())
                Log.i(
                    "TAG",
                    "doneAudioExport: $outputfilePath, currentSong:${File(outputfilePath).toUri()},$outputAudioPath"
                )
                setAndPlay("file://$finalOutput")
                _transformerState.value=TransformerState.Success
            },
            onException = {
                _transformerState.value=TransformerState.Error
            })
    }

    fun delete() {
        val file = File(finalOutput)
        if (file.exists()) file.delete()
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
        outputAudioPath.clear()
        audioPathList.clear()
        _selectedSong.clear()
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

    fun removeAt(it: Int) {
        _selectedSong.removeAt(it)
    }
}