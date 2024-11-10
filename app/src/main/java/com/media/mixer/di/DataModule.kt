package com.media.mixer.di

import com.media.mixer.data.repository.LocalMediaRepository
import com.media.mixer.domain.repository.MediaRepository
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent

@Module
@InstallIn(SingletonComponent::class)
interface DataModule {

    @Binds
    fun bindsMediaRepository(
        videoRepository: LocalMediaRepository
    ): MediaRepository

}
