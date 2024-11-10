package com.media.mixer.media.presentation

import android.annotation.SuppressLint
import androidx.activity.OnBackPressedCallback
import androidx.compose.animation.Crossfade
import androidx.compose.animation.core.spring
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.basicMarquee
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.FavoriteBorder
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.SideEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableLongStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.runtime.snapshots.SnapshotStateList
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.media3.common.Player
import coil.compose.AsyncImage
import coil.request.ImageRequest
import com.media.mixer.R
import com.media.mixer.core.components.AppIcon
import com.media.mixer.core.components.CreatePlaylistPopUp
import com.media.mixer.core.components.PlaylistPopUp
import com.media.mixer.core.utils.convertToText
import com.media.mixer.core.utils.listOfColorForBorder
import com.media.mixer.core.utils.shareContent
import com.media.mixer.data.entities.Playlist
import com.media.mixer.data.entities.Song
import com.media.mixer.media.audio.common.MusicState
import com.media.mixer.media.domain.utils.findActivity
import com.media.mixer.media.media.rememberControllerState
import com.media.mixer.media.media.rememberMediaState
import com.media.mixer.screens.components.TrackSlider
import kotlinx.coroutines.delay


@OptIn(ExperimentalMaterial3Api::class, ExperimentalFoundationApi::class)
@SuppressLint("UnusedMaterial3ScaffoldPaddingParameter")
@Composable
fun PlayerScreen(
    player: Player,
    musicState: MusicState,
    playlist: SnapshotStateList<Playlist>,
    addSongInPlaylist: (Song, Long) -> Unit,
    addNewPlaylist: (String) -> Unit,
    addFavourite: (Int, Boolean) -> Unit,
    onBack: () -> Unit,
) {
    var favSong by remember {
        mutableStateOf(false)
    }
    LaunchedEffect(key1 = musicState) {
        favSong = musicState.currentSong.isFev
    }
    var playlistPopup by remember {
        mutableStateOf(false)
    }
    var selectPlaylistPopup by remember {
        mutableStateOf(false)
    }
    var song by remember {
        mutableStateOf<Song?>(null)
    }
    val state = rememberMediaState(player = player)
    val controllerState = rememberControllerState(state)
    val context = LocalContext.current
    Scaffold(
        modifier = Modifier
            .fillMaxSize(),
        topBar = {
            CenterAlignedTopAppBar(
                title = {
                    Text(
                        text = "Audio Player",
                        style = MaterialTheme.typography.titleLarge,
                        color = Color.White
                    )
                },
                navigationIcon = {
                    IconButton(
                        onClick = onBack
                    ) {
                        AppIcon(painter = painterResource(id = R.drawable.back_))
                    }
                },
                actions = {
                    IconButton(
                        onClick = {
                            shareContent(
                                musicState.currentSong.mediaUri.toString(),
                                context = context
                            )
                        }
                    ) {
                        AppIcon(painter = painterResource(id = R.drawable.share))
                    }
                },
                colors = TopAppBarDefaults.centerAlignedTopAppBarColors(containerColor = Color.Transparent)
            )
        }
    ) { it ->

        val activity = LocalContext.current.findActivity()!!
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

        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 16.dp)
                .padding(top = it.calculateTopPadding(), bottom = it.calculateBottomPadding()),
            verticalArrangement = Arrangement.Top,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            AsyncImage(
                model = ImageRequest.Builder(LocalContext.current)
                    .data(
                        state.playerState?.mediaMetadata?.artworkData
                            ?: musicState.currentSong.artworkUri
                    )
                    .crossfade(true)
                    .build(),
                contentDescription = null,
                contentScale = ContentScale.Crop,
                modifier = Modifier
                    .fillMaxWidth()
                    .aspectRatio(1f)
                    .padding(bottom = 16.dp)
                    .clip(MaterialTheme.shapes.medium),
                error = painterResource(id = R.drawable.music_cover),
                placeholder = painterResource(id = R.drawable.music_cover)
            )
            Column(
                modifier = Modifier.fillMaxWidth(),
                verticalArrangement = Arrangement.spacedBy(8.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 4.dp),
                    horizontalAlignment = Alignment.Start,
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Text(
                        modifier = Modifier.basicMarquee(),
                        text = musicState.currentSong.title.ifEmpty { "Unknown" },
                        style = MaterialTheme.typography.titleMedium,
                        maxLines = 1,
                        color = Color.White
                    )
                    Text(
                        text = musicState.currentSong.artist.ifEmpty { "Unknown" },
                        style = MaterialTheme.typography.bodyMedium,
                        color = Color.White
                    )
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
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 4.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceAround
                ) {
                    Text(
                        text = (currentPosition.longValue).convertToText(),
                        modifier = Modifier.weight(1f),
                        color = Color.White,
                        style = TextStyle(fontWeight = FontWeight.Bold)
                    )
                    Spacer(modifier = Modifier.weight(1f))
                    Text(
                        text = if (totalDuration.longValue >= 0) totalDuration.longValue.convertToText() else "",
                        color = Color.White,
                        style = TextStyle(fontWeight = FontWeight.Bold)
                    )
                }
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 4.dp),
                    horizontalArrangement = Arrangement.SpaceEvenly,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    IconButton(
                        onClick = {
                            favSong = !favSong
                            addFavourite(musicState.currentSong.id, favSong)
                        }
                    ) {
                        Crossfade(
                            animationSpec = spring(),
                            targetState = favSong,
                            label = ""
                        ) { fav ->
                            if (fav) AppIcon(
                                imageVector = Icons.Default.Favorite,
                                contentDescription = null
                            )
                            else AppIcon(
                                imageVector = Icons.Default.FavoriteBorder,
                                contentDescription = null
                            )
                        }

                    }
                    IconButton(
                        onClick = { player.seekToPrevious() },
                    ) {
                        AppIcon(
                            painter = painterResource(id = R.drawable.previous),
                            contentDescription = null
                        )
                    }

                    IconButton(
                        onClick = {
                            controllerState.playOrPause()
                        },
                        modifier = Modifier
                            .size(50.dp)
                            .border(
                                BorderStroke(2.dp, Brush.linearGradient(listOfColorForBorder)),
                                CircleShape
                            )
                    ) {
                        AppIcon(
                            modifier = Modifier.padding(4.dp),
                            painter = painterResource(
                                if (controllerState.showPause) R.drawable.ic_video_pause_black
                                else R.drawable.ic_video_play_black
                            ),
                            contentDescription = null
                        )
                    }

                    IconButton(
                        onClick = { player.seekToNext() },
                    ) {
                        AppIcon(
                            painter = painterResource(id = R.drawable.next),
                            contentDescription = null
                        )
                    }
                    IconButton(
                        onClick = {
                            selectPlaylistPopup = true
                            song = musicState.currentSong
                        },
                    ) {
                        AppIcon(
                            painter = painterResource(id = R.drawable.music_filter_white),
                            contentDescription = null
                        )
                    }
                }
            }

        }
        val onBackPressedCallback = remember {
            object : OnBackPressedCallback(true) {
                override fun handleOnBackPressed() {
                    onBack()
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
        }
    }
    if (playlistPopup) {
        CreatePlaylistPopUp(onSave = {
            playlistPopup = false
            addNewPlaylist(it)
        }) {
            playlistPopup = false
        }
    }
    if (selectPlaylistPopup) {
        PlaylistPopUp(list = playlist, newPlaylist = {
            selectPlaylistPopup = false
            playlistPopup = true
        }, onSave = {
            selectPlaylistPopup = false
            addSongInPlaylist(song!!, it)
            song = null
        }) {
            selectPlaylistPopup = false
        }
    }

}


