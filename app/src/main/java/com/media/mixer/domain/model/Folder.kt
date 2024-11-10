package com.media.mixer.domain.model

import java.io.Serializable

data class Folder(
    val name: String,
    val path: String,
    val mediaSize: Long,
    val mediaCount: Int,
    val dateModified: Long,
    val formattedMediaSize: String = "",
    val mediaList: List<Video> = emptyList()
) : Serializable
