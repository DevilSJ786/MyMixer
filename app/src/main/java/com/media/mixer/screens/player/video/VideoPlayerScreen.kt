package com.media.mixer.screens.player.video

import android.annotation.SuppressLint
import android.content.pm.ActivityInfo
import android.content.res.Configuration
import androidx.activity.OnBackPressedCallback
import androidx.compose.animation.animateContentSize
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.SideEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.paint
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.media3.common.Player
import com.media.mixer.R
import com.media.mixer.core.utils.hideSystemUI
import com.media.mixer.core.utils.showSystemUI
import com.media.mixer.media.domain.utils.findActivity
import com.media.mixer.media.media.ControllerVisibility
import com.media.mixer.media.media.Media
import com.media.mixer.media.media.MediaState
import com.media.mixer.media.media.ResizeMode
import com.media.mixer.media.media.rememberMediaState
import com.media.mixer.media.presentation.PlayerEvent
import com.media.mixer.media.presentation.RememberPlayer
import com.media.mixer.media.presentation.SimpleController
import com.media.mixer.media.presentation.UiState


@Composable
fun VideoPlayerScreen(
    title: String,
    player: Player,
    uiState: UiState,
    onShare: () -> Unit,
    onAudioEvent: (PlayerEvent) -> Unit,
    onResize: (ResizeMode) -> Unit,
    onBack: () -> Unit,
) {

    val configuration = LocalConfiguration.current
    val isLandscape = configuration.orientation == Configuration.ORIENTATION_LANDSCAPE

    val activity = LocalContext.current.findActivity()!!
    SideEffect {
        if (isLandscape) activity.hideSystemUI()
        else activity.showSystemUI()
    }
    RememberPlayer(
        onPause = {
            onAudioEvent(PlayerEvent.Pause)
        },
        onStart = {
            onAudioEvent(PlayerEvent.Play)
        }
    )

    val state = rememberMediaState(player = player.takeIf { uiState.setPlayer })
    MediaContentView(
        isLandscape = isLandscape,
        modifier = Modifier
            .fillMaxSize()
            .animateContentSize(),
        uiState = uiState,
        state = state,
        onBack = onBack,
        onResize = onResize,
        title = title,
        onShare = onShare
    )
}


@Composable
private fun MediaContentView(
    isLandscape: Boolean,
    modifier: Modifier = Modifier,
    title: String,
    uiState: UiState,
    state: MediaState,
    onShare: () -> Unit,
    onResize: (ResizeMode) -> Unit,
    onBack: () -> Unit,
) {

    val activity = LocalContext.current.findActivity()!!
    val enterFullscreen = {
        activity.requestedOrientation =
            ActivityInfo.SCREEN_ORIENTATION_USER_LANDSCAPE
    }
    val exitFullscreen = {
        @SuppressLint("SourceLockedOrientationActivity")
        // Will reset to SCREEN_ORIENTATION_USER later
        activity.requestedOrientation = ActivityInfo.SCREEN_ORIENTATION_USER_PORTRAIT
    }
    Box(
        modifier = modifier
            .fillMaxSize()
            .paint(painter = painterResource(id = R.drawable.bg_video_audio))
            .clickable(
                indication = null,
                interactionSource = remember { MutableInteractionSource() }
            ) {
                if (state.player != null) {
                    state.controllerVisibility = when (state.controllerVisibility) {
                        ControllerVisibility.Visible -> ControllerVisibility.Invisible
                        ControllerVisibility.PartiallyVisible -> ControllerVisibility.Visible
                        ControllerVisibility.Invisible -> ControllerVisibility.Visible
                    }
                }
            },
        contentAlignment = Alignment.Center
    ) {
        Media(
            state = state,
            modifier = Modifier
                .fillMaxSize()
                .paint(
                    painterResource(id = R.drawable.bg_video_audio),
                    contentScale = ContentScale.FillBounds
                ),
            surfaceType = uiState.surfaceType,
            resizeMode = uiState.resizeMode,
            keepContentOnPlayerReset = uiState.keepContentOnPlayerReset,
            useArtwork = uiState.useArtwork,
            showBuffering = uiState.showBuffering,
            buffering = {
                Box(Modifier.fillMaxSize(), Alignment.Center) {
                    CircularProgressIndicator()
                }
            },
            errorMessage = { error ->
                Box(Modifier.fillMaxSize(), Alignment.Center) {
                    Text(
                        error.message ?: "",
                        modifier = Modifier
                            .background(
                                Color(0x80808080),
                                RoundedCornerShape(16.dp)
                            )
                            .padding(horizontal = 12.dp, vertical = 4.dp),
                        color = Color.White,
                        textAlign = TextAlign.Center
                    )
                }
            },
        )
        Scaffold(containerColor = Color.Black.copy(alpha = 0.5f)) {
            SimpleController(
                player = state.player!!,
                title = title,
                mediaState = state,
                modifier = Modifier
                    .fillMaxSize()
                    .padding(it),
                isLandscape = isLandscape,
                enterFullscreen = enterFullscreen,
                exitFullscreen = exitFullscreen,
                onBack = {
                    if (isLandscape) exitFullscreen() else onBack()
                },
                onShare = onShare,
                onResize = {
                    onResize(
                        when (uiState.resizeMode) {
                            ResizeMode.Zoom -> ResizeMode.Fit
                            ResizeMode.Fit -> ResizeMode.Fill
                            ResizeMode.Fill -> ResizeMode.FixedHeight
                            ResizeMode.FixedHeight -> ResizeMode.FixedWidth
                            ResizeMode.FixedWidth -> ResizeMode.Zoom
                        }
                    )
                }
            )
        }


    }
    val onBackPressedCallback = remember {
        object : OnBackPressedCallback(true) {
            override fun handleOnBackPressed() {
                if (isLandscape) {
                    exitFullscreen()
                } else {
                    onBack()
                }
            }
        }
    }
    val onBackPressedDispatcher = activity.onBackPressedDispatcher
    DisposableEffect(onBackPressedDispatcher) {
        onBackPressedDispatcher.addCallback(onBackPressedCallback)
        onDispose { onBackPressedCallback.remove() }
    }
    SideEffect {
        onBackPressedCallback.isEnabled = true
        if (isLandscape) {
            if (activity.requestedOrientation == ActivityInfo.SCREEN_ORIENTATION_USER) {
                activity.requestedOrientation = ActivityInfo.SCREEN_ORIENTATION_USER_LANDSCAPE
            }
        } else {
            activity.requestedOrientation = ActivityInfo.SCREEN_ORIENTATION_USER_PORTRAIT
        }
    }
}

