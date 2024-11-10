package com.media.mixer.media.domain.utils

import android.net.Uri
import androidx.media3.common.MediaItem
import androidx.media3.common.MediaMetadata

internal fun buildPlayableMediaItem(
    mediaId: String,
    mediaUri: Uri,
    artworkUri: Uri,
    title: String,
    artist: String
) = MediaItem.Builder()
    .setUri(mediaUri)
    .setMediaId(mediaId)
    .setMediaMetadata(
        MediaMetadata.Builder()
            .setArtworkUri(artworkUri)
            .setArtist(artist)
            .setDisplayTitle(title)
            .setTitle(title)
            .build()
    ).build()

