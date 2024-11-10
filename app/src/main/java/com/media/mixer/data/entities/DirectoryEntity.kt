package com.media.mixer.data.entities

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(
    tableName = "directories"
)
data class DirectoryEntity(
    @PrimaryKey
    @ColumnInfo(name = "path")
    val path: String,
    @ColumnInfo(name = "filename") val name: String,
    @ColumnInfo(name = "last_modified") val modified: Long
)
