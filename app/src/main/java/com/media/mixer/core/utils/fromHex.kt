package com.media.mixer.core.utils

import androidx.compose.ui.graphics.Color
import androidx.media3.common.MimeTypes

fun Color.Companion.fromHex(colorString: String) = Color(android.graphics.Color.parseColor("#$colorString"))

fun getAudioMemeType(format:String):String{
   return when(format){
        "MP3"-> MimeTypes.AUDIO_AAC
        "AAC"->MimeTypes.AUDIO_AAC
        "FLAC"->MimeTypes.AUDIO_AMR_NB
       else -> MimeTypes.AUDIO_AAC
   }
}
fun getAudioEndPoint(format:String):String{
    return when(format){
        "MP3"-> "mp3"
        "AAC"->MimeTypes.AUDIO_AAC.substringAfter("/")
        "FLAC"->MimeTypes.AUDIO_AMR_NB.substringAfter("/")
        else -> MimeTypes.AUDIO_AAC.substringAfter("/")
    }
}

