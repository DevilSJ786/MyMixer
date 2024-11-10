package com.media.mixer.media.presentation

import androidx.compose.animation.Crossfade
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableLongStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.media3.common.Player
import com.media.mixer.R
import com.media.mixer.core.utils.convertToText
import com.media.mixer.core.utils.listOfColorForBorder
import com.media.mixer.media.media.MediaState
import com.media.mixer.media.media.rememberControllerState
import com.media.mixer.screens.components.TrackSlider
import kotlinx.coroutines.delay

/**
 * A simple controller, which consists of a play/pause button and a time bar.
 */
@Composable
fun SimpleController(
    player: Player,
    title: String,
    mediaState: MediaState,
    modifier: Modifier = Modifier,
    isLandscape: Boolean,
    onShare: () -> Unit,
    exitFullscreen: () -> Unit,
    enterFullscreen: () -> Unit,
    onResize: () -> Unit,
    onBack: () -> Unit
) {
    Crossfade(targetState = mediaState.isControllerShowing, modifier, label = "") { isShowing ->
        if (isShowing) {
            val currentPosition = remember { mutableLongStateOf(0) }
            val sliderPosition = remember { mutableLongStateOf(0) }
            val totalDuration = remember { mutableLongStateOf(0) }

            LaunchedEffect(key1 = player.currentPosition, key2 = player.isPlaying) {
                delay(1000)
                currentPosition.longValue = player.currentPosition
            }

            LaunchedEffect(currentPosition.longValue) {
                sliderPosition.longValue = currentPosition.longValue
            }

            LaunchedEffect(player.duration) {
                if (player.duration > 0) {
                    totalDuration.longValue = player.duration
                }
            }
            val controllerState = rememberControllerState(mediaState)
            var scrubbing by remember { mutableStateOf(false) }
            val hideWhenTimeout = !mediaState.shouldShowControllerIndefinitely && !scrubbing
            var hideEffectReset by remember { mutableIntStateOf(0) }
            LaunchedEffect(hideWhenTimeout, hideEffectReset) {
                if (hideWhenTimeout) {
                    // hide after 3s
                    delay(3000)
                    mediaState.isControllerShowing = false
                }
            }
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(16.dp),
                verticalArrangement = Arrangement.spacedBy(0.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(16.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    IconButton(onClick = onBack) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = null,
                            tint = MaterialTheme.colorScheme.primary
                        )
                    }
                    Text(
                        modifier = Modifier
                            .fillMaxWidth()
                            .weight(1f, fill = false),
                        text = title,
                        color = MaterialTheme.colorScheme.primary,
                        style = MaterialTheme.typography.titleMedium,
                        overflow = TextOverflow.Ellipsis,
                        maxLines = 1
                    )
                    IconButton(onClick = onShare) {
                        Icon(
                            painter = painterResource(id = R.drawable.share),
                            contentDescription = null,
                            tint = MaterialTheme.colorScheme.primary
                        )
                    }
                }
                Spacer(modifier = Modifier.weight(1f))

                TrackSlider(
                    value = sliderPosition.longValue.toFloat(),
                    onValueChange = {
                        sliderPosition.longValue = it.toLong()
                    },
                    onValueChangeFinished = {
                        currentPosition.longValue = sliderPosition.longValue
                        player.seekTo(sliderPosition.longValue)
                    },
                    songDuration = totalDuration.longValue.toFloat()
                )
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceAround
                ) {
                    Text(
                        text = (currentPosition.longValue).convertToText(),
                        modifier = Modifier.weight(1f),
                        color = MaterialTheme.colorScheme.primary,
                        style = TextStyle(fontWeight = FontWeight.Bold)
                    )

                    val remainTime = totalDuration.longValue - currentPosition.longValue
                    Text(
                        text = if (remainTime >= 0) remainTime.convertToText() else "",
                        color = MaterialTheme.colorScheme.primary,
                        style = TextStyle(fontWeight = FontWeight.Bold)
                    )
                }
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceEvenly,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    IconButton(
                        onClick = if (isLandscape) exitFullscreen else enterFullscreen
                    ) {
                        Icon(
                            painter = painterResource(id = R.drawable.rotation),
                            contentDescription = null, tint = MaterialTheme.colorScheme.primary
                        )
                    }
                    IconButton(
                        onClick = { player.seekToPrevious() },
                    ) {
                        Icon(
                            painter = painterResource(id = R.drawable.previous),
                            contentDescription = null, tint = MaterialTheme.colorScheme.primary
                        )
                    }

                    IconButton(
                        onClick = {
                            hideEffectReset++
                            controllerState.playOrPause()
                        },
                        modifier = Modifier
                            .size(50.dp)
                            .border(
                                BorderStroke(2.dp, Brush.linearGradient(listOfColorForBorder)),
                                CircleShape
                            )
                    ) {
                        Icon(
                            modifier = Modifier.padding(4.dp),
                            painter = painterResource(
                                if (controllerState.showPause) R.drawable.ic_video_pause_black
                                else R.drawable.ic_video_play_black
                            ),
                            contentDescription = null, tint = MaterialTheme.colorScheme.primary
                        )
                    }

                    IconButton(
                        onClick = { player.seekToNext() },
                    ) {
                        Icon(
                            painter = painterResource(id = R.drawable.next),
                            contentDescription = null, tint = MaterialTheme.colorScheme.primary
                        )
                    }
                    IconButton(
                        onClick = onResize,
                    ) {
                        Icon(
                            painter = painterResource(id = R.drawable.resize),
                            contentDescription = null, tint = MaterialTheme.colorScheme.primary
                        )
                    }
                }

            }
        }
    }
}
