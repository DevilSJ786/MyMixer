package com.media.mixer.screens.recordings

import android.content.Context
import android.net.Uri
import android.util.Log
import androidx.compose.runtime.mutableStateListOf
import androidx.documentfile.provider.DocumentFile
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.media3.common.MediaItem
import androidx.media3.common.PlaybackException
import androidx.media3.common.Player
import androidx.media3.exoplayer.ExoPlayer
import com.media.mixer.core.utils.StoragePath
import com.media.mixer.core.utils.getAvailableFiles
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.io.IOException
import javax.inject.Inject

@HiltViewModel
class PlayerViewModel @Inject constructor() : ViewModel() {
    private var player: Player? = null
    private val playbackStateListener: Player.Listener = playbackStateListener()

    val files = mutableStateListOf<DocumentFile>()
    val ringtones= MutableStateFlow(files)
    private var onFinish: () -> Unit = {}


    fun loadVoiceRecordingFiles(context: Context, path: String) {
        viewModelScope.launch {
            withContext(Dispatchers.IO) {
                val f = getAvailableFiles(context, path)
                files.clear()
                files.addAll(f)
            }
        }
    }
   fun loadAllFiles(context:Context){
       files.clear()
       viewModelScope.launch {
           withContext(Dispatchers.Main) {
               val f = getAvailableFiles(context, StoragePath.RINGTONE.path)
               files.addAll(f)
           }
       }
       viewModelScope.launch {
           withContext(Dispatchers.Main) {
               val f = getAvailableFiles(context, StoragePath.NOTIFICATIONS.path)
               files.addAll(f)
           }
       }
       viewModelScope.launch {
           withContext(Dispatchers.Main) {
               val f = getAvailableFiles(context, StoragePath.ALARM.path)
               files.addAll(f)
           }
       }
    }

    private fun playbackStateListener() = object : Player.Listener {
        override fun onPlaybackStateChanged(playbackState: Int) {
            super.onPlaybackStateChanged(playbackState)
            if (playbackState == Player.STATE_ENDED) {
                onFinish.invoke()
            }
        }

        override fun onPlayerError(error: PlaybackException) {
            super.onPlayerError(error)
            onFinish.invoke()
        }
    }

    fun startPlaying(context: Context, file: Uri, onEnded: () -> Unit) {
        onFinish.invoke()
        onFinish = onEnded

        stopPlaying()

        player = getMediaPlayer(context).apply {
            try {
                setMediaItem(MediaItem.fromUri(file))
                prepare()
                playWhenReady = true
            } catch (e: IOException) {
                Log.e("reading file", e.toString())
            }
        }
        player?.addListener(playbackStateListener)
    }

    fun stopPlaying() {
        player?.removeListener(playbackStateListener)
        player?.release()
        player = null
    }

    private fun getMediaPlayer(context: Context): Player {
        return ExoPlayer.Builder(context).build()
    }
}