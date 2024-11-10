package com.media.mixer.screens.audioplayer.screen

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.Crossfade
import androidx.compose.animation.core.spring
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.basicMarquee
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.FavoriteBorder
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.media.mixer.R
import com.media.mixer.core.components.AppIcon
import com.media.mixer.core.components.BottomNavigationBar
import com.media.mixer.core.components.CreatePlaylistPopUp
import com.media.mixer.core.components.PlaylistPopUp
import com.media.mixer.core.utils.listOfColorForBorder
import com.media.mixer.core.utils.listOfColorForPlayer
import com.media.mixer.data.entities.Song
import com.media.mixer.media.domain.utils.AppIcons
import com.media.mixer.media.domain.utils.asFormattedString
import com.media.mixer.media.presentation.PlayerEvent
import com.media.mixer.media.presentation.PlayerImage2
import com.media.mixer.screens.audioplayer.navigation.AudioDestination
import com.media.mixer.screens.audioplayer.navigation.AudioNavGraph
import com.media.mixer.screens.player.PlayListViewModel

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun MusicHome(playListViewModel: PlayListViewModel = hiltViewModel(), onBack: () -> Unit) {
    val musicState by playListViewModel.musicState.collectAsStateWithLifecycle()
    val playlist by playListViewModel.playList.collectAsStateWithLifecycle()
    val navController = rememberNavController()
    val navBackStackEntry by navController.currentBackStackEntryAsState()
    var cardVisibility by remember {
        mutableStateOf(false)
    }
    var favSong by remember {
        mutableStateOf(false)
    }
    LaunchedEffect(key1 = musicState, key2 = navBackStackEntry) {
        cardVisibility =
            musicState.currentSong.id != -1 && !navBackStackEntry?.destination?.route.equals(
                AudioDestination.AudioPlayer.root
            )
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
    Scaffold(
        bottomBar = {
            BottomNavigationBar(navController = navController)
        }) {
        Box(
            modifier = Modifier
                .padding(bottom = it.calculateBottomPadding())
                .fillMaxSize()
        ) {
            AudioNavGraph(
                navController = navController,
                playListViewModel = playListViewModel,
                onBack = onBack
            )
            AnimatedVisibility(
                visible = cardVisibility,
                modifier = Modifier.align(Alignment.BottomCenter)
            ) {
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(Brush.linearGradient(listOfColorForPlayer)),
                    colors = CardDefaults.cardColors(containerColor = Color.Transparent),
                    onClick = {
                        navController.navigate(AudioDestination.AudioPlayer.root)
                        cardVisibility = false
                    }
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(8.dp),
                        horizontalArrangement = Arrangement.spacedBy(0.dp)
                    ) {
                        PlayerImage2(
                            modifier = Modifier.size(45.dp),
                            trackImageUrl = musicState.currentSong.artworkUri,
                        )
                        Spacer(modifier = Modifier.width(8.dp))

                        Column(
                            modifier = Modifier.fillMaxWidth()
                                .weight(1f, fill = false),
                            horizontalAlignment = Alignment.Start,
                            verticalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            Text(
                                modifier = Modifier.basicMarquee(),
                                text = musicState.currentSong.title,
                                style = MaterialTheme.typography.titleMedium,
                                color = Color.White
                            )
                            Text(
                                modifier = Modifier.basicMarquee(),
                                text = musicState.duration.asFormattedString(),
                                style = MaterialTheme.typography.bodyMedium,
                                color = Color.White
                            )
                        }
                        IconButton(
                            onClick = {
                                favSong = !favSong
                                playListViewModel.updateSongFav(musicState.currentSong.id, favSong)
                            }) {
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
                        IconButton(onClick = {
                            selectPlaylistPopup = true
                            song = musicState.currentSong
                        }) {
                            AppIcon(
                                painter = painterResource(id = R.drawable.music_filter_white),
                                contentDescription = null
                            )
                        }

                        Crossfade(
                            targetState = musicState.playWhenReady, animationSpec = spring(),
                            label = ""
                        ) { targetPlayWhenReady ->
                            if (targetPlayWhenReady) {
                                IconButton(
                                    modifier = Modifier.border(
                                        BorderStroke(2.dp, Brush.linearGradient(listOfColorForBorder)),
                                        CircleShape
                                    ),
                                    onClick = { playListViewModel.onAudioEvent(PlayerEvent.Pause) },
                                ) {
                                    AppIcon(
                                        painter = painterResource(id = AppIcons.Pause.resourceId),
                                        contentDescription = stringResource(id = R.string.play)
                                    )
                                }
                            } else {
                                IconButton(
                                    modifier = Modifier.border(
                                        BorderStroke(2.dp, Brush.linearGradient(listOfColorForBorder)),
                                        CircleShape
                                    ),
                                    onClick = { playListViewModel.onAudioEvent(PlayerEvent.Play) },
                                ) {
                                    AppIcon(
                                        painter = painterResource(id = AppIcons.Play.resourceId),
                                        contentDescription = stringResource(id = R.string.play)
                                    )
                                }
                            }
                        }
                    }
                }
            }
            if (playlistPopup) {
                CreatePlaylistPopUp(onSave = { name ->
                    playlistPopup = false
                    playListViewModel.addNewPlaylist(name)
                }) {
                    playlistPopup = false
                }
            }
            if (selectPlaylistPopup) {
                PlaylistPopUp(list = playlist, newPlaylist = {
                    selectPlaylistPopup = false
                    playlistPopup = true
                }, onSave = { id ->
                    selectPlaylistPopup = false
                    playListViewModel.addSongToPlaylist(song!!, id)
                    song = null
                }) {
                    selectPlaylistPopup = false
                }
            }
        }
    }
}