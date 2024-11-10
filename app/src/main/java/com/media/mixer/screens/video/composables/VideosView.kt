package com.media.mixer.screens.video.composables

import android.net.Uri
import androidx.compose.foundation.lazy.items
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.runtime.snapshots.SnapshotStateList
import androidx.compose.ui.platform.LocalContext
import com.media.mixer.R
import com.media.mixer.core.components.CreatePlaylistPopUp
import com.media.mixer.core.components.NoItemFound
import com.media.mixer.core.components.PlaylistPopUp
import com.media.mixer.core.utils.shareContent
import com.media.mixer.data.entities.Playlist
import com.media.mixer.domain.model.Video
import com.media.mixer.screens.video.screens.VideosState


@Composable
fun VideosView(
    videosState: VideosState,
    playlist: SnapshotStateList<Playlist>,
    addNewPlaylist: (String) -> Unit,
    addSongInPlaylist: (Video, Long) -> Unit,
    addFavourite: (Long) -> Unit,
    onVideoClick: (Uri,String) -> Unit,
    onVideoLoaded: (Uri) -> Unit = {}
) {
    val context = LocalContext.current
    var playlistPopup by remember {
        mutableStateOf(false)
    }
    var selectPlaylistPopup by remember {
        mutableStateOf(false)
    }
    var song by remember {
        mutableStateOf<Video?>(null)
    }
    when (videosState) {
        VideosState.Loading -> CenterCircularProgressBar()
        is VideosState.Success -> if (videosState.data.isEmpty()) {
            NoItemFound(message = "No Video File Available\nIn Your list", id = R.drawable.music_square_remove)
        } else {
            MediaLazyList {
                items(videosState.data, key = { it.path }) { video ->
                    LaunchedEffect(Unit) {
                        onVideoLoaded(Uri.parse(video.uriString))
                    }
                    VideoItem(
                        video = video,
                        isRecentlyPlayedVideo = video == videosState.recentPlayedVideo,
                        onClick = { onVideoClick(Uri.parse(video.uriString),video.displayName) },
                        addToFav = { addFavourite(video.id) },
                        addToPlaylist = {
                            selectPlaylistPopup = true
                            song = video
                        },
                        onShare = {
                            shareContent(
                                path =video.uriString,
                                context = context
                            )
                        }
                    )
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

