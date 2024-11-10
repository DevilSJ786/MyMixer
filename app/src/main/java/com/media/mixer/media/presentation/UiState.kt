package com.media.mixer.media.presentation

import com.media.mixer.media.media.ResizeMode
import com.media.mixer.media.media.ShowBuffering
import com.media.mixer.media.media.SurfaceType




data class UiState(
    val surfaceType: SurfaceType = SurfaceType.SurfaceView,
    val resizeMode: ResizeMode = ResizeMode.FixedWidth,
    val keepContentOnPlayerReset: Boolean = false,
    val useArtwork: Boolean = true,
    val showBuffering: ShowBuffering = ShowBuffering.Always,
    val setPlayer: Boolean = true,
    val playWhenReady: Boolean = true,

)
