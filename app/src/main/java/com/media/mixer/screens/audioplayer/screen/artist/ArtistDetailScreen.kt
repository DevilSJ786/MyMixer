package com.media.mixer.screens.audioplayer.screen.artist

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.aspectRatio
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
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImage
import coil.request.ImageRequest
import com.media.mixer.R
import com.media.mixer.core.components.AppIcon
import com.media.mixer.core.components.CreatePlaylistPopUp
import com.media.mixer.core.components.NoItemFound
import com.media.mixer.core.components.PlaylistPopUp
import com.media.mixer.core.utils.shareContent
import com.media.mixer.data.entities.Playlist
import com.media.mixer.data.entities.Song
import com.media.mixer.domain.model.Artist
import com.media.mixer.media.audio.common.MusicState
import com.media.mixer.screens.audioplayer.screen.music.TrackItem

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ArtistDetailScreen(
    title: String,
    artist: Artist,
    songs: SnapshotStateList<Song>,
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
                        text = title,
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
        }) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(it)
                .padding(horizontal = 16.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            Box(modifier = Modifier.fillMaxWidth()) {
                AsyncImage(
                    model = ImageRequest.Builder(LocalContext.current)
                        .data(artist.image)
                        .crossfade(true)
                        .build(),
                    contentDescription = null,
                    contentScale = ContentScale.FillBounds,
                    modifier = Modifier
                        .aspectRatio(16f / 9f)
                        .clip(MaterialTheme.shapes.small),
                    error = painterResource(id = R.drawable.music_),
                    placeholder = painterResource(id = R.drawable.music_)
                )
                Text(
                    modifier = Modifier
                        .align(Alignment.BottomCenter)
                        .padding(8.dp),
                    text = artist.name,
                    color = Color.White,
                    maxLines = 1,
                    style = MaterialTheme.typography.titleLarge
                )
            }
            LazyColumn(
                modifier = Modifier.fillMaxWidth(),
                state = lazyListState,
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                if (songs.isEmpty()){
                    item {
                        NoItemFound(message = "No Audio File Available\nIn Your Artist", id = R.drawable.music_square_remove)
                    }
                }
                itemsIndexed(songs) { index, item ->
                    TrackItem(musicState = musicState,
                        onClick = { isRunning ->
                            onAudioEvent(index, isRunning)
                        }, song = item,
                        onShare = {
                            shareContent(
                                path = item.mediaUri.toString(),
                                context = context
                            )
                        },
                        addFavourite = { addFavourite(item.id) },
                        addPlaylist = {
                            selectPlaylistPopup = true
                            song = item
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