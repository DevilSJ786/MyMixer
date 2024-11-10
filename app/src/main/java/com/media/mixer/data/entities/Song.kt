package com.media.mixer.data.entities

import android.net.Uri
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "song")
data class Song(
 @PrimaryKey(autoGenerate = false) val id: Int,
 val mediaUri: Uri,
 val artworkUri: Uri,
 val title: String,
 val artist: String,
 val duration: Int,
 val size: String="",
 val isFev: Boolean=false,
 val album:String,
 val playlistMap: Map<Long,Boolean> = emptyMap(),
 val path:String,
 val type:String
)
