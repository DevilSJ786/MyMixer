package com.media.mixer.media.domain.model

import android.net.Uri

data class MediaAudio(
    val id: Long,
    val uri: Uri,
    val data: String,
    val duration: Long,
    val title: String,
    val artist: String,
    val art: Uri,
    val albumName:String,
    val albumId:Long,
    val size:String,
    val isFev:Boolean,
    val type:String
)
