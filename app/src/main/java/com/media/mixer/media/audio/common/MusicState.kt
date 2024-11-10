package com.media.mixer.media.audio.common

import android.net.Uri
import com.media.mixer.data.entities.Song
import com.media.mixer.media.domain.utils.MediaConstants.DEFAULT_DURATION_MS
import com.media.mixer.media.domain.utils.MediaConstants.DEFAULT_MEDIA_ID

data class MusicState(
    val currentSong: Song = Song(
        id = DEFAULT_MEDIA_ID,
        mediaUri = Uri.EMPTY,
        artworkUri = Uri.EMPTY,
        title = "",
        artist = "",
        duration = 0,
        album = "",
        path = "",
        type = ""
    ),
    val playbackState: PlaybackState = PlaybackState.IDLE,
    val playWhenReady: Boolean = true,
    val duration: Long = DEFAULT_DURATION_MS
)
