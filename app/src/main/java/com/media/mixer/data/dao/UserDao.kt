package com.media.mixer.data.dao

import android.net.Uri
import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Query
import androidx.room.Upsert
import com.media.mixer.data.entities.MediumEntity
import com.media.mixer.data.entities.Playlist
import com.media.mixer.data.entities.Song
import kotlinx.coroutines.flow.Flow
import java.net.URI

@Dao
interface UserDao {
    @Upsert(Playlist::class)
    suspend fun insert(playlist: Playlist)

    @Query("UPDATE playlist SET count=:count WHERE id=:id")
    suspend fun updatePlaylistCount(id: Long,count:Int)

    @Query("SELECT * FROM playlist Where isVideoPlaylist=:isVideo")
     fun getPlaylist(isVideo:Boolean): Flow<List<Playlist>>
    @Query("SELECT * FROM playlist Where id=:id")
    fun getSinglePlaylist(id:Long): Playlist?

    @Upsert(Song::class)
    suspend fun insertSong(song: Song)

    @Query("SELECT * FROM song WHERE mediaUri = :uri")
    suspend fun get(uri: Uri): Song?

    @Query("DELETE FROM song WHERE mediaUri in (:uris)")
    suspend fun delete(uris: List<Uri>)
    @Query("SELECT * FROM song ")
    fun getPlaylistSong(): Flow<List<Song>>

    @Upsert
    suspend fun upsertAllAudio(audio: List<Song>)

    @Query("UPDATE song SET isFev=:fav WHERE id=:id")
    suspend fun updateSongFav(id: Int,fav:Boolean)

    @Delete(entity = Playlist::class)
    suspend fun deletePlaylist(playlist: Playlist)
}