package com.media.mixer.screens.audioplayer.screen.playlist

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.runtime.snapshots.SnapshotStateList
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.paint
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import com.media.mixer.R
import com.media.mixer.core.components.AppIcon
import com.media.mixer.core.components.CreatePlaylistPopUp
import com.media.mixer.core.components.NoItemFound
import com.media.mixer.core.components.PlaylistPopUp
import com.media.mixer.core.utils.shareContent
import com.media.mixer.data.entities.Playlist
import com.media.mixer.data.entities.Song
import com.media.mixer.media.audio.common.MusicState
import com.media.mixer.screens.audioplayer.screen.music.TrackItem

@OptIn(ExperimentalMaterial3Api::class, ExperimentalFoundationApi::class)
@Composable
fun PlaylistScreen(
    playlistName: String,
    list: SnapshotStateList<Song>,
    musicState: MusicState,
    playlist: SnapshotStateList<Playlist>,
    addNewPlaylist: (String) -> Unit,
    addSongInPlaylist: (Song, Long) -> Unit,
    addFavourite: (Int) -> Unit,
    onAudioEvent: (Int, Boolean) -> Unit,
    onBack: () -> Unit
) {
    val lazyListState = rememberLazyListState()
    val context = LocalContext.current
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
        topBar = {
            CenterAlignedTopAppBar(
                title = {
                    Text(
                        text = playlistName, style = MaterialTheme.typography.titleLarge,
                        color = Color.White
                    )
                },
                navigationIcon = {
                    IconButton(onClick = onBack) {
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
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            LazyColumn(
                modifier = Modifier.fillMaxSize(),
                verticalArrangement = Arrangement.spacedBy(8.dp),
                state = lazyListState
            ) {
                if (list.isEmpty()){
                    item {
                        NoItemFound(message = "No Audio File Available\nIn Your Playlist", id = R.drawable.music_square_remove)
                    }
                }
                itemsIndexed(items = list) { index: Int, item: Song ->
                    TrackItem(modifier = Modifier
                        .fillMaxWidth()
                        .animateItemPlacement(),
                        musicState = musicState,
                        song = item,
                        onClick = { isRunning ->
                            onAudioEvent(index, isRunning)
                        },
                        addFavourite = { addFavourite(item.id) },
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