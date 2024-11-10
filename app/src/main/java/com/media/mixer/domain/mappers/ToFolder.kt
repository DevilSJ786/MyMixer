package com.media.mixer.domain.mappers

import com.media.mixer.core.utils.Utils
import com.media.mixer.data.relations.DirectoryWithMedia
import com.media.mixer.domain.model.Folder

fun DirectoryWithMedia.toFolder() = Folder(
    name = directory.name,
    path = directory.path,
    mediaSize = media.sumOf { it.size },
    mediaCount = media.size,
    dateModified = directory.modified,
    formattedMediaSize = Utils.formatFileSize(media.sumOf { it.size }),
    mediaList = media.map { it.toVideo() }
)
