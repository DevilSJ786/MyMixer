package com.media.mixer.screens.audioplayer.screen.playlist

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.basicMarquee
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.runtime.snapshots.SnapshotStateList
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.paint
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.media.mixer.R
import com.media.mixer.core.components.AppIcon
import com.media.mixer.core.components.NoItemFound
import com.media.mixer.core.utils.colorOfDropDown
import com.media.mixer.data.entities.Playlist
import com.media.mixer.screens.components.DropdownMenuItemCustom


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AudioPlayList(
    playlist: SnapshotStateList<Playlist>,
    bottomPadding: Dp,
    onPlaylist: (Playlist) -> Unit,
    onDeletePlaylist: (Playlist) -> Unit,
    onBack: () -> Unit
) {

    Scaffold(
        topBar = {
            CenterAlignedTopAppBar(
                title = {
                    Text(
                        text = "My Playlist",
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
            )
        }) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .padding(start = 16.dp, bottom = bottomPadding, end = 16.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            if (playlist.isEmpty()) {
                NoItemFound(message = "No Playlist Found", id = R.drawable.music_square_remove)
            }
            playlist.forEach {
                PlaylistItem(
                    playlist = it,
                    onClick = { onPlaylist(it) },
                    onDeletePlaylist = { onDeletePlaylist(it) })
            }
        }
    }
}

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun PlaylistItem(playlist: Playlist, onClick: () -> Unit, onDeletePlaylist: () -> Unit) {
    var isContextMenuVisible by rememberSaveable {
        mutableStateOf(false)
    }
    val type = if (playlist.isVideoPlaylist) "Video" else "Songs"
    Card(
        modifier = Modifier.fillMaxWidth(),
        onClick = { onClick() },
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(start = 16.dp, top = 16.dp, bottom = 16.dp, end = 8.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(16.dp)
        ) {

            AppIcon(
                painter = painterResource(id = R.drawable.music_filter_play),
                contentDescription = null,
                modifier = Modifier
                    .size(45.dp)
                    .clip(MaterialTheme.shapes.small),
            )
            Column(
                modifier = Modifier
                    .weight(1f, fill = false)
                    .fillMaxWidth(), horizontalAlignment = Alignment.Start
            ) {
                Text(
                    modifier = Modifier
                        .basicMarquee(),
                    text = playlist.name,
                    color = Color.White,
                    style = MaterialTheme.typography.titleMedium,
                    maxLines = 1
                )
                Text(
                    modifier = Modifier
                        .basicMarquee(),
                    text = "${playlist.count} $type",
                    color = Color.White,
                    style = MaterialTheme.typography.titleMedium,
                    maxLines = 1
                )
            }

            Box {
                AppIcon(
                    modifier = Modifier.clickable { isContextMenuVisible = true },
                    imageVector = Icons.Default.MoreVert,
                    contentDescription = null
                )
                DropdownMenu(
                    modifier = Modifier.background(colorOfDropDown),
                    expanded = isContextMenuVisible,
                    onDismissRequest = {
                        isContextMenuVisible = false
                    },
                ) {
                    DropdownMenuItemCustom(
                        contentPadding = PaddingValues(vertical = 4.dp, horizontal = 8.dp),
                        text = {
                            Text(
                                "Delete Playlist",
                                color = Color.White,
                                style = MaterialTheme.typography.titleSmall
                            )
                        },
                        onClick = {
                            isContextMenuVisible = false
                            onDeletePlaylist()
                        }
                    )
                }
            }
        }
    }
}

