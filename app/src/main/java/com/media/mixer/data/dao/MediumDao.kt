package com.media.mixer.data.dao

import androidx.room.Dao
import androidx.room.Query
import androidx.room.Upsert
import com.media.mixer.data.entities.MediumEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface MediumDao {

    @Upsert
    suspend fun upsert(medium: MediumEntity)

    @Query("UPDATE media SET isFev=:fav WHERE media_store_id=:id")
    suspend fun updateVideoFav(id: Long,fav:Boolean)

    @Query("UPDATE media SET playlistMap=:map WHERE media_store_id=:id")
    suspend fun updateVideoPlaylist(id: Long,map:Map<Long,Boolean>)

    @Upsert
    suspend fun upsertAll(media: List<MediumEntity>)

    @Query("SELECT * FROM media WHERE uri = :uri")
    suspend fun get(uri: String): MediumEntity?

    @Query("SELECT * FROM media")
    fun getAll(): Flow<List<MediumEntity>>

    @Query("SELECT * FROM media WHERE parent_path = :directoryPath")
    fun getAllFromDirectory(directoryPath: String): Flow<List<MediumEntity>>


    @Query("SELECT * FROM media WHERE uri = :uri")
    suspend fun getWithInfo(uri: String): MediumEntity?


    @Query("SELECT * FROM media")
    fun getAllWithInfo(): Flow<List<MediumEntity>>


    @Query("SELECT * FROM media WHERE parent_path = :directoryPath")
    fun getAllWithInfoFromDirectory(directoryPath: String): Flow<List<MediumEntity>>

    @Query("DELETE FROM media WHERE uri in (:uris)")
    suspend fun delete(uris: List<String>)

    @Query("UPDATE OR REPLACE media SET playback_position = :position, audio_track_index = :audioTrackIndex, playback_speed = :playbackSpeed, last_played_time = :lastPlayedTime WHERE uri = :uri")
    suspend fun updateMediumState(
        uri: String,
        position: Long,
        audioTrackIndex: Int?,
        playbackSpeed: Float?,
        lastPlayedTime: Long?
    )
}
