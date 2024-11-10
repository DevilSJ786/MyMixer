package com.media.mixer.domain.model

import java.io.Serializable
import java.util.Date

data class Video(
    val id: Long,
    val path: String,
    val parentPath: String = "",
    val duration: Long,
    val uriString: String,
    val displayName: String,
    val nameWithExtension: String,
    val width: Int,
    val height: Int,
    val size: Long,
    val dateModified: Long = 0,
    val formattedDuration: String = "",
    val formattedFileSize: String = "",
    val format: String? = null,
    val thumbnailPath: String? = null,
    val lastPlayedAt: Date? = null,
    val isFev: Boolean=false,
    val playlistMap: Map<Long,Boolean> = emptyMap(),
) : Serializable
