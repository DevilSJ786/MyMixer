package com.media.mixer.media.audio

import android.annotation.SuppressLint
import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.util.Log
import androidx.media3.common.util.UnstableApi
import androidx.media3.session.MediaBrowser
import androidx.media3.session.SessionToken
import androidx.work.await
import com.media.mixer.media.domain.utils.MediaConstants.DEFAULT_POSITION_MS
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.currentCoroutineContext
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.isActive
import kotlinx.coroutines.launch
import javax.inject.Inject
import javax.inject.Singleton

@SuppressLint("RestrictedApi")
@UnstableApi
@Singleton
class MusicServiceConnection @Inject constructor(
    @ApplicationContext context: Context,
) {
    private var mediaBrowser: MediaBrowser? = null
    private val coroutineScope = CoroutineScope(Dispatchers.Main + SupervisorJob())



//    val currentPosition = flow {
//        while (currentCoroutineContext().isActive) {
//            val currentPosition = mediaBrowser?.currentPosition ?: DEFAULT_POSITION_MS
//            emit(currentPosition)
//            delay(POSITION_UPDATE_INTERVAL_MS)
//        }
//    }
    private val _currentProgress = MutableSharedFlow<Long>()
//    val currentProgress = _currentProgress.asSharedFlow()

    init {
        Log.d("TAG", "init:start $mediaBrowser")
        coroutineScope.launch {
                mediaBrowser = MediaBrowser.Builder(
                    context,
                    SessionToken(context, ComponentName(context, MusicService::class.java))
                ).buildAsync().await()
                while (currentCoroutineContext().isActive) {
                    val process = (mediaBrowser?.contentPosition?.div(1000) ?: 1) / 60
                    _currentProgress.emit(process)
                    delay(60000)
                }
        }
    }

    fun release(context: Context) {
        mediaBrowser?.release()
        val stopIntent = Intent(context, MusicService::class.java)
        context.stopService(stopIntent)
    }

    fun skipPrevious() = mediaBrowser?.run {
        seekToPrevious()
        play()
    }

    fun forward() = mediaBrowser?.run {
        seekForward()
    }

    fun backward() = mediaBrowser?.run {
        seekBack()
    }

    fun play() = mediaBrowser?.play()
    fun pause() = mediaBrowser?.pause()

    fun skipNext() = mediaBrowser?.run {
        seekToNext()
        play()
    }

    fun skipTo(position: Long) = mediaBrowser?.run {
        seekTo(position)
        play()
    }

//    fun playSongs(
//        songs: List<Song>,
//        startIndex: Int = DEFAULT_INDEX,
//        startPositionMs: Long = DEFAULT_POSITION_MS
//    ) {
//        mediaBrowser?.run {
//            clearMediaItems()
//            setMediaItems(songs.map(Song::asMediaItem), startIndex, startPositionMs)
//            prepare()
//            playWhenReady = true
//        }
//    }


    fun playIndex(index: Int) {
        mediaBrowser?.seekTo(index, DEFAULT_POSITION_MS)
    }

}
