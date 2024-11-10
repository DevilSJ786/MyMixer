package com.media.mixer.data.dao

import androidx.room.Dao
import androidx.room.Query
import androidx.room.Upsert
import com.media.mixer.data.entities.DirectoryEntity
import com.media.mixer.data.relations.DirectoryWithMedia
import kotlinx.coroutines.flow.Flow

@Dao
interface DirectoryDao {

    @Upsert
    suspend fun upsert(directory: DirectoryEntity)

    @Upsert
    suspend fun upsertAll(directories: List<DirectoryEntity>)

    @Query("SELECT * FROM directories")
    fun getAll(): Flow<List<DirectoryEntity>>

    @Query("SELECT * FROM directories")
    fun getAllWithMedia(): Flow<List<DirectoryWithMedia>>

    @Query("DELETE FROM directories WHERE path in (:paths)")
    suspend fun delete(paths: List<String>)
}
