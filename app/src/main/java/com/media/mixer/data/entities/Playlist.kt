package com.media.mixer.data.entities

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "playlist")
data class Playlist(@PrimaryKey(autoGenerate = true) val id: Long=0, val name: String, val count: Int,val isVideoPlaylist:Boolean)
