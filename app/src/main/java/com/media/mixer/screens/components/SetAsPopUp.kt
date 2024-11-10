package com.media.mixer.screens.components

import android.net.Uri
import androidx.compose.animation.Crossfade
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.spring
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.IconButton
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.mutableLongStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import androidx.media3.common.Player
import com.media.mixer.R
import com.media.mixer.core.components.AppIcon
import com.media.mixer.core.components.GradientButton
import com.media.mixer.core.utils.convertToText
import com.media.mixer.media.presentation.PlayerImage2
import com.media.mixer.ui.theme.buttonBg
import kotlinx.coroutines.delay

@Composable
fun SetAsPopUp(
    player: Player,
    trackImageUrl: Uri,
    title: String,
    onRingtone: () -> Unit,
    onAlarm: () -> Unit,
    onMessage: () -> Unit,
    onContact: () -> Unit,
    onClose: () -> Unit,
) {
    val currentPosition = remember {
        mutableLongStateOf(0)
    }

    val sliderPosition = remember {
        mutableLongStateOf(0)
    }

    val totalDuration = remember {
        mutableLongStateOf(1)
    }
    val progress = animateFloatAsState(
        targetValue = sliderPosition.longValue.toFloat().div(totalDuration.longValue.toFloat()),
        label = ""
    )

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
    Dialog(
        onDismissRequest = onClose,
        properties = DialogProperties(dismissOnBackPress = true, dismissOnClickOutside = true)
    ) {
        Card(
            shape = RoundedCornerShape(10.dp),
        ) {
            Column(
                modifier = Modifier.padding(16.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(
                    text = "Set As?",
                    style = MaterialTheme.typography.titleLarge,
                    modifier = Modifier.fillMaxWidth(),
                    textAlign = TextAlign.Center,
                    color = Color.White
                )
                PlayerCard(
                    modifier = Modifier.fillMaxWidth(),
                    trackImageUrl = trackImageUrl,
                    title = title,
                    value = progress.value,
                    songDuration = totalDuration.longValue.toFloat(),
                    playWhenReady = player.playWhenReady,
                    play = { player.play() },
                    pause = { player.pause() }
                )
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceAround,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    ActionItem(
                        title = "Ringtone",
                        id = R.drawable.call_calling,
                        onClick = onRingtone
                    )
                    ActionItem(
                        title = "Alarm",
                        id = R.drawable.notification,
                        onClick = onAlarm
                    )
                    ActionItem(
                        title = "Message",
                        id = R.drawable.message_favorite,
                        onClick = onMessage
                    )
                    ActionItem(
                        title = "Contact",
                        id = R.drawable.contact,
                        onClick = onContact
                    )
                }
            }
        }
    }
}

@Composable
fun PlayerCard(
    modifier: Modifier,
    trackImageUrl: Uri,
    title: String,
    value: Float,
    songDuration: Float,
    playWhenReady: Boolean,
    play: () -> Unit,
    pause: () -> Unit
) {

    Card(
        modifier = modifier,
        shape = RoundedCornerShape(10.dp),
        border = BorderStroke(2.dp, Color.White.copy(alpha = 0.5f)),
        colors = CardDefaults.cardColors(containerColor = buttonBg)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(8.dp),
            horizontalArrangement = Arrangement.spacedBy(8.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                contentAlignment = Alignment.Center
            ) {
                PlayerImage2(
                    modifier = Modifier.size(45.dp),
                    trackImageUrl = trackImageUrl,
                )
                Crossfade(
                    targetState = playWhenReady,
                    animationSpec = spring(),
                    label = ""
                ) { targetPlayWhenReady ->
                    if (targetPlayWhenReady) {
                        IconButton(
                            onClick = pause,
                        ) {
                            AppIcon(
                                painter = painterResource(id = R.drawable.pause_circle),
                                modifier = Modifier.size(25.dp),
                                contentDescription = stringResource(id = R.string.pause)
                            )
                        }
                    } else {
                        IconButton(
                            onClick = play,
                        ) {
                            AppIcon(
                                painter = painterResource(id = R.drawable.play_circle),
                                modifier = Modifier.size(25.dp),
                                contentDescription = stringResource(id = R.string.play)
                            )
                        }
                    }
                }
            }
            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                Text(
                    text = title,
                    color = Color.White,
                    style = MaterialTheme.typography.bodySmall,
                    overflow = TextOverflow.Ellipsis
                )
                LinearProgressIndicator(
                    progress = { value },
                    strokeCap = StrokeCap.Round
                )
                Text(
                    text = songDuration.toLong().convertToText(),
                    color = Color.White,
                    style = MaterialTheme.typography.labelSmall
                )
            }
        }
    }
}


@Composable
fun SetAudioPopUp(
    title: String,
    titleSong: String,
    description: String,
    player: Player,
    trackImageUrl: Uri,
    onSave: () -> Unit,
    onClose: () -> Unit,
) {
    val currentPosition = remember {
        mutableLongStateOf(0)
    }

    val sliderPosition = remember {
        mutableLongStateOf(0)
    }

    val totalDuration = remember {
        mutableLongStateOf(1)
    }
    val progress = animateFloatAsState(
        targetValue = sliderPosition.longValue.toFloat().div(totalDuration.longValue.toFloat()),
        label = ""
    )

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
    Dialog(
        onDismissRequest = onClose,
        properties = DialogProperties(dismissOnBackPress = true, dismissOnClickOutside = true)
    ) {
        Card(
            shape = RoundedCornerShape(10.dp),
        ) {
            Column(
                modifier = Modifier.padding(16.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(
                    text = title,
                    style = MaterialTheme.typography.titleLarge,
                    modifier = Modifier.fillMaxWidth(),
                    textAlign = TextAlign.Center,
                    color = Color.White
                )
                PlayerCard(
                    modifier = Modifier.fillMaxWidth(),
                    trackImageUrl = trackImageUrl,
                    title = titleSong,
                    value = progress.value,
                    songDuration = totalDuration.longValue.toFloat(),
                    playWhenReady = player.playWhenReady,
                    play = { player.play() },
                    pause = { player.pause() }
                )
                Text(text = description, style = MaterialTheme.typography.bodyMedium, color = Color.White)
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceAround,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Button(colors = ButtonDefaults.buttonColors(containerColor = buttonBg),onClick = onClose) {
                        Text(text = "Cancel",color = Color.White)
                    }
                    GradientButton(onClick = onSave) {
                        Text(text = "Save")
                    }
                }
            }
        }
    }
}