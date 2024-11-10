package com.media.mixer.media.domain.utils

import android.content.Context
import android.content.ContextWrapper
import androidx.activity.ComponentActivity
import androidx.media3.common.C
import androidx.media3.common.Player
import androidx.media3.common.TrackGroup
import androidx.media3.common.TrackSelectionOverride
import androidx.media3.common.util.UnstableApi
import androidx.media3.exoplayer.ExoPlayer
import androidx.media3.exoplayer.SeekParameters
import com.media.mixer.media.media.ResizeMode
import java.util.Locale

fun Context.findActivity(): ComponentActivity? {
    var context = this
    while (context is ContextWrapper) {
        if (context is ComponentActivity) return context
        context = context.baseContext
    }
    return null
}

fun Player.getCurrentTrackIndex(type: @C.TrackType Int): Int {
    return currentTracks.groups
        .filter { it.type == type && it.isSupported }
        .indexOfFirst { it.isSelected }
}
