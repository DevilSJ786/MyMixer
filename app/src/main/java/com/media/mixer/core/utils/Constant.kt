package com.media.mixer.core.utils

import android.content.Context
import android.content.Intent
import android.media.RingtoneManager
import android.net.Uri
import android.provider.Settings
import android.util.Log
import androidx.compose.ui.graphics.Color
import androidx.core.net.toUri
import java.io.File


val listOfColor = listOf(
    Color(0xFFFE53BB),
    Color(0xFF6B0242),
)
val listOfColorForBorder = listOf(
    Color(0xFFFE53BB),
    Color(0xFF09FBD3),
)
val listOfColorForPlayer = listOf(
    Color(0xFF0A927C),
    Color(0xFF076B5F)
   )
val listOfColorForLine = listOf(
    Color(0xFFDDDDDD).copy(alpha = 0f),
    Color(0xFFFFFFFF).copy(alpha = 0.5f),
    Color(0xFFDDDDDD).copy(alpha = 0f)
)
val listOfColorForBottomBar = listOf(
    Color(0xFF643454),
    Color(0xFF302730),
)
val listOfColorForBottomBarStroke = listOf(
    Color(0xFF934B7B),
    Color(0xFF443842),
)

val colorOfDropDown=Color(0xFF3D4848)
val listOfAudioFormat = listOf(
    "MP3",
    "AAC",
    "FLAC"
)
val listOfAudioQuality = listOf("32Kbps", "64Kbps", "128Kbps", "192Kbps", "256Kbps", "320Kbps")

val START_ACTIVITY = "newUser"


/***
 * Convert the millisecond to String text
 */
fun Long.convertToText(): String {
    val sec = this / 1000
    val minutes = sec / 60
    val seconds = sec % 60

    val minutesString = if (minutes < 10) {
        "0$minutes"
    } else {
        minutes.toString()
    }
    val secondsString = if (seconds < 10) {
        "0$seconds"
    } else {
        seconds.toString()
    }
    return "$minutesString:$secondsString"
}

fun setRingtoneManageTone(
    context: Context,
    path: String,
    isRingtone: Boolean = true,
    isNotification: Boolean = false,
    isAlarm: Boolean = false,
) {
    val file = File(path)
    Log.d("TAG", "setRingtoneManageTone: $path,uri ${file.toUri()}")
    val type = if (isAlarm) RingtoneManager.TYPE_ALARM
    else if (isRingtone) RingtoneManager.TYPE_RINGTONE
    else if (isNotification) RingtoneManager.TYPE_NOTIFICATION
    else RingtoneManager.TYPE_ALL
    RingtoneManager.setActualDefaultRingtoneUri(
        context,
        type,
        file.toUri()
    )
}

fun checkSystemWriteSettings(ctx: Context, onGranted: (Boolean) -> Unit) {
    if (!Settings.System.canWrite(ctx)) {
        val intent =
            Intent(Settings.ACTION_MANAGE_WRITE_SETTINGS, Uri.parse("package:" + ctx.packageName))
        ctx.startActivity(intent)
    } else {
        onGranted(true)
    }
}