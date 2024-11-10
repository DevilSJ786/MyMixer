package com.media.mixer.domain.repository

import com.media.mixer.data.entities.Playlist
import com.media.mixer.data.entities.Song
import kotlinx.coroutines.flow.Flow

interface LocalRepository {
    suspend fun insertPlaylist(playlist: Playlist)
    fun getPlaylist(isVideo:Boolean): Flow<List<Playlist>>
    suspend fun getSinglePlaylist(id:Long): Playlist?
    suspend fun updatePlaylistCount(id: Long,count:Int)
    suspend fun insertSong(song: Song)
    fun getPlaylistSong(): Flow<List<Song>>
    suspend fun updateSongFav(id: Int,fav:Boolean)
    suspend fun deletePlaylist(playlist: Playlist)
}