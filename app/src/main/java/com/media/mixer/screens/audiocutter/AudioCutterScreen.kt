package com.media.mixer.screens.audiocutter

import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.Crossfade
import androidx.compose.animation.core.spring
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.basicMarquee
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
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.RangeSlider
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Slider
import androidx.compose.material3.SliderDefaults
import androidx.compose.material3.Tab
import androidx.compose.material3.TabRow
import androidx.compose.material3.TabRowDefaults.tabIndicatorOffset
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableLongStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.runtime.snapshots.SnapshotStateList
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.paint
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
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
import com.media.mixer.core.utils.listOfColorForBorder
import com.media.mixer.data.entities.Song
import com.media.mixer.media.audio.common.MusicState
import com.media.mixer.media.domain.utils.asFormattedString
import com.media.mixer.media.presentation.PlayerImage2
import com.media.mixer.screens.components.BackHandler
import com.media.mixer.screens.videotoaudio.CutterUiState
import com.media.mixer.ui.theme.buttonBg

import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AudioCutterScreen(
    allAudio: List<Song>,
    transformerState: TransformerState,
    trimState: CutterUiState,
    player: Player,
    musicState: MusicState,
    title: String,
    saveLocation: String,
    duration: Long,
    onSave: (ClosedFloatingPointRange<Float>) -> Unit,
    onOutPut: () -> Unit,
    onNext: (Song) -> Unit,
    onCut: () -> Unit,
    onSaveAgain: (ClosedFloatingPointRange<Float>) -> Unit,
    setRingtone: () -> Unit,
    setAlarm: () -> Unit,
    setNotification: () -> Unit,
    onShare: () -> Unit,
    onSaveAs: () -> Unit,
    onDelete: () -> Unit,
    setDefaultDownloadLocation: (String) -> Unit,
    onBack: () -> Unit
) {
    BackHandler {
        onBack()
    }

    Scaffold(modifier = Modifier
        .fillMaxSize(),
        topBar = {
            TopAppBar(title = {
                Text(
                    text = "Audio Cutter",
                    color = Color.White,
                    style = MaterialTheme.typography.titleLarge
                )
            },
                colors = TopAppBarDefaults.centerAlignedTopAppBarColors(containerColor = Color.Transparent),
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        AppIcon(painter = painterResource(id = R.drawable.back_))
                    }
                },
                actions = {
                    AppIcon(
                        modifier = Modifier.padding(horizontal = 16.dp),
                        painter = painterResource(id = R.drawable.rotate_right)
                    )
                })
        }) {
        when (trimState) {
            is CutterUiState.AudioSelection -> {
                AudioSelectionScreen(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(it),
                    allAudio = allAudio,
                    onNext = onNext
                )
            }

            is CutterUiState.AudioCutter -> {
                CutAudioScreen(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(it)
                        .padding(16.dp),
                    player = player,
                    title = title,
                    duration = duration,
                    musicState = musicState,
                    onSave = onSave
                )
            }

            is CutterUiState.Loading -> {
                ProgressScreen(
                    paddingValues = it,
                    transformerState = transformerState,
                    onSaveProcessState = onOutPut,
                    onBack = onBack
                )
            }

            is CutterUiState.OutPut -> {
                OutPutScreen(
                    title = "SongOutPut",
                    saveLocation = saveLocation,
                    player = player,
                    musicState = musicState,
                    padding = it,
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

            is CutterUiState.CutAgain -> {
                CutAudioScreen(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(it)
                        .padding(16.dp),
                    player = player,
                    title = title,
                    duration = duration,
                    musicState = musicState,
                    onSave = onSaveAgain
                )
            }
        }
    }
}

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun AudioSelectionScreen(
    modifier: Modifier = Modifier,
    allAudio: List<Song>,
    onNext: (Song) -> Unit
) {
    val selectedAudio = remember {
        mutableStateOf<Song?>(null)
    }
    val lazyListState1 = rememberLazyListState()
    Column(
        modifier = modifier,
        verticalArrangement = Arrangement.spacedBy(0.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        LazyColumn(
            modifier = Modifier.fillMaxWidth().padding(horizontal = 16.dp)
                .weight(1f, fill = false),
            verticalArrangement = Arrangement.spacedBy(8.dp),
            state = lazyListState1
        ) {

            itemsIndexed(items = allAudio) { i: Int, item: Song ->
                if (i == 0) Spacer(modifier = Modifier.height(16.dp))
                AudioItem(modifier = Modifier
                    .fillMaxWidth()
                    .animateItemPlacement(),
                    song = item,
                    selected = selectedAudio.value == item,
                    onSelected = {
                        when (selectedAudio.value) {
                            null -> {
                                selectedAudio.value = item
                            }

                            item -> {
                                selectedAudio.value = null
                            }

                            else -> {
                                selectedAudio.value = item
                            }
                        }
                    })
            }
        }
        val number = if (selectedAudio.value == null) "0" else "1"
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .background(Color(0xFF473A43))
                .padding(16.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Text(text = "$number File Selected", color = Color.White)
            GradientButton(onClick = { selectedAudio.value?.let { onNext(it) } }) {
                Text(text = "Next")
            }
        }
    }
}

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun AudioItem(
    modifier: Modifier = Modifier,
    selected: Boolean,
    song: Song,
    onSelected: () -> Unit,
) {
    Card(
        modifier = modifier.fillMaxWidth(), shape = RoundedCornerShape(16.dp),
    ) {
        Row(
            modifier = modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            PlayerImage2(
                modifier = Modifier.size(45.dp),
                trackImageUrl = song.artworkUri,
            )
            Column(
                modifier = Modifier
                    .weight(1f, fill = false)
                    .fillMaxWidth(),
                horizontalAlignment = Alignment.Start
            ) {
                Text(
                    modifier = Modifier.basicMarquee(),
                    text = song.title,
                    color = Color.White,
                    style = MaterialTheme.typography.titleMedium,
                    maxLines = 1
                )
                Text(
                    text = song.duration.toLong().convertToText(),
                    color = Color.White,
                    style = MaterialTheme.typography.bodyMedium
                )
            }
            Crossfade(targetState = selected, label = "") {
                if (it) {
                    val brush = Brush.linearGradient(listOfColorForBorder)
                    Button(
                        onClick = onSelected,
                        border = BorderStroke(2.dp, brush),
                        shape = RoundedCornerShape(50),
                        colors = ButtonDefaults.buttonColors(containerColor = Color.Transparent),
                        contentPadding = PaddingValues(12.dp, 8.dp)
                    ) {
                        Text(
                            text = "Selected",
                            color = Color.White,
                            style = MaterialTheme.typography.bodyMedium
                        )
                    }
                } else {
                    Button(
                        onClick = onSelected,
                        colors = ButtonDefaults.buttonColors(containerColor = buttonBg),
                    ) {
                        Text(text = "Add", color = Color.White)
                    }
                }
            }
        }
    }

}


@Composable
fun CutAudioScreen(
    modifier: Modifier = Modifier,
    player: Player,
    musicState: MusicState,
    title: String,
    duration: Long,
    onSave: (ClosedFloatingPointRange<Float>) -> Unit
) {
    val currentPosition = remember {
        mutableLongStateOf(0)
    }

    val totalDuration = remember {
        mutableLongStateOf(duration)
    }

    var sliderPosition by remember {
        mutableStateOf(
            0f..totalDuration.longValue.toFloat().div(1.5f)
        )
    }
    val volumeSliderPosition = remember {
        mutableFloatStateOf(50f)
    }
    val difference = remember {
        mutableLongStateOf((sliderPosition.endInclusive - sliderPosition.start).toLong())
    }
    LaunchedEffect(key1 = player.currentPosition, key2 = player.isPlaying) {
        delay(1000)
        currentPosition.longValue = player.currentPosition
    }

    LaunchedEffect(currentPosition.longValue) {
        if (currentPosition.longValue > sliderPosition.endInclusive.toLong()) {
            player.seekTo(sliderPosition.start.toLong())
        }
    }

    LaunchedEffect(player.duration) {
        if (player.duration > 0) {
            totalDuration.longValue = player.duration
        }
    }
    Column(
        modifier = modifier,
        verticalArrangement = Arrangement.spacedBy(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalAlignment = Alignment.Top,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Column(
                modifier = Modifier.weight(1f, fill = false),
                verticalArrangement = Arrangement.spacedBy(8.dp),
                horizontalAlignment = Alignment.Start
            ) {
                Text(
                    text = title,
                    style = MaterialTheme.typography.titleMedium,
                    maxLines = 2,
                    color = Color.White,
                    overflow = TextOverflow.Ellipsis
                )
                Text(
                    text = totalDuration.longValue.asFormattedString(),
                    style = MaterialTheme.typography.bodyMedium,
                    overflow = TextOverflow.Ellipsis,
                    color = Color.White
                )
            }
            Crossfade(
                targetState = musicState.playWhenReady, animationSpec = spring(), label = ""
            ) { targetPlayWhenReady ->
                if (targetPlayWhenReady) {
                    IconButton(
                        onClick = { player.pause() },
                    ) {
                        AppIcon(
                            painter = painterResource(id = R.drawable.pause_circle),
                            modifier = Modifier.size(45.dp),
                            contentDescription = stringResource(id = R.string.pause)
                        )
                    }
                } else {
                    IconButton(
                        onClick = { player.play() },
                    ) {
                        AppIcon(
                            painter = painterResource(id = R.drawable.play_circle),
                            modifier = Modifier.size(45.dp),
                            contentDescription = stringResource(id = R.string.play)
                        )
                    }
                }
            }
        }

        RangeSliderAudio(valueRange = totalDuration.longValue.toFloat(),
            sliderPosition = sliderPosition,
            onValueChange = {
                sliderPosition = it
            },
            onValueChangeFinished = {
                currentPosition.longValue = sliderPosition.start.toLong()
                difference.longValue = (sliderPosition.endInclusive - sliderPosition.start).toLong()
                player.seekTo(sliderPosition.start.toLong())
            })

        Row(
            modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Text(
                text = sliderPosition.start.toLong().convertToText(),
                modifier = Modifier,
                color = Color.White,
                style = TextStyle(fontWeight = FontWeight.Bold)
            )
            Text(
                text = difference.longValue.convertToText(),
                modifier = Modifier,
                color = Color.White,
                style = TextStyle(fontWeight = FontWeight.Bold)
            )
            Text(
                text = if (sliderPosition.endInclusive >= 0) sliderPosition.endInclusive.toLong()
                    .convertToText() else "",
                color = Color.White,
                style = TextStyle(fontWeight = FontWeight.Bold)
            )
        }
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(text = "Volume", color = Color.White.copy(alpha = 0.5f))
            Slider(
                modifier = Modifier.weight(1f, fill = false),
                value = volumeSliderPosition.floatValue,
                onValueChange = { volumeSliderPosition.floatValue = it },
                onValueChangeFinished = {},
                valueRange = 0f..100f,
                colors = SliderDefaults.colors(
                    thumbColor = Color.White,
                    activeTrackColor = Color.Magenta,
                    inactiveTrackColor = Color.White,
                )
            )
            Text(
                text = "${volumeSliderPosition.floatValue.toInt()}%",
                color = Color.White.copy(alpha = 0.5f)
            )
        }
        Spacer(modifier = Modifier.weight(1f))
        GradientButton(onClick = {
            player.pause()
            onSave(sliderPosition)
        }) {
            Text(text = "Save")
        }
    }
}

@Composable
fun RangeSliderAudio(
    modifier: Modifier = Modifier,
    sliderPosition: ClosedFloatingPointRange<Float>,
    valueRange: Float,
    onValueChange: (ClosedFloatingPointRange<Float>) -> Unit,
    onValueChangeFinished: (() -> Unit)? = null
) {
    RangeSlider(
        modifier = modifier,
        value = sliderPosition,
        onValueChange = onValueChange,
        valueRange = 0f..valueRange,
        onValueChangeFinished = onValueChangeFinished,
        colors = SliderDefaults.colors(thumbColor = Color.White, activeTrackColor = Color.Magenta)
    )
}