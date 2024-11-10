package com.media.mixer.screens.recorder

import android.Manifest
import android.text.format.DateUtils
import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
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
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Pause
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.ViewList
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.ElevatedCard
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.runtime.snapshots.SnapshotStateList
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.media3.common.Player
import com.airbnb.lottie.compose.LottieAnimation
import com.airbnb.lottie.compose.LottieCompositionSpec
import com.airbnb.lottie.compose.LottieConstants
import com.airbnb.lottie.compose.animateLottieCompositionAsState
import com.airbnb.lottie.compose.rememberLottieComposition
import com.media.mixer.R
import com.media.mixer.core.components.AppIcon
import com.media.mixer.core.components.GradientButton
import com.media.mixer.core.components.OutPutScreen
import com.media.mixer.core.components.ProgressScreen
import com.media.mixer.core.utils.PermissionHelper
import com.media.mixer.core.utils.RecorderState
import com.media.mixer.core.utils.TransformerState
import com.media.mixer.core.utils.listOfColorForBorder
import com.media.mixer.media.audio.common.MusicState
import com.media.mixer.screens.audiocutter.CutAudioScreen
import com.media.mixer.screens.components.AudioVisualizer
import com.media.mixer.screens.components.BackHandler
import com.media.mixer.screens.components.ClickableAppIcon
import kotlinx.coroutines.delay

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AudioRecorderScreen(
    uiState: RecorderUiState,
    transformerState: TransformerState,
    amplitudes: SnapshotStateList<Int>,
    recordedTime: Long,
    recorderState: RecorderState,
    player: Player,
    saveLocation: String,
    title: String,
    musicState: MusicState,
    setRingtone: () -> Unit,
    setAlarm: () -> Unit,
    setNotification: () -> Unit,
    onCut: () -> Unit,
    onShare: () -> Unit,
    onSaveAs: () -> Unit,
    onPlay: () -> Unit,
    onSave: () -> Unit,
    onRecord: () -> Unit,
    onCounterScreen: () -> Unit,
    onStart: () -> Unit,
    onSaveProcessState: () -> Unit,
    onClose: () -> Unit,
    onDelete: () -> Unit,
    setDefaultDownloadLocation: (String) -> Unit,
    onSaveCut: (ClosedFloatingPointRange<Float>) -> Unit,
    onBack: () -> Unit
) {
    BackHandler {
        onBack()
    }
    val context = LocalContext.current
    Scaffold( topBar = {
            CenterAlignedTopAppBar(
                title = {
                    Text(
                        text = "Voice Recorder",
                        color = Color.White,
                        style = MaterialTheme.typography.titleLarge
                    )
                },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        AppIcon(painter = painterResource(id = R.drawable.back_))
                    }
                },
                actions = {
                    ClickableAppIcon(imageVector = Icons.Default.ViewList) {
                        onRecord()
                    }
                },
                colors = TopAppBarDefaults.centerAlignedTopAppBarColors(containerColor = Color.Transparent)
            )
        }
    ) { pV ->
        AnimatedContent(targetState = uiState, label = "") {
            when (it) {
                is RecorderUiState.StartState -> {
                    StartScreen(paddingValues = pV, onStart = {
                        if (PermissionHelper.checkPermissions(
                                context,
                                arrayOf(Manifest.permission.RECORD_AUDIO)
                            )
                        ) {
                            onCounterScreen()
                        }
                    })
                }

                is RecorderUiState.WaitState -> {
                    StartCounterScreen(paddingValues = pV, onStart = onStart)
                }

                is RecorderUiState.RecordingState -> {
                    RecordingScreen(
                        paddingValues = pV,
                        amplitudes = amplitudes,
                        recordedTime = recordedTime,
                        recorderState = recorderState,
                        onClose = onClose,
                        onPlay = onPlay,
                        onSave = onSave
                    )
                }

                is RecorderUiState.LoadingState -> {
                    LoadingScreen(paddingValues = pV, onSaveProcessState = onSaveProcessState)
                }

                is RecorderUiState.SaveProcessState -> {
                    OutPutScreen(
                        title = "SongOutPut",
                        player = player,
                        musicState = musicState,
                        padding = pV,
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

                is RecorderUiState.CutState -> {
                    CutAudioScreen(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(pV)
                            .padding(16.dp),
                        player = player,
                        title = title,
                        duration = recordedTime,
                        musicState = musicState,
                        onSave = onSaveCut
                    )
                }
                is RecorderUiState.CutLoadingState->{
                    ProgressScreen(
                        paddingValues = pV,
                        transformerState = transformerState,
                        onSaveProcessState = onSaveProcessState,
                        onBack = onBack
                    )
                }
            }
        }
    }
}

@Composable
fun StartScreen(paddingValues: PaddingValues, onStart: () -> Unit) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(paddingValues),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.SpaceEvenly
    ) {
        Spacer(modifier = Modifier.height(32.dp))
        Card(
            border = BorderStroke(4.dp, Brush.linearGradient(listOfColorForBorder)),
            shape = CircleShape
        ) {
            AppIcon(
                modifier = Modifier
                    .size(110.dp)
                    .padding(20.dp),
                painter = painterResource(id = R.drawable.microphone)
            )
        }
        Spacer(modifier = Modifier.weight(1F))
        GradientButton(onClick = onStart) {
            Text(text = "Start", modifier = Modifier.padding(horizontal = 32.dp))
        }
    }
}

@Composable
fun LoadingScreen(paddingValues: PaddingValues, onSaveProcessState: () -> Unit) {
    var visibility by remember {
        mutableStateOf(false)
    }
    var progressT by remember {
        mutableIntStateOf(0)
    }
    LaunchedEffect(Unit) {
        repeat(5) {
            delay(1000)
            progressT += 20
        }
        visibility = true
    }
    val composition by rememberLottieComposition(
        spec = LottieCompositionSpec.RawRes(R.raw.cd)
    )
    var isPlaying by remember {
        mutableStateOf(true)
    }
    val progress by animateLottieCompositionAsState(
        composition = composition,
        iterations = LottieConstants.IterateForever,
        isPlaying = isPlaying
    )
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(paddingValues),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(0.dp)
    ) {
        Box(
            modifier = Modifier.fillMaxWidth(),
            contentAlignment = Alignment.Center
        ) {
            LottieAnimation(
                modifier = Modifier.size(200.dp),
                composition = composition,
                progress = {
                    progress
                }
            )
            Text(
                modifier = Modifier.align(Alignment.Center),
                text = "$progressT %",
                color = Color.White,
                style = MaterialTheme.typography.titleMedium
            )
            Image(
                modifier = Modifier
                    .align(Alignment.BottomEnd)
                    .size(100.dp)
                    .padding(end = 16.dp),
                painter = painterResource(id = R.drawable.player_tip),
                contentDescription = null
            )
        }
        Spacer(modifier = Modifier.height(40.dp))
        Text(
            modifier = Modifier.fillMaxWidth(),
            color = Color.White,
            textAlign = TextAlign.Center,
            text = "Wait a movement While your audio get \n ready...",
            style = MaterialTheme.typography.bodyMedium
        )
        Spacer(modifier = Modifier.height(20.dp))
        Text(
            modifier = Modifier.fillMaxWidth(),
            color = Color.White,
            textAlign = TextAlign.Center,
            text = "Do not need to stay here, once the \n process is finished. Click on DONE button \nto see your result",
            style = MaterialTheme.typography.bodyMedium
        )
        Spacer(modifier = Modifier.weight(1F))
        AnimatedVisibility(visible = visibility) {
            GradientButton(onClick = onSaveProcessState) {
                Text(text = "DONE")
            }
        }
    }


}

@Composable
fun StartCounterScreen(paddingValues: PaddingValues, onStart: () -> Unit) {
    var count by rememberSaveable {
        mutableIntStateOf(5)
    }

    LaunchedEffect(count) {
        delay(1000)
        count--
        if (count == 0) onStart()
    }
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(paddingValues)
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Top
    ) {
        Card(
            shape = RoundedCornerShape(8.dp),
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(0.dp)
            ) {
                Spacer(modifier = Modifier.height(20.dp))
                Text(
                    text = "You have to record at least 5 sec. here",
                    style = MaterialTheme.typography.bodyMedium,
                    color = Color.White.copy(alpha = 0.5F)
                )
                Spacer(modifier = Modifier.height(30.dp))
                Text(
                    text = "Start In",
                    style = MaterialTheme.typography.titleLarge,
                    color = Color.White
                )
                Spacer(modifier = Modifier.height(20.dp))
                Card(
                    modifier = Modifier.size(60.dp),
                    border = BorderStroke(4.dp, Brush.linearGradient(listOfColorForBorder)),
                    shape = CircleShape,
                ) {
                    Box(
                        contentAlignment = Alignment.Center, modifier = Modifier
                            .size(60.dp)
                            .clip(CircleShape)
                    ) {
                        Text(
                            text = count.toString(),
                            style = MaterialTheme.typography.displayMedium,
                            textAlign = TextAlign.Center,
                            color = Color.White
                        )
                    }
                }
                Spacer(modifier = Modifier.height(20.dp))
            }
        }
    }
}

@Composable
fun RecordingScreen(
    paddingValues: PaddingValues,
    amplitudes: SnapshotStateList<Int>,
    recordedTime: Long,
    recorderState: RecorderState,
    onClose: () -> Unit,
    onPlay: () -> Unit,
    onSave: () -> Unit
) {
    val composition by rememberLottieComposition(
        spec = LottieCompositionSpec.RawRes(R.raw.voice_recorder)
    )
    var isPlaying by remember {
        mutableStateOf(true)
    }
    val progress by animateLottieCompositionAsState(
        composition = composition,
        iterations = LottieConstants.IterateForever,
        isPlaying = isPlaying
    )
    LaunchedEffect(key1 = recorderState) {
      isPlaying= recorderState != RecorderState.PAUSED
    }
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(paddingValues),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {

        LottieAnimation(
            modifier = Modifier
                .fillMaxWidth()
                .height(200.dp),
            composition = composition,
            progress = {
                progress
            }
        )
        AudioVisualizer(
            modifier = Modifier
                .fillMaxWidth()
                .height(100.dp),
            amplitudes = amplitudes
        )

        Text(
            text = DateUtils.formatElapsedTime(recordedTime),
            style = MaterialTheme.typography.displayMedium,
            color = Color.White
        )
        Spacer(modifier = Modifier.weight(1f))
        Row(
            verticalAlignment = Alignment.CenterVertically
        ) {
            ElevatedCard(
                shape = CircleShape,
                onClick = onClose,
                colors = CardDefaults.elevatedCardColors(containerColor = Color(0xFF57324B)),
            ) {
                AppIcon(modifier = Modifier.padding(16.dp), imageVector = Icons.Default.Close)
            }
            Spacer(modifier = Modifier.width(20.dp))
            ElevatedCard(
                colors = CardDefaults.elevatedCardColors(containerColor = Color.White),
                shape = CircleShape,
                onClick = onPlay,
            ) {
                AppIcon(
                    modifier = Modifier
                        .padding(16.dp)
                        .size(52.dp),
                    imageVector = if (recorderState == RecorderState.PAUSED) Icons.Default.PlayArrow
                    else Icons.Default.Pause
                )
            }
            Spacer(modifier = Modifier.width(20.dp))
            ElevatedCard(
                shape = CircleShape,
                onClick = onSave,
                colors = CardDefaults.elevatedCardColors(containerColor = Color(0xFF332A33))
            ) {
                AppIcon(modifier = Modifier.padding(16.dp), imageVector = Icons.Default.Check)
            }
        }
        Spacer(modifier = Modifier.height(40.dp))
    }
}