package com.media.mixer.screens.ringtones

import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.Crossfade
import androidx.compose.animation.core.spring
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.Image
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
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Tab
import androidx.compose.material3.TabRow
import androidx.compose.material3.TabRowDefaults.tabIndicatorOffset
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableLongStateOf
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
import androidx.core.net.toUri
import androidx.media3.common.Player
import com.media.mixer.R
import com.media.mixer.core.components.AppIcon
import com.media.mixer.core.components.ProgressScreen
import com.media.mixer.core.utils.TransformerState
import com.media.mixer.core.utils.convertToText
import com.media.mixer.data.entities.Song
import com.media.mixer.media.audio.common.MusicState
import com.media.mixer.media.presentation.PlayerImage2
import com.media.mixer.screens.audiocutter.AudioSelectionScreen
import com.media.mixer.screens.audiocutter.CutAudioScreen
import com.media.mixer.screens.components.ActionItem
import com.media.mixer.screens.components.BackHandler
import com.media.mixer.screens.components.SetAsPopUp
import com.media.mixer.screens.components.SetAudioPopUp
import com.media.mixer.screens.components.TrackSlider
import com.media.mixer.screens.merger.MergerAudiosScreen
import com.media.mixer.screens.videotoaudio.RingtoneUiState

import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun RingtoneScreen(
    allAudio: List<Song>,
    ringtoneUiState: RingtoneUiState,
    transformerState: TransformerState,
    player: Player,
    musicState: MusicState,
    title: String,
    duration: Long,
    listOfSong: SnapshotStateList<Song>,
    goToOutPut: (Song) -> Unit,
    onCut: () -> Unit,
    onMerge: () -> Unit,
    onDelete: () -> Unit,
    setRingtone: () -> Unit,
    setPlayer: (Song) -> Unit,
    onOutPut: () -> Unit,
    removeAt: (Int) -> Unit,
    onSaveMerge: (List<ClosedFloatingPointRange<Float>>) -> Unit,
    onSaveCut: (ClosedFloatingPointRange<Float>) -> Unit,
    setAlarm: () -> Unit,
    setNotification: () -> Unit,
    onStartMerge: (Song) -> Unit,
    onBack: () -> Unit
) {
    BackHandler {
        onBack()
    }

    Scaffold(
        topBar = {
            CenterAlignedTopAppBar(title = {
                Text(
                    text = "Ringtones",
                    color = Color.White,
                    style = MaterialTheme.typography.titleLarge
                )
            },
                colors = TopAppBarDefaults.centerAlignedTopAppBarColors(containerColor = Color.Transparent),
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        AppIcon(painter = painterResource(id = R.drawable.back_))
                    }
                })
        })
    { paddingValues ->

        when (ringtoneUiState) {
            is RingtoneUiState.RingtoneHome -> {
                RingtoneHome(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(paddingValues)
                        .padding(16.dp),
                    allAudio = allAudio,
                ) {
                    goToOutPut(it)
                }
            }

            is RingtoneUiState.Selection -> {
                AudioSelectionScreen(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(paddingValues),
                    allAudio = allAudio,
                    onNext = onStartMerge
                )
            }

            is RingtoneUiState.Merge -> {
                MergerAudiosScreen(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(paddingValues)
                        .padding(16.dp),
                    list = listOfSong,
                    player = player,
                    musicState = musicState,
                    setPlayer = setPlayer,
                    onSave = onSaveMerge,
                    removeAt = removeAt
                )
            }

            is RingtoneUiState.Loading -> {
                ProgressScreen(
                    paddingValues = paddingValues,
                    transformerState = transformerState,
                    onSaveProcessState = onOutPut,
                    onBack = onBack
                )
            }

            is RingtoneUiState.OutPut -> {
                RingtoneOutPutScreen(
                    title = "SongOutPut",
                    player = player,
                    musicState = musicState,
                    padding = paddingValues,
                    setRingtone = setRingtone,
                    setAlarm = setAlarm,
                    setNotification = setNotification,
                    onMerge = onMerge,
                    onCut = onCut,
                    onDelete = onDelete
                )
            }

            is RingtoneUiState.Cut -> {
                CutAudioScreen(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(paddingValues)
                        .padding(16.dp),
                    player = player,
                    title = title,
                    duration = duration,
                    musicState = musicState,
                    onSave = onSaveCut
                )
            }
        }
    }
}


@OptIn(ExperimentalFoundationApi::class)
@Composable
fun RingtoneHome(
    modifier: Modifier = Modifier,
    allAudio: List<Song>,
    onClick: (Song) -> Unit
) {

    val lazyListState1 = rememberLazyListState()
    Column(
        modifier = modifier,
        verticalArrangement = Arrangement.spacedBy(0.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        LazyColumn(
            modifier = Modifier.fillMaxSize(),
            verticalArrangement = Arrangement.spacedBy(8.dp),
            state = lazyListState1
        ) {
            itemsIndexed(items = allAudio) { _: Int, item: Song ->
                RingtoneItem(modifier = Modifier
                    .fillMaxWidth()
                    .animateItemPlacement(),
                    song = item,
                    onSelected = { onClick(item) }
                )
            }
        }
    }
}

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun RingtoneItem(
    modifier: Modifier = Modifier,
    song: Song,
    onSelected: () -> Unit,
) {
    Card(
        onClick = onSelected,
        modifier = modifier.fillMaxWidth(), shape = RoundedCornerShape(16.dp),
    ) {
        Row(
            modifier = modifier
                .fillMaxWidth()
                .padding(start = 16.dp, top = 16.dp, bottom = 16.dp, end = 8.dp),
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
            AppIcon(painter = painterResource(id = R.drawable.right_))
        }
    }

}

@Composable
fun RingtoneOutPutScreen(
    title: String,
    player: Player,
    musicState: MusicState,
    padding: PaddingValues,
    setRingtone: () -> Unit,
    setAlarm: () -> Unit,
    setNotification: () -> Unit,
    onMerge: () -> Unit,
    onCut: () -> Unit,
    onDelete: () -> Unit,
) {
    var popupSetAs by remember {
        mutableStateOf(false)
    }
    var popupRingtone by remember {
        mutableStateOf(false)
    }
    var popupAlarm by remember {
        mutableStateOf(false)
    }
    var popupMessage by remember {
        mutableStateOf(false)
    }
    var popupContact by remember {
        mutableStateOf(false)
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(padding)
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        RingtoneOutPutTrack(
            title = title,
            player = player,
            musicState = musicState,
            onCut = onCut,
            onMerge = onMerge,
            onSet = { popupSetAs = true },
            onDelete = onDelete
        )
    }

    if (popupSetAs) {
        SetAsPopUp(
            player = player,
            trackImageUrl = "fdsaf".toUri(),
            title = title,
            onRingtone = {
                popupSetAs = false
                popupRingtone = true
            },
            onAlarm = {
                popupSetAs = false
                popupAlarm = true
            },
            onMessage = {
                popupSetAs = false
                popupMessage = true
            },
            onContact = {
                popupSetAs = false
                popupContact = true
            }) {
            popupSetAs = false
        }
    }
    if (popupRingtone) {
        SetAudioPopUp(
            title = "Set Ringtone?",
            description = "Your Ringtone is now ready !\n" +
                    "Proceed with this Ringtone ?",
            titleSong = "Song name",
            player = player,
            trackImageUrl = "fdsaf".toUri(),
            onSave = {
                setRingtone()
                popupRingtone = false
            }) {
            popupRingtone = false
        }
    }
    if (popupAlarm) {
        SetAudioPopUp(
            title = "Set Alarm?",
            titleSong = "Song name",
            description = "Your Ringtone is now ready !\n" +
                    "Proceed with this Ringtone ?",
            player = player,
            trackImageUrl = "fdsaf".toUri(),
            onSave = {
                setAlarm()
                popupAlarm = false
            }) {
            popupAlarm = false
        }
    }
    if (popupMessage) {
        SetAudioPopUp(
            title = "Set Message?",
            description = "Your Ringtone is now ready !\n" +
                    "Proceed with this Ringtone ?",
            titleSong = "Song name",
            player = player,
            trackImageUrl = "fdsaf".toUri(),
            onSave = {
                setNotification()
                popupMessage = false
            }) {
            popupMessage = false
        }
    }
    if (popupContact) {
        SetAudioPopUp(
            title = "Set Contact?",
            description = "Your Ringtone is now ready !\n" +
                    "Proceed with this Ringtone ?",
            titleSong = "Song name",
            player = player,
            trackImageUrl = "fdsaf".toUri(),
            onSave = {
                popupContact = false
            }) {
            popupContact = false
        }
    }
}

@Composable
fun RingtoneOutPutTrack(
    title: String,
    player: Player,
    musicState: MusicState,
    onSet: () -> Unit,
    onMerge: () -> Unit,
    onCut: () -> Unit,
    onDelete: () -> Unit,
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
    }

    LaunchedEffect(currentPosition.longValue) {
        sliderPosition.longValue = currentPosition.longValue
    }

    LaunchedEffect(player.duration) {
        if (player.duration > 0) {
            totalDuration.longValue = player.duration
        }
    }
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(12.dp),
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(0.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Box(
                    contentAlignment = Alignment.Center
                ) {
                    PlayerImage2(
                        modifier = Modifier.size(45.dp),
                        trackImageUrl = musicState.currentSong.artworkUri,
                    )
                    Crossfade(
                        targetState = musicState.playWhenReady,
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
                                onClick = { player.play() },
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
                Column {
                    Text(
                        text = title,
                        color = Color.White,
                        style = MaterialTheme.typography.titleMedium,
                        overflow = TextOverflow.Ellipsis
                    )
                    Text(
                        text = "size",
                        color = Color.White,
                        style = MaterialTheme.typography.bodyMedium
                    )
                }
                Spacer(modifier = Modifier.weight(1f))
                IconButton(onClick = {
                    player.pause()
                    onDelete()
                }) {
                    AppIcon(
                        painter = painterResource(id = R.drawable.trash),
                        contentDescription = null
                    )
                }
            }
            Box(modifier = Modifier.fillMaxWidth(), contentAlignment = Alignment.Center){
                Image(painter = painterResource(id = R.drawable.play_anim), contentDescription =null )
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
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                ActionItem(
                    title = "Set",
                    id = R.drawable.notification,
                    onClick = {
                        player.pause()
                        onSet()
                    }
                )
                ActionItem(
                    title = "Merger",
                    id = R.drawable.merger,
                    onClick = {
                        player.pause()
                        onMerge()
                    }
                )
                ActionItem(title = "Cut", id = R.drawable.audio_cutter, onClick = {
                    player.pause()
                    onCut()
                })
            }
        }
    }
}