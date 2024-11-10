package com.media.mixer.media.domain.utils.common

import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.graphics.Bitmap
import android.net.Uri
import android.os.Build
import android.os.Bundle
import androidx.core.app.NotificationCompat
import androidx.core.content.getSystemService
import androidx.media3.common.util.UnstableApi
import androidx.media3.session.CommandButton
import androidx.media3.session.MediaNotification
import androidx.media3.session.MediaSession
import androidx.media3.session.MediaStyleNotificationHelper.MediaStyle
import com.google.common.collect.ImmutableList
import com.media.mixer.R
import com.media.mixer.screens.audioplayer.AudioActivity
import com.media.mixer.media.domain.utils.AppIcons
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.cancel
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import javax.inject.Inject

@UnstableApi
class MusicNotificationProvider @Inject constructor(
    @ApplicationContext private val context: Context,
) : MediaNotification.Provider {
    private val notificationManager = checkNotNull(context.getSystemService<NotificationManager>())
    private val coroutineScope = CoroutineScope(Dispatchers.Main + SupervisorJob())

    override fun createNotification(
        session: MediaSession,
        customLayout: ImmutableList<CommandButton>,
        actionFactory: MediaNotification.ActionFactory,
        onNotificationChangedCallback: MediaNotification.Provider.Callback
    ): MediaNotification {
        ensureNotificationChannel()

        val player = session.player
        val metadata = player.mediaMetadata
        val intent = Intent(context, AudioActivity::class.java)
       val pendingIntent =
            PendingIntent.getActivity(context, 0, intent,  PendingIntent.FLAG_IMMUTABLE)
        val builder = NotificationCompat.Builder(context, MusicNotificationChannelId)
            .setContentTitle(metadata.title)
            .setContentText(metadata.artist)
            .setSmallIcon(AppIcons.Music.resourceId)
            .setStyle(MediaStyle(session))
            .setContentIntent(pendingIntent)

        getNotificationActions(
            mediaSession = session,
            actionFactory = actionFactory,
            playWhenReady = player.playWhenReady
        ).forEach(builder::addAction)
        setupArtwork(
            uri = metadata.artworkUri,
            setLargeIcon = builder::setLargeIcon,
            updateNotification = {
                val notification = MediaNotification(MusicNotificationId, builder.build())
                onNotificationChangedCallback.onNotificationChanged(notification)
            }
        )

        return MediaNotification(MusicNotificationId, builder.build())
    }

    override fun handleCustomCommand(session: MediaSession, action: String, extras: Bundle) = false

    fun cancelCoroutineScope() = coroutineScope.cancel()

    private fun ensureNotificationChannel() {
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.O ||
            notificationManager.getNotificationChannel(MusicNotificationChannelId) != null
        ) {
            return
        }

        val notificationChannel = NotificationChannel(
            MusicNotificationChannelId,
            context.getString(R.string.music_notification_channel_name),
            NotificationManager.IMPORTANCE_LOW
        )
        notificationManager.createNotificationChannel(notificationChannel)
    }

    private fun getNotificationActions(
        mediaSession: MediaSession,
        actionFactory: MediaNotification.ActionFactory,
        playWhenReady: Boolean
    ) = listOf(
        MusicActions.getSkipBackAction(context, mediaSession, actionFactory),
        MusicActions.getSkipPreviousAction(context, mediaSession, actionFactory),
        MusicActions.getPlayPauseAction(context, mediaSession, actionFactory, playWhenReady),
        MusicActions.getSkipNextAction(context, mediaSession, actionFactory),
        MusicActions.getSkipForwardAction(context, mediaSession, actionFactory),
    )

    private fun setupArtwork(
        uri: Uri?,
        setLargeIcon: (Bitmap?) -> Unit,
        updateNotification: () -> Unit
    ) = coroutineScope.launch {
        val bitmap = loadArtworkBitmap(uri)
        val bitmap1 =loadArtworkBitmap(Uri.parse("https://i.pinimg.com/736x/4b/02/1f/4b021f002b90ab163ef41aaaaa17c7a4.jpg"))
        setLargeIcon(bitmap?:bitmap1)
        updateNotification()
    }

    private suspend fun loadArtworkBitmap(uri: Uri?) =
        withContext(Dispatchers.IO) { uri?.asArtworkBitmap(context) }

}

private const val MusicNotificationId = 1001
private const val MusicNotificationChannelId = "MusicNotificationChannel"
