package com.media.mixer.screens.audioplayer.screen.music

import androidx.compose.animation.AnimatedContent
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.basicMarquee
import androidx.compose.foundation.clickable
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
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material3.Card
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.DropdownMenu
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
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.runtime.snapshots.SnapshotStateList
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.paint
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.media.mixer.R
import com.media.mixer.core.components.AppIcon
import com.media.mixer.core.components.CreatePlaylistPopUp
import com.media.mixer.core.components.NoItemFound
import com.media.mixer.core.components.PlaylistPopUp
import com.media.mixer.core.utils.convertToText
import com.media.mixer.core.utils.listOfColorForLine
import com.media.mixer.core.utils.shareContent
import com.media.mixer.data.entities.Playlist
import com.media.mixer.data.entities.Song
import com.media.mixer.media.audio.common.MusicState
import com.media.mixer.media.presentation.PlayerImage2
import com.media.mixer.screens.audioplayer.screen.State
import com.media.mixer.screens.components.DropdownMenuItemCustom
import kotlinx.coroutines.launch


@OptIn(ExperimentalFoundationApi::class, ExperimentalMaterial3Api::class)
@Composable
fun AudioHomeContent(
    musicList: List<Song>,
    musicListFav: SnapshotStateList<Song>,
    musicState: MusicState,
    playlist: SnapshotStateList<Playlist>,
    onAudioEvent: (Int, Boolean, State) -> Unit,
    addSongInPlaylist: (Song, Long) -> Unit,
    addNewPlaylist: (String) -> Unit,
    addFavourite: (Int) -> Unit,
    removeFavourite: (Int) -> Unit,
    onBack: () -> Unit
) {
    val context = LocalContext.current
    val lazyListState = rememberLazyListState()
    val lazyListState1 = rememberLazyListState()
    var playlistPopup by remember {
        mutableStateOf(false)
    }
    var selectPlaylistPopup by remember {
        mutableStateOf(false)
    }
    val state = rememberPagerState {
        2
    }
    val scope = rememberCoroutineScope()
    var song by remember {
        mutableStateOf<Song?>(null)
    }
    Scaffold(
        topBar = {
            CenterAlignedTopAppBar(
                title = {
                    Text(
                        text = "My Music",
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
                colors = TopAppBarDefaults.centerAlignedTopAppBarColors(containerColor = Color.Transparent)
            )
        }) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .padding(horizontal = 16.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            TabRow(
                selectedTabIndex = state.currentPage,
                containerColor = Color.Transparent,
                indicator = {
                    Box(
                        modifier = Modifier
                            .tabIndicatorOffset(it[state.currentPage])
                            .height(4.dp)
                            .background(color = Color(0xFF09FBD3), shape = RoundedCornerShape(50))
                    )
                }) {
                Tab(selected = state.currentPage == 0, onClick = {
                    scope.launch { state.animateScrollToPage(0) }
                }) {
                    Text(
                        text = "SONGS",
                        modifier = Modifier.padding(16.dp),
                        style = MaterialTheme.typography.titleMedium,
                        textAlign = TextAlign.Center,
                        color = if (state.currentPage != 0) Color.White else Color(0xFF09FBD3),
                    )
                }
                Tab(selected = state.currentPage == 1, onClick = {
                    scope.launch { state.animateScrollToPage(1) }
                }) {
                    Text(
                        text = "FAVORITE",
                        modifier = Modifier.padding(16.dp),
                        style = MaterialTheme.typography.titleMedium,
                        textAlign = TextAlign.Center,
                        color = if (state.currentPage != 1) Color.White else Color(0xFF09FBD3),
                    )
                }
            }

            HorizontalPager(state = state) { pageIndex ->
                AnimatedContent(targetState = pageIndex, label = "") {
                    if (it == 0) {
                        LazyColumn(
                            modifier = Modifier.fillMaxSize(),
                            verticalArrangement = Arrangement.spacedBy(8.dp),
                            state = lazyListState
                        ) {
                            if (musicList.isEmpty()) {
                                item {
                                    NoItemFound(
                                        message = "No Audio File Available\nIn Your Songs",
                                        id = R.drawable.music_square_remove
                                    )
                                }
                            }
                            itemsIndexed(
                                items = musicList,
                                key = { _, i -> i.id }) { index: Int, item: Song ->
                                TrackItem(modifier = Modifier
                                    .fillMaxWidth()
                                    .animateItemPlacement(),
                                    musicState = musicState,
                                    song = item,
                                    onClick = { isRunning ->
                                        onAudioEvent(index, isRunning, State.SONGS)
                                    }, addFavourite = { addFavourite(item.id) },
                                    addPlaylist = {
                                        selectPlaylistPopup = true
                                        song = item
                                    },
                                    onShare = {
                                        shareContent(
                                            path = item.mediaUri.toString(),
                                            context = context
                                        )
                                    })
                            }
                        }
                    } else {
                        LazyColumn(
                            modifier = Modifier.fillMaxSize(),
                            verticalArrangement = Arrangement.spacedBy(8.dp),
                            state = lazyListState1
                        ) {
                            if (musicListFav.isEmpty()) {
                                item {
                                    NoItemFound(
                                        message = "No Audio File Available\nIn Your Favourite",
                                        id = R.drawable.music_square_remove
                                    )
                                }
                            }
                            itemsIndexed(
                                items = musicListFav,
                                key = { _, i -> i.id }) { index: Int, item: Song ->
                                TrackFavItem(modifier = Modifier
                                    .fillMaxWidth()
                                    .animateItemPlacement(),
                                    musicState = musicState,
                                    song = item,
                                    onClick = { isRunning ->
                                        onAudioEvent(index, isRunning, State.FAVORITE)
                                    },
                                    removeFavourite = { removeFavourite(item.id) }
                                )
                            }
                        }
                    }
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
    }
}


@OptIn(ExperimentalFoundationApi::class)
@Composable
fun TrackItem(
    modifier: Modifier = Modifier,
    musicState: MusicState,
    song: Song,
    onClick: (Boolean) -> Unit,
    addFavourite: () -> Unit,
    addPlaylist: () -> Unit,
    onShare: () -> Unit,
) {
    val isRunning = musicState.currentSong.id == song.id
    val textColor = if (isRunning) Color.Yellow
    else Color.White
    var isContextMenuVisible by rememberSaveable {
        mutableStateOf(false)
    }
    Card(
        modifier = modifier.fillMaxWidth(),
        onClick = { onClick(isRunning) }
    ) {
        Row(
            modifier = Modifier
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
                    color = textColor,
                    style = MaterialTheme.typography.titleMedium,
                    maxLines = 1
                )
                Text(
                    text = song.duration.toLong().convertToText(),
                    color = textColor,
                    style = MaterialTheme.typography.bodyMedium
                )
            }
            Box {
                AppIcon(
                    modifier = Modifier.clickable { isContextMenuVisible = true },
                    imageVector = Icons.Default.MoreVert,
                    contentDescription = null
                )
                DropdownMenu(
                    expanded = isContextMenuVisible,
                    onDismissRequest = {
                        isContextMenuVisible = false
                    }
                ) {
                    DropdownMenuItemCustom(
                        contentPadding = PaddingValues(vertical = 4.dp, horizontal = 8.dp),
                        text = {
                            Text(
                                "Add  Favourite",
                                style = MaterialTheme.typography.titleSmall
                            )
                        },
                        onClick = {
                            isContextMenuVisible = false
                            addFavourite()
                        }
                    )
                    Spacer(
                        modifier = Modifier
                            .height(2.dp)
                            .fillMaxWidth()
                            .background(Brush.linearGradient(listOfColorForLine))
                    )
                    DropdownMenuItemCustom(
                        contentPadding = PaddingValues(vertical = 4.dp, horizontal = 8.dp),
                        text = {
                            Text(
                                "Add  Playlist",
                                style = MaterialTheme.typography.titleSmall
                            )
                        },
                        onClick = {
                            isContextMenuVisible = false
                            addPlaylist()
                        }
                    )
                    Spacer(
                        modifier = Modifier
                            .height(2.dp)
                            .fillMaxWidth()
                            .background(Brush.linearGradient(listOfColorForLine))
                    )
                    DropdownMenuItemCustom(
                        contentPadding = PaddingValues(vertical = 4.dp, horizontal = 8.dp),
                        text = {
                            Text(
                                "Share",
                                style = MaterialTheme.typography.titleSmall
                            )
                        },
                        onClick = {
                            isContextMenuVisible = false
                            onShare()
                        }
                    )
                }
            }
        }
    }

}

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun TrackFavItem(
    modifier: Modifier = Modifier,
    musicState: MusicState,
    song: Song,
    onClick: (Boolean) -> Unit,
    removeFavourite: () -> Unit = {},
) {
    val isRunning = musicState.currentSong.id == song.id
    val textColor = if (isRunning) Color.Yellow
    else Color.White
    var isContextMenuVisible by rememberSaveable {
        mutableStateOf(false)
    }
    Card(
        modifier = modifier
            .fillMaxWidth(),
        onClick = { onClick(isRunning) },
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
                    color = textColor,
                    style = MaterialTheme.typography.titleMedium,
                    maxLines = 1
                )
                Text(
                    text = song.duration.toLong().convertToText(),
                    color = textColor,
                    style = MaterialTheme.typography.bodyMedium
                )
            }
            Box {
                AppIcon(
                    modifier = Modifier.clickable { isContextMenuVisible = true },
                    imageVector = Icons.Default.MoreVert,
                    contentDescription = null
                )
                DropdownMenu(
                    expanded = isContextMenuVisible,
                    onDismissRequest = {
                        isContextMenuVisible = false
                    }
                ) {
                    DropdownMenuItemCustom(
                        contentPadding = PaddingValues(vertical = 4.dp, horizontal = 8.dp),
                        text = {
                            Text(
                                "Remove from Favourite",
                                style = MaterialTheme.typography.titleSmall
                            )
                        },
                        onClick = {
                            isContextMenuVisible = false
                            removeFavourite()
                        }
                    )
                }
            }
        }
    }

}