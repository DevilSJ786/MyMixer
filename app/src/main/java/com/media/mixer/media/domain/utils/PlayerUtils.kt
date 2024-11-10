package com.media.mixer.media.domain.utils

import androidx.media3.common.C
import androidx.media3.common.Player
import com.media.mixer.media.audio.common.PlaybackState
import com.media.mixer.media.domain.utils.MediaConstants.DEFAULT_DURATION_MS

internal fun Int.asPlaybackState() = when (this) {
    Player.STATE_IDLE -> PlaybackState.IDLE
    Player.STATE_BUFFERING -> PlaybackState.BUFFERING
    Player.STATE_READY -> PlaybackState.READY
    Player.STATE_ENDED -> PlaybackState.ENDED
    else -> error("Invalid playback state.")
}

internal fun Long.orDefaultTimestamp() = takeIf { it != C.TIME_UNSET } ?: DEFAULT_DURATION_MS
