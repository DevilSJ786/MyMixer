package com.media.mixer.media.audio

import android.util.Log
import androidx.media3.common.MediaItem
import androidx.media3.common.util.UnstableApi
import androidx.media3.session.MediaLibraryService.MediaLibrarySession
import androidx.media3.session.MediaSession
import com.google.common.util.concurrent.Futures
import com.google.common.util.concurrent.ListenableFuture
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.cancel
import javax.inject.Inject

class MusicSessionCallback @Inject constructor(
) : MediaLibrarySession.Callback {
    private val coroutineScope = CoroutineScope(Dispatchers.Main + SupervisorJob())
    override fun onAddMediaItems(
        mediaSession: MediaSession,
        controller: MediaSession.ControllerInfo,
        mediaItems: List<MediaItem>
    ): ListenableFuture<List<MediaItem>> {
        return Futures.immediateFuture(
            mediaItems.map { mediaItem ->
                Log.d("TAG", "onAddMediaItems:old ${mediaItem.mediaMetadata.title}")
              val new =  mediaItem.buildUpon()
                    .setUri(mediaItem.requestMetadata.mediaUri)
                    .setMediaId(mediaItem.mediaId)
                    .build()
                Log.d("TAG", "onAddMediaItems:new ${new.mediaMetadata.title}")
                new
            }
        )
    }


    override fun onConnect(
        session: MediaSession,
        controller: MediaSession.ControllerInfo
    ): MediaSession.ConnectionResult {
        val connectionResult = super.onConnect(session, controller)
        val availableSessionCommands = connectionResult.availableSessionCommands.buildUpon()

        return MediaSession.ConnectionResult.accept(
            availableSessionCommands.build(),
            connectionResult.availablePlayerCommands
        )
    }

    fun cancelCoroutineScope() {
        coroutineScope.cancel()
    }

    @UnstableApi
    override fun onPlaybackResumption(
        mediaSession: MediaSession,
        controller: MediaSession.ControllerInfo
    ): ListenableFuture<MediaSession.MediaItemsWithStartPosition> {
        return super.onPlaybackResumption(mediaSession, controller)
    }
}
