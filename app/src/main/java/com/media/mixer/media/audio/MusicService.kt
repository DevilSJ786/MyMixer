package com.media.mixer.media.audio

import android.content.Intent
import android.util.Log
import androidx.media3.common.Player
import androidx.media3.common.util.UnstableApi
import androidx.media3.session.MediaLibraryService
import androidx.media3.session.MediaSession
import com.media.mixer.media.domain.utils.common.MusicNotificationProvider
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

@UnstableApi @AndroidEntryPoint
class MusicService : MediaLibraryService() {
    private var mediaLibrarySession: MediaLibrarySession? = null

    @Inject
    lateinit var musicSessionCallback: MusicSessionCallback

    @Inject
    lateinit var musicNotificationProvider: MusicNotificationProvider

    @Inject
    lateinit var player: Player

    override fun onCreate() {
        super.onCreate()
        Log.d("TAG", "onCreate: MusicService session$mediaLibrarySession")
        mediaLibrarySession =
            MediaLibrarySession.Builder(this, player, musicSessionCallback).build()

        setMediaNotificationProvider(musicNotificationProvider)
    }

    // The user dismissed the app from the recent tasks
    override fun onTaskRemoved(rootIntent: Intent?) {
        val player = mediaLibrarySession?.player!!
        if (!player.playWhenReady
            || player.mediaItemCount == 0
            || player.playbackState == Player.STATE_ENDED) {
            // Stop the service if not playing, continue playing in the background
            // otherwise
            stopForeground(STOP_FOREGROUND_DETACH)
            stopSelf()
        }
    }

    override fun onGetSession(controllerInfo: MediaSession.ControllerInfo) = mediaLibrarySession
    override fun onDestroy() {
        super.onDestroy()
        mediaLibrarySession?.run {
            player.release()
            release()
            mediaLibrarySession = null
        }
        musicSessionCallback.cancelCoroutineScope()
        musicNotificationProvider.cancelCoroutineScope()
    }
}
