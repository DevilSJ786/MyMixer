package com.media.mixer.data

import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import androidx.room.migration.Migration
import androidx.sqlite.db.SupportSQLiteDatabase
import com.media.mixer.data.converter.Converters
import com.media.mixer.data.dao.DirectoryDao
import com.media.mixer.data.dao.MediumDao
import com.media.mixer.data.dao.UserDao
import com.media.mixer.data.entities.DirectoryEntity
import com.media.mixer.data.entities.MediumEntity
import com.media.mixer.data.entities.Playlist
import com.media.mixer.data.entities.Song

@Database(
    entities = [
        DirectoryEntity::class,
        MediumEntity::class,
        Playlist::class,
        Song::class
    ],
    version = 1,
    exportSchema = true,
)
@TypeConverters(Converters::class)
abstract class MediaDatabase : RoomDatabase() {

    abstract fun mediumDao(): MediumDao

    abstract fun directoryDao(): DirectoryDao

    abstract fun userDao(): UserDao

    companion object {
        const val DATABASE_NAME = "media_db"
    }
}
