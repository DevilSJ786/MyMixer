package com.media.mixer.core.utils

import android.content.Context
import android.media.MediaRecorder
import android.os.Build
import androidx.media3.common.AudioAttributes
import androidx.media3.common.C

object PlayerHelper {
    fun newRecorder(context: Context): MediaRecorder {
        return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
            MediaRecorder(context)
        } else {
            @Suppress("DEPRECATION")
            (MediaRecorder())
        }
    }

    fun getAudioAttributes(): AudioAttributes = AudioAttributes.Builder()
        .setContentType(C.AUDIO_CONTENT_TYPE_MUSIC)
        .setUsage(C.USAGE_MEDIA)
        .build()
}
