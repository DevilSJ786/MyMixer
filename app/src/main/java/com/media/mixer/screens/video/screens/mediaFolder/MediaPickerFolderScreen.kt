package com.media.mixer.screens.video.screens.mediaFolder

import android.net.Uri
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.WindowInsetsSides
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.only
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.safeDrawing
import androidx.compose.foundation.layout.windowInsetsPadding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.PlayArrow
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.snapshots.SnapshotStateList
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.paint
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.media.mixer.R
import com.media.mixer.core.components.AppIcon
import com.media.mixer.core.utils.prettyName
import com.media.mixer.data.entities.Playlist
import com.media.mixer.domain.model.Video
import com.media.mixer.screens.video.composables.VideosView
import com.media.mixer.screens.video.screens.VideosState
import java.io.File

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun VideoFolderDetailsScreen(
    folderPath: String,
    playlist: SnapshotStateList<Playlist>,
    videosState: VideosState,
    bottomPadding: Dp,
    onVideoClick: (uri: Uri, String) -> Unit,
    onAddToSync: (Uri) -> Unit,
    addFavourite: (Long) -> Unit,
    addNewPlaylist: (String) -> Unit,
    addSongInPlaylist: (Video, Long) -> Unit,
    onBack: () -> Unit
) {
    Scaffold(
        modifier = Modifier
            .fillMaxSize()
            .windowInsetsPadding(WindowInsets.safeDrawing.only(WindowInsetsSides.Horizontal)),
        floatingActionButton = {
            FloatingActionButton(
                onClick = {
                    val state = videosState as? VideosState.Success
                    val videoToPlay = state?.recentPlayedVideo ?: state?.firstVideo
                    if (videoToPlay != null) {
                        onVideoClick(Uri.parse(videoToPlay.uriString), videoToPlay.displayName)
                    }
                }
            ) {
                Icon(
                    imageVector = Icons.Rounded.PlayArrow,
                    contentDescription = null
                )
            }
        },
        topBar = {
            CenterAlignedTopAppBar(
                title = {
                    Text(
                        text = File(folderPath).prettyName,
                        color = Color.White,
                        style = MaterialTheme.typography.titleLarge
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
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .padding(start = 16.dp, bottom = bottomPadding, end = 16.dp),
            contentAlignment = Alignment.Center
        ) {
            VideosView(
                videosState = videosState,
                playlist = playlist,
                onVideoClick = onVideoClick,
                onVideoLoaded = onAddToSync,
                addFavourite = addFavourite,
                addNewPlaylist = addNewPlaylist,
                addSongInPlaylist = addSongInPlaylist,
            )
        }
    }
}

