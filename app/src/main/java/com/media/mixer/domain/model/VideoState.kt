package com.media.mixer.domain.model

data class VideoState(
    val path: String,
    val position: Long,
    val audioTrackIndex: Int?,
    val subtitleTrackIndex: Int?,
    val playbackSpeed: Float?
)
