package com.media.mixer.media.domain.utils.common

import android.content.Context
import androidx.compose.runtime.Stable
import androidx.core.graphics.drawable.IconCompat
import androidx.media3.common.util.UnstableApi
import androidx.media3.session.MediaNotification
import androidx.media3.session.MediaSession
import com.media.mixer.media.domain.utils.common.MusicAction

@Stable
@androidx.annotation.OptIn(UnstableApi::class)
internal fun MusicAction.asNotificationAction(
    context: Context,
    mediaSession: MediaSession,
    actionFactory: MediaNotification.ActionFactory
) = actionFactory.createMediaAction(
    mediaSession,
    IconCompat.createWithResource(context, iconResource),
    context.getString(titleResource),
    command
)
