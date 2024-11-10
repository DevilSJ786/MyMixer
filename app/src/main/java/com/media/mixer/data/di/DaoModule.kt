package com.media.mixer.data.di

import com.media.mixer.data.MediaDatabase
import com.media.mixer.data.dao.DirectoryDao
import com.media.mixer.data.dao.MediumDao
import com.media.mixer.data.dao.UserDao
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent

@Module
@InstallIn(SingletonComponent::class)
object DaoModule {

    @Provides
    fun provideMediumDao(db: MediaDatabase): MediumDao = db.mediumDao()

    @Provides
    fun provideDirectoryDao(db: MediaDatabase): DirectoryDao = db.directoryDao()

    @Provides
    fun provideUserDao(db: MediaDatabase): UserDao = db.userDao()
}
