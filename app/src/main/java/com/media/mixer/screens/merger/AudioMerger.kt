package com.media.mixer.screens.merger

import android.util.Log
import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.Crossfade
import androidx.compose.animation.animateContentSize
import androidx.compose.animation.core.spring
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.basicMarquee
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
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
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.ElevatedCard
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Slider
import androidx.compose.material3.SliderDefaults
import androidx.compose.material3.Tab
import androidx.compose.material3.TabRow
import androidx.compose.material3.TabRowDefaults.tabIndicatorOffset
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableLongStateOf
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.runtime.snapshots.SnapshotStateList
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.paint
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
import com.media.mixer.data.entities.Song
import com.media.mixer.media.audio.common.MusicState
import com.media.mixer.screens.audiocutter.AudioItem
import com.media.mixer.screens.audiocutter.CutAudioScreen
import com.media.mixer.screens.audiocutter.RangeSliderAudio
import com.media.mixer.screens.components.BackHandler
import com.media.mixer.screens.videotoaudio.MergerUiState

import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AudioMerger(
    allAudio: List<Song>,
    listOfSong: SnapshotStateList<Song>,
    mergerState: MergerUiState,
    transformerState: TransformerState,
    player: Player,
    musicState: MusicState,
    saveLocation: String,
    setPlayer: (Song) -> Unit,
    onSave: (List<ClosedFloatingPointRange<Float>>) -> Unit,
    onOutPut: () -> Unit,
    onNext: (List<Song>) -> Unit,
    onCut: () -> Unit,
    onSaveAgain: (ClosedFloatingPointRange<Float>) -> Unit,
    setRingtone: () -> Unit,
    setAlarm: () -> Unit,
    setNotification: () -> Unit,
    removeAt: (Int) -> Unit,
    onShare: () -> Unit,
    onSaveAs: () -> Unit,
    onDelete: () -> Unit,
    setDefaultDownloadLocation: (String) -> Unit,
    onBack: () -> Unit
) {
    BackHandler {
        onBack()
    }

    Scaffold(
        topBar = {
            CenterAlignedTopAppBar(title = {
                Text(
                    text = "Audio Merger",
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
                        painter = painterResource(id = R.drawable.rotate_right),
                        contentDescription = null
                    )
                })
        }) {
        when (mergerState) {
            is MergerUiState.AudioSelection -> {
                AudioMergerSelectionScreen(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(it),
                    allAudio = allAudio,
                    onNext = onNext
                )
            }

            is MergerUiState.AudioMerger -> {
                MergerAudiosScreen(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(it)
                        .padding(16.dp),
                    list = listOfSong,
                    player = player,
                    musicState = musicState,
                    setPlayer = setPlayer,
                    onSave = onSave,
                    removeAt = removeAt
                )
            }

            is MergerUiState.Loading -> {
                ProgressScreen(
                    paddingValues = it,
                    transformerState = transformerState,
                    onSaveProcessState = onOutPut,
                    onBack = onBack
                )
            }

            is MergerUiState.OutPut -> {
                OutPutScreen(
                    title = "SongOutPut",
                    player = player,
                    musicState = musicState,
                    padding = it,
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

            is MergerUiState.CutAgain -> {
                CutAudioScreen(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(it)
                        .padding(16.dp),
                    player = player,
                    title = "Output",
                    duration = 0,
                    musicState = musicState,
                    onSave = onSaveAgain
                )
            }
        }
    }
}

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun AudioMergerSelectionScreen(
    modifier: Modifier = Modifier,
    allAudio: List<Song>,
    onNext: (List<Song>) -> Unit
) {
    val state = rememberPagerState {
        3
    }
    val selectedAudio = remember {
        mutableStateListOf<Song>()
    }
    var count by remember {
        mutableIntStateOf(0)
    }
    val lazyListState1 = rememberLazyListState()
    LaunchedEffect(key1 = selectedAudio.size) {
        count = selectedAudio.size
    }
    Column(
        modifier = modifier,
        verticalArrangement = Arrangement.spacedBy(0.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        LazyColumn(
            modifier = Modifier.fillMaxSize().padding(horizontal = 16.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp),
            state = lazyListState1
        ) {
            itemsIndexed(items = allAudio) { i: Int, item: Song ->
                val selected = selectedAudio.contains(item)
                if (i==0)Spacer(modifier = Modifier.height(16.dp))
                AudioItem(modifier = Modifier
                    .fillMaxWidth()
                    .animateItemPlacement(),
                    song = item,
                    selected = selected,
                    onSelected = {
                        if (selected) selectedAudio.remove(item)
                        else selectedAudio.add(item)
                    }
                )
            }
        }
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .background(Color(0xFF473A43))
                .padding(16.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Text(text = "$count File Selected", color = Color.White)
            GradientButton(onClick = { if (selectedAudio.isNotEmpty()) onNext(selectedAudio.toList()) }) {
                Text(text = "Next")
            }
        }
    }
}

@Composable
fun MergerAudiosScreen(
    modifier: Modifier = Modifier,
    list: SnapshotStateList<Song>,
    player: Player,
    musicState: MusicState,
    setPlayer: (Song) -> Unit,
    removeAt: (Int) -> Unit,
    onSave: (List<ClosedFloatingPointRange<Float>>) -> Unit
) {
    var currentIndex by remember {
        mutableIntStateOf(0)
    }
    val currentPosition = remember {
        mutableListOf<ClosedFloatingPointRange<Float>>()
    }
    LaunchedEffect(key1 = currentIndex) {
        setPlayer(list[currentIndex])
    }
    LaunchedEffect(key1 = Unit) {
        if (currentPosition.isEmpty()) currentPosition.addAll(list.map { 0f..it.duration.toFloat() })
        Log.d("TAG", "MergerAudiosScreen:currentPosition ${currentPosition.size}")
    }
    val sliderPosition = remember {
        mutableFloatStateOf(50f)
    }
    Column(
        modifier = modifier.verticalScroll(rememberScrollState()),
        verticalArrangement = Arrangement.spacedBy(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        list.forEachIndexed { index, song ->
            MergerItem(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp),
                player = player,
                musicState = musicState,
                title = song.title,
                duration = song.duration.toLong(),
                trimState = index == currentIndex,
                remove = {
                    currentPosition.removeAt(index)
                    removeAt(index)
                },
                onClick = {
                    currentIndex = index
                },
                onSave = {
                    currentPosition.add(index, it)
                    Log.d("TAG", "MergerAudiosScreen:onadd $currentPosition")
                }
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
                value = sliderPosition.floatValue,
                onValueChange = { sliderPosition.floatValue = it },
                onValueChangeFinished = {},
                valueRange = 0f..100f,
                colors = SliderDefaults.colors(
                    thumbColor = Color.White,
                    activeTrackColor = Color.Magenta,
                    inactiveTrackColor = Color.White,
                )
            )
            Text(
                text = "${sliderPosition.floatValue.toInt()}%",
                color = Color.White.copy(alpha = 0.5f)
            )
        }
        Spacer(modifier = Modifier.weight(1f))
        GradientButton(onClick = {
            player.pause()
            onSave(currentPosition)
            Log.d("TAG", "MergerAudiosScreen: onsave:${currentPosition.size}")
        }) {
            Text(text = "Save")
        }
    }
}

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun MergerItem(
    modifier: Modifier = Modifier,
    player: Player,
    musicState: MusicState,
    title: String,
    duration: Long,
    trimState: Boolean,
    onClick: () -> Unit,
    remove: () -> Unit,
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
            0f..totalDuration.longValue.toFloat()
        )
    }
    val difference = remember {
        mutableLongStateOf((sliderPosition.endInclusive - sliderPosition.start).toLong())
    }
    LaunchedEffect(key1 = player.currentPosition, key2 = player.isPlaying, key3 = trimState) {
        if (!trimState) return@LaunchedEffect
        delay(1000)
        currentPosition.longValue = player.currentPosition
    }

    LaunchedEffect(currentPosition.longValue, key2 = trimState) {
        if (!trimState) return@LaunchedEffect
        if (currentPosition.longValue > sliderPosition.endInclusive.toLong()) {
            player.seekTo(sliderPosition.start.toLong())
        }
    }

    LaunchedEffect(player.duration, key2 = trimState) {
        if (!trimState) return@LaunchedEffect
        if (player.duration > 0) {
            totalDuration.longValue = player.duration
        }
    }
    ElevatedCard(
        modifier = Modifier.animateContentSize(),
        onClick = onClick,
    ) {
        Column(
            modifier = modifier,
            verticalArrangement = Arrangement.spacedBy(0.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.End
            ) {
                Text(
                    modifier = Modifier
                        .weight(1f, fill = false)
                        .basicMarquee(),
                    text = title,
                    style = MaterialTheme.typography.titleMedium,
                    maxLines = 1,
                    color = Color.White,
                    overflow = TextOverflow.Ellipsis,
                    textAlign = TextAlign.Start
                )
                Crossfade(
                    targetState = musicState.playWhenReady and trimState,
                    animationSpec = spring(),
                    label = ""
                ) { targetPlayWhenReady ->
                    if (targetPlayWhenReady) {
                        IconButton(
                            onClick = { player.pause() },
                        ) {
                            AppIcon(
                                painter = painterResource(id = R.drawable.pause_circle),
                                modifier = Modifier.size(25.dp),
                                contentDescription = stringResource(id = R.string.pause)
                            )
                        }
                    } else {
                        IconButton(
                            onClick = {
                                if (!trimState) onClick()
                                player.play()
                                player.seekTo(sliderPosition.start.toLong())
                            },
                        ) {
                            AppIcon(
                                painter = painterResource(id = R.drawable.play_circle),
                                modifier = Modifier.size(25.dp),
                                contentDescription = stringResource(id = R.string.play)
                            )
                        }
                    }
                }
                IconButton(
                    onClick = { remove() },
                ) {
                    AppIcon(
                        painter = painterResource(id = R.drawable.trash),
                        modifier = Modifier.size(25.dp),
                        contentDescription = null
                    )
                }
            }
            AnimatedVisibility(visible = trimState) {
                Column(
                    modifier = Modifier.fillMaxWidth(),
                    verticalArrangement = Arrangement.spacedBy(0.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    RangeSliderAudio(
                        valueRange = totalDuration.longValue.toFloat(),
                        sliderPosition = sliderPosition,
                        onValueChange = {
                            sliderPosition = it
                        }, onValueChangeFinished = {
                            currentPosition.longValue = sliderPosition.start.toLong()
                            difference.longValue =
                                (sliderPosition.endInclusive - sliderPosition.start).toLong()
                            player.seekTo(sliderPosition.start.toLong())
                            onSave(sliderPosition)
                        })
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
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
                }
            }
        }
    }
}