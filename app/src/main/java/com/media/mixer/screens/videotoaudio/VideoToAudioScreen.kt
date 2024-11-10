package com.media.mixer.screens.videotoaudio

import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.Crossfade
import androidx.compose.animation.core.spring
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableLongStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.paint
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.media3.common.Player
import com.media.mixer.R
import com.media.mixer.core.components.AppIcon
import com.media.mixer.core.components.GradientButton
import com.media.mixer.core.components.OutPutScreen
import com.media.mixer.core.components.ProgressScreen
import com.media.mixer.core.utils.TransformerState
import com.media.mixer.core.utils.convertToText
import com.media.mixer.core.utils.listOfAudioFormat
import com.media.mixer.core.utils.listOfAudioQuality
import com.media.mixer.media.audio.common.MusicState
import com.media.mixer.media.media.MediaState
import com.media.mixer.media.media.rememberMediaState
import com.media.mixer.media.presentation.PlayerImage
import com.media.mixer.screens.audiocutter.CutAudioScreen
import com.media.mixer.screens.components.BackHandler
import com.media.mixer.screens.components.TrackSlider

import kotlinx.coroutines.delay

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun VideoToAudioScreen(
    uiState: AudioUiState,
    name: String,
    transformerState: TransformerState,
    player: Player,
    saveLocation: String,
    musicState: MusicState,
    onSave: () -> Unit,
    onSaveAgain: (ClosedFloatingPointRange<Float>) -> Unit,
    setRingtone: () -> Unit,
    setAlarm: () -> Unit,
    setNotification: () -> Unit,
    onCut: () -> Unit,
    onShare: () -> Unit,
    onSaveAs: () -> Unit,
    onDelete: () -> Unit,
    onOutPut: () -> Unit,
    setDefaultDownloadLocation: (String) -> Unit,
    onBack: () -> Unit
) {
    BackHandler {
        onBack()
    }
    val state = rememberMediaState(player = player)
    Scaffold(
        topBar = {
            CenterAlignedTopAppBar(
                title = {
                    Text(
                        text = "Video To Audio",
                        color = Color.White,
                        style = MaterialTheme.typography.titleLarge
                    )
                },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        AppIcon(painter = painterResource(id = R.drawable.back_))
                    }
                },
                colors = TopAppBarDefaults.centerAlignedTopAppBarColors(containerColor = Color.Transparent)
            )
        }) { padding ->
        AnimatedContent(targetState = uiState, label = "") { audioUiState ->
            when (audioUiState) {
                is AudioUiState.AudioFormat -> {
                    AudioFormatScreen(
                        name = name,
                        state = state,
                        player = player,
                        musicState = musicState,
                        padding = padding,
                        play = { player.play() },
                        pause = { player.pause() },
                        onSave = onSave
                    )
                }

                is AudioUiState.Loading -> {
                    ProgressScreen(
                        paddingValues = padding,
                        transformerState = transformerState,
                        onSaveProcessState = onOutPut,
                        onBack = onBack
                    )
                }

                is AudioUiState.OutPut -> {
                    OutPutScreen(
                        title = "SongOutPut",
                        player = player,
                        musicState = musicState,
                        padding = padding,
                        saveLocation = saveLocation,
                        setRingtone = setRingtone,
                        setAlarm = setAlarm,
                        setNotification = setNotification,
                        onShare = onShare,
                        onSaveAs = onSaveAs,
                        onCut = onCut,
                        onDelete = onDelete,
                        setDefaultDownloadLocation = setDefaultDownloadLocation
                    )
                }

                is AudioUiState.CutAudio -> {
                    CutAudioScreen(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(padding)
                            .padding(16.dp),
                        player = player,
                        title = name,
                        duration = 0,
                        musicState = musicState,
                        onSave = onSaveAgain
                    )
                }
            }
        }
    }
}

@Composable
fun AudioFormatScreen(
    name: String,
    state: MediaState,
    player: Player,
    musicState: MusicState,
    padding: PaddingValues,
    play: () -> Unit,
    pause: () -> Unit,
    onSave: () -> Unit,
) {

    val currentPosition = remember {
        mutableLongStateOf(0)
    }

    val sliderPosition = remember {
        mutableLongStateOf(0)
    }

    val totalDuration = remember {
        mutableLongStateOf(0)
    }


    LaunchedEffect(key1 = player.currentPosition, key2 = player.isPlaying) {
        delay(1000)
        currentPosition.longValue = player.currentPosition
        if (player.currentPosition==totalDuration.longValue) player.seekTo(0)
    }

    LaunchedEffect(currentPosition.longValue) {
        sliderPosition.longValue = currentPosition.longValue
    }

    LaunchedEffect(player.duration) {
        if (player.duration > 0) {
            totalDuration.longValue = player.duration
        }
    }
    var formate by remember {
        mutableStateOf(listOfAudioFormat.first())
    }
    var quality by remember {
        mutableStateOf(listOfAudioQuality.first())
    }
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(padding)
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {

        Text(
            text = name,
            color = Color.White,
            maxLines = 1,
            overflow = TextOverflow.Ellipsis,
            style = MaterialTheme.typography.titleMedium
        )

        Box(
            modifier = Modifier.fillMaxWidth(),
            contentAlignment = Alignment.Center
        ) {
            PlayerImage(
                modifier = Modifier.size(200.dp),
                trackImageUrl = musicState.currentSong.artworkUri,
                imageBitmap = state.playerState?.mediaMetadata?.artworkData
            )
            Crossfade(
                targetState = player.isPlaying,
                animationSpec = spring(),
                label = ""
            ) { targetPlayWhenReady ->
                if (targetPlayWhenReady) {
                    IconButton(
                        onClick = pause,
                    ) {
                        AppIcon(
                            painter = painterResource(id = R.drawable.pause_circle),
                            modifier = Modifier.size(72.dp),
                            contentDescription = stringResource(id = R.string.pause)
                        )
                    }
                } else {
                    IconButton(
                        onClick = play,
                    ) {
                        AppIcon(
                            painter = painterResource(id = R.drawable.play_circle),
                            modifier = Modifier.size(72.dp),
                            contentDescription = stringResource(id = R.string.play)
                        )
                    }
                }
            }
        }

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
        ) {
            Text(
                text = (currentPosition.longValue).convertToText(),
                modifier = Modifier.weight(1f),
                color = Color.White,
                style = TextStyle(fontWeight = FontWeight.Bold)
            )

            val remainTime = totalDuration.longValue - currentPosition.longValue
            Text(
                text = if (remainTime >= 0) remainTime.convertToText() else "",
                color = Color.White,
                style = TextStyle(fontWeight = FontWeight.Bold)
            )
        }
        Spacer(modifier = Modifier.weight(1f))
        GradientButton(onClick = { onSave() }) {
            Text(text = "Save")
        }
    }
}



