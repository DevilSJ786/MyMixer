package com.media.mixer.data.di

import com.media.mixer.data.sync.LocalMediaInfoSynchronizer
import com.media.mixer.data.sync.MediaSynchronizer
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import com.media.mixer.data.sync.LocalMediaSynchronizer
import com.media.mixer.data.sync.MediaInfoSynchronizer
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
interface MediaModule {

    @Binds
    @Singleton
    fun bindsMediaSynchronizer(
        mediaSynchronizer: LocalMediaSynchronizer
    ): MediaSynchronizer

    @Binds
    @Singleton
    fun bindsMediaInfoSynchronizer(
        mediaInfoSynchronizer: LocalMediaInfoSynchronizer
    ): MediaInfoSynchronizer
}
