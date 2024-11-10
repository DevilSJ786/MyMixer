package com.media.mixer.screens.recorder

import android.content.Context
import android.media.MediaRecorder
import android.os.Handler
import android.os.Looper
import android.util.Log
import androidx.compose.runtime.mutableStateListOf
import androidx.lifecycle.ViewModel
import androidx.media3.common.Player
import com.media.mixer.core.utils.PlayerHelper
import com.media.mixer.core.utils.RecorderState
import com.media.mixer.core.utils.StorageHelper
import com.media.mixer.core.utils.StoragePath
import com.media.mixer.core.utils.TransformerState
import com.media.mixer.core.utils.createBundle
import com.media.mixer.core.utils.createExternalFile
import com.media.mixer.core.utils.getAudioEndPoint
import com.media.mixer.core.utils.getAudioMemeType
import com.media.mixer.core.utils.startExport
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import java.io.File


class RecorderViewModel : ViewModel() {
    private var recorder: MediaRecorder? = null
    var outputPath: String = ""
    var player: Player? = null
    private var outputMimeType: String = ""
    private val _uiState = MutableStateFlow<RecorderUiState>(RecorderUiState.StartState)
    val uiState: StateFlow<RecorderUiState> = _uiState.asStateFlow()

    private val _recorderState = MutableStateFlow(RecorderState.IDLE)
    val recorderState: StateFlow<RecorderState> = _recorderState.asStateFlow()

    private var _recordedTime = MutableStateFlow(0L)
    var recordedTime:StateFlow<Long> = _recordedTime.asStateFlow()

    private val _recordedAmplitudes = mutableStateListOf<Int>()
    val recordedAmplitudes = MutableStateFlow(_recordedAmplitudes)


    private val handler = Handler(Looper.getMainLooper())

    fun startAudioRecorder(context: Context) {
        outputPath= StorageHelper.getOutputFileForAudio(context, "m4a", StoragePath.VOICERECORDING.path)
        recorder = PlayerHelper.newRecorder(context).apply {
            setAudioSource(MediaRecorder.AudioSource.MIC)
            setOutputFormat(MediaRecorder.OutputFormat.MPEG_4)
            setAudioEncoder(MediaRecorder.AudioEncoder.DEFAULT)
            setOutputFile(outputPath)

            prepare()
            start()
        }
        _recorderState.value = RecorderState.ACTIVE

        _recordedTime.value = 0L
        handler.postDelayed(this::updateTime, 1000)
        handler.postDelayed(this::updateAmplitude, 100)
    }

    fun stopRecording() {
        _recorderState.value = RecorderState.IDLE
        recorder?.stop()
        recorder?.reset()
        recorder?.release()
        recorder = null
        _recordedTime.value = 0L
        _recordedAmplitudes.clear()

    }
    private val _transformerState = MutableStateFlow<TransformerState>(TransformerState.Loading)
    val transformerState: StateFlow<TransformerState> = _transformerState.asStateFlow()

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
                _transformerState.value= TransformerState.Loading
            },
            onCompletion = { outputfilePath ->
                delete()
                outputPath = outputfilePath
                _transformerState.value= TransformerState.Success
            },
            onException = {
                _transformerState.value= TransformerState.Error
            })
    }
    fun delete(){
        val file =File(outputPath)
        if (file.exists()) file.delete()
    }

    fun pauseRecording() {
        recorder?.pause()
        _recorderState.value = RecorderState.PAUSED
    }

    fun resumeRecording() {
        recorder?.resume()
        _recorderState.value = RecorderState.ACTIVE
        handler.postDelayed(this::updateTime, 1000)
        handler.postDelayed(this::updateAmplitude, 100)
    }

    private fun updateAmplitude() {
        if (_recorderState.value != RecorderState.ACTIVE) return

        recorder?.maxAmplitude?.let {
            if (_recordedAmplitudes.size >= 90) _recordedAmplitudes.removeAt(0)
            _recordedAmplitudes.add(it)
        }

        handler.postDelayed(this::updateAmplitude, 100)
    }

    private fun updateTime() {
        if (_recorderState.value != RecorderState.ACTIVE) return
        _recordedTime.value++
        handler.postDelayed(this::updateTime, 1000)
    }

    fun setUiState(state: RecorderUiState) {
        _uiState.value=state
    }
}
