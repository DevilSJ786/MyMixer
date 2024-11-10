package com.media.mixer.data.relations

import androidx.room.Embedded
import androidx.room.Relation
import com.media.mixer.data.entities.DirectoryEntity
import com.media.mixer.data.entities.MediumEntity

data class DirectoryWithMedia(
    @Embedded val directory: DirectoryEntity,
    @Relation(
        parentColumn = "path",
        entityColumn = "parent_path"
    )
    val media: List<MediumEntity>
)
