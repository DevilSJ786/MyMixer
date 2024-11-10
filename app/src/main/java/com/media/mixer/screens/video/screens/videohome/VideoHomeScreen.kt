package com.media.mixer.screens.video.screens.videohome

import android.net.Uri
import androidx.compose.animation.AnimatedContent
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.VideoCameraBack
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Tab
import androidx.compose.material3.TabRow
import androidx.compose.material3.TabRowDefaults.tabIndicatorOffset
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material3.surfaceColorAtElevation
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.runtime.snapshots.SnapshotStateList
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.min
import coil.compose.AsyncImage
import coil.request.ImageRequest
import com.media.mixer.R
import com.media.mixer.core.components.AppIcon
import com.media.mixer.core.components.CreatePlaylistPopUp
import com.media.mixer.core.components.NoItemFound
import com.media.mixer.core.components.PlaylistPopUp
import com.media.mixer.core.utils.shareContent
import com.media.mixer.data.entities.Playlist
import com.media.mixer.domain.model.Video
import com.media.mixer.screens.video.composables.CenterCircularProgressBar
import com.media.mixer.screens.video.composables.InfoChip
import com.media.mixer.screens.video.composables.MediaLazyList
import com.media.mixer.screens.video.composables.VideoItem
import com.media.mixer.screens.video.composables.VideoItemFav
import com.media.mixer.screens.video.screens.VideosState
import com.media.mixer.screens.video.screens.recentPlayedList
import kotlinx.coroutines.launch


@OptIn(ExperimentalFoundationApi::class, ExperimentalMaterial3Api::class)
@Composable
fun VideoHomeScreen(
    videosState: VideosState,
    videosStateFav: VideosState,
    playlist: SnapshotStateList<Playlist>,
    bottomPadding: Dp,
    onPlayVideo: (Uri, String) -> Unit,
    onAddToSync: (Uri) -> Unit,
    addToFav: (Video) -> Unit,
    onRemoveFav: (Video) -> Unit,
    addSongInPlaylist: (Video, Long) -> Unit,
    addNewPlaylist: (String) -> Unit,
    onBack: () -> Unit
) {

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
    var video by remember {
        mutableStateOf<Video?>(null)
    }
    val context = LocalContext.current
    Scaffold(
        topBar = {
            CenterAlignedTopAppBar(
                title = {
                    Text(
                        text = "My Videos",
                        color = Color.White,
                        style = MaterialTheme.typography.titleLarge
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
                .padding(start = 16.dp, bottom = bottomPadding, end = 16.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            TabRow(
                selectedTabIndex = state.currentPage,
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
                        text = "VIDEOS",
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
            when (videosState) {
                is VideosState.Success -> {
                    Text(
                        text = "History",
                        style = MaterialTheme.typography.titleMedium
                    )
                    LazyRow(
                        modifier = Modifier.fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(16.dp)
                    ) {
                        items(videosState.data.recentPlayedList()) { video ->
                            Box(
                                modifier = Modifier.clickable { onPlayVideo(Uri.parse(video.uriString), video.displayName) }
                                    .clip(MaterialTheme.shapes.small)
                                    .background(MaterialTheme.colorScheme.surfaceColorAtElevation(1.dp))
                                    .width(
                                        min(
                                            100.dp,
                                            LocalConfiguration.current.screenWidthDp.dp * 0.35f
                                        )
                                    )
                                    .aspectRatio(16f / 10f)
                            ) {
                                Icon(
                                    imageVector = Icons.Rounded.VideoCameraBack,
                                    contentDescription = null,
                                    tint = MaterialTheme.colorScheme.surfaceColorAtElevation(100.dp),
                                    modifier = Modifier
                                        .align(Alignment.Center)
                                        .fillMaxSize(0.5f)
                                )
                                AsyncImage(
                                    model = ImageRequest.Builder(context)
                                        .data(video.thumbnailPath)
                                        .crossfade(true)
                                        .build(),
                                    contentDescription = null,
                                    alignment = Alignment.Center,
                                    contentScale = ContentScale.Crop,
                                    modifier = Modifier.fillMaxSize()
                                )
                                InfoChip(
                                    text = video.formattedDuration,
                                    modifier = Modifier
                                        .padding(5.dp)
                                        .align(Alignment.BottomEnd),
                                    shape = MaterialTheme.shapes.extraSmall,
                                    backgroundColor =Color.Black.copy(alpha = 0.6f),
                                    contentColor = Color.White,
                                )
                            }
                        }
                    }
                    Text(
                        text = "My Videos",
                        style = MaterialTheme.typography.titleMedium
                    )
                }

                else -> {}
            }
            HorizontalPager(state = state) { pageIndex ->
                AnimatedContent(targetState = pageIndex, label = "") { index ->
                    if (index == 0) {
                        when (videosState) {
                            VideosState.Loading -> CenterCircularProgressBar()
                            is VideosState.Success -> if (videosState.data.isEmpty()) {
                                NoItemFound(
                                    message = "No Video File Available\nIn Your Device",
                                    id = R.drawable.music_square_remove
                                )
                            } else {
                                    MediaLazyList(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                                        items(videosState.data, key = { it.path }) { v ->
                                            LaunchedEffect(Unit) {
                                                onAddToSync(Uri.parse(v.uriString))
                                            }
                                            VideoItem(
                                                video = v,
                                                isRecentlyPlayedVideo = v == videosState.recentPlayedVideo,
                                                onClick = {
                                                    onPlayVideo(
                                                        Uri.parse(v.uriString),
                                                        v.displayName
                                                    )
                                                },
                                                addToFav = { addToFav(v) },
                                                addToPlaylist = {
                                                    selectPlaylistPopup = true
                                                    video = v
                                                },
                                                onShare = {
                                                    shareContent(
                                                        path = v.uriString,
                                                        context = context
                                                    )
                                                }
                                            )
                                        }
                                    }
                            }
                        }
                    } else {
                        when (videosStateFav) {
                            VideosState.Loading -> CenterCircularProgressBar()
                            is VideosState.Success -> if (videosStateFav.data.isEmpty()) {
                                NoItemFound(
                                    message = "No Video File Available\nIn Your Favourite",
                                    id = R.drawable.music_square_remove
                                )
                            } else {
                                    MediaLazyList(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                                        items(videosStateFav.data, key = { it.path }) { video ->
                                            LaunchedEffect(Unit) {
                                                onAddToSync(Uri.parse(video.uriString))
                                            }
                                            VideoItemFav(
                                                video = video,
                                                isRecentlyPlayedVideo = video == videosStateFav.recentPlayedVideo,
                                                onClick = {
                                                    onPlayVideo(
                                                        Uri.parse(video.uriString),
                                                        video.displayName
                                                    )
                                                },
                                                onRemoveFav = { onRemoveFav(video) }
                                            )
                                        }
                                    }
                            }
                        }
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
            addSongInPlaylist(video!!, it)
            video = null
        }) {
            selectPlaylistPopup = false
        }
    }
}
