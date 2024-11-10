package com.media.mixer.domain.mappers

import com.media.mixer.core.utils.Utils
import com.media.mixer.data.entities.MediumEntity
import com.media.mixer.domain.model.Video
import java.util.Date

fun MediumEntity.toVideo() = Video(
    id = mediaStoreId,
    path = path,
    parentPath = parentPath,
    duration = duration,
    uriString = uriString,
    displayName = name.substringBeforeLast("."),
    nameWithExtension = name,
    width = width,
    height = height,
    size = size,
    dateModified = modified,
    format = format,
    thumbnailPath = thumbnailPath,
    lastPlayedAt = lastPlayedTime?.let { Date(it) },
    formattedDuration = Utils.formatDurationMillis(duration),
    formattedFileSize = Utils.formatFileSize(size),
    isFev = isFev,
    playlistMap = playlistMap
)

