package com.media.mixer.di

import android.content.Context
import androidx.annotation.OptIn
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.media3.common.AudioAttributes
import androidx.media3.common.C
import androidx.media3.common.Player
import androidx.media3.common.util.UnstableApi
import androidx.media3.datasource.DefaultDataSource
import androidx.media3.exoplayer.ExoPlayer
import androidx.media3.exoplayer.source.ProgressiveMediaSource
import com.media.mixer.core.utils.dataStore
import com.media.mixer.data.dao.UserDao
import com.media.mixer.data.repository.LocalRepositoryImp
import com.media.mixer.domain.repository.LocalRepository
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import io.github.anilbeesetti.nextlib.media3ext.ffdecoder.NextRenderersFactory
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object AppModule {

    @OptIn(UnstableApi::class)
    @Provides
    @Singleton
    fun providesPlayer(@ApplicationContext context: Context): Player {
        val audioAttributes = AudioAttributes.Builder()
            .setContentType(C.AUDIO_CONTENT_TYPE_MOVIE)
            .setUsage(C.USAGE_MEDIA)
            .build()
        val renderersFactory = NextRenderersFactory(context)
            .setEnableDecoderFallback(true)
        return ExoPlayer.Builder(context)
            .setRenderersFactory(renderersFactory)
//            .setMediaSourceFactory(ProgressiveMediaSource.Factory(DefaultDataSource.Factory(context)))
            .setAudioAttributes(audioAttributes, true)
            .setHandleAudioBecomingNoisy(true)
            .setWakeMode(C.WAKE_MODE_LOCAL)
            .build()
    }


    @Provides
    @Singleton
    fun provideLocalRepo(userDao: UserDao): LocalRepository {
       return LocalRepositoryImp(userDao)
    }
    @Provides
    @Singleton
    fun providePreferences(@ApplicationContext context:Context): DataStore<Preferences> {
        return context.dataStore
    }
}