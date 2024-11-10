package com.media.mixer.data.di

import android.content.Context
import androidx.room.Room
import com.media.mixer.data.MediaDatabase
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object DatabaseModule {

    @Singleton
    @Provides
    fun provideMediaDatabase(
        @ApplicationContext context: Context
    ): MediaDatabase = Room.databaseBuilder(
        context = context,
        klass = MediaDatabase::class.java,
        name = MediaDatabase.DATABASE_NAME
    ).build()
}
