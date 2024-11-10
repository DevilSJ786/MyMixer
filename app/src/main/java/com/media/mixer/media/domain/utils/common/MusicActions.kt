package com.media.mixer.media.domain.utils.common

import android.content.Context
import androidx.media3.common.Player
import androidx.media3.common.util.UnstableApi
import androidx.media3.session.MediaNotification
import androidx.media3.session.MediaSession
import com.media.mixer.R
import com.media.mixer.media.domain.utils.AppIcons

@UnstableApi
internal object MusicActions {
    internal fun getSkipPreviousAction(
        context: Context,
        mediaSession: MediaSession,
        actionFactory: MediaNotification.ActionFactory
    ) = MusicAction(
        iconResource = AppIcons.SkipPrevious.resourceId,
        titleResource = R.string.skip_previous,
        command = Player.COMMAND_SEEK_TO_PREVIOUS
    ).asNotificationAction(context, mediaSession, actionFactory)

    internal fun getPlayPauseAction(
        context: Context,
        mediaSession: MediaSession,
        actionFactory: MediaNotification.ActionFactory,
        playWhenReady: Boolean
    ) = MusicAction(
        iconResource = if (playWhenReady) AppIcons.Pause.resourceId else AppIcons.Play.resourceId,
        titleResource = if (playWhenReady) R.string.pause else R.string.play,
        command = Player.COMMAND_PLAY_PAUSE
    ).asNotificationAction(context, mediaSession, actionFactory)

    internal fun getSkipNextAction(
        context: Context,
        mediaSession: MediaSession,
        actionFactory: MediaNotification.ActionFactory
    ) = MusicAction(
        iconResource = AppIcons.SkipNext.resourceId,
        titleResource = R.string.skip_next,
        command = Player.COMMAND_SEEK_TO_NEXT
    ).asNotificationAction(context, mediaSession, actionFactory)
    internal fun getSkipForwardAction(
        context: Context,
        mediaSession: MediaSession,
        actionFactory: MediaNotification.ActionFactory
    ) = MusicAction(
        iconResource = AppIcons.SkipForward.resourceId,
        titleResource = R.string.skip_next,
        command = Player.COMMAND_SEEK_FORWARD
    ).asNotificationAction(context, mediaSession, actionFactory)
    internal fun getSkipBackAction(
        context: Context,
        mediaSession: MediaSession,
        actionFactory: MediaNotification.ActionFactory
    ) = MusicAction(
        iconResource = AppIcons.SkipBack.resourceId,
        titleResource = R.string.skip_next,
        command = Player.COMMAND_SEEK_BACK
    ).asNotificationAction(context, mediaSession, actionFactory)
}
