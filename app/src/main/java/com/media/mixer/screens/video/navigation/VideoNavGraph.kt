package com.media.mixer.screens.video.navigation

import android.net.Uri
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.unit.Dp
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.navArgument
import com.media.mixer.screens.audioplayer.screen.playlist.AudioPlayList
import com.media.mixer.screens.video.screens.media.VideoFolderScreen
import com.media.mixer.screens.video.screens.mediaFolder.VideoFolderDetailsScreen
import com.media.mixer.screens.video.screens.videohome.VideoHomeScreen
import com.media.mixer.screens.video.screens.videohome.VideoHomeViewModel


@Composable
fun VideoNavGraph(
    navController: NavHostController,
    bottomPadding:Dp,
    viewModel: VideoHomeViewModel,
    onVideoPlayerNavigate: (Uri, String) -> Unit,
    onBack: () -> Unit
) {
    NavHost(navController = navController, startDestination = VideoDestination.Video.root) {

        composable(VideoDestination.Video.root) {
            val videosState by viewModel.videos.collectAsStateWithLifecycle(minActiveState = Lifecycle.State.RESUMED)
            val videosStateFav by viewModel.videosFavorite.collectAsStateWithLifecycle(
                minActiveState = Lifecycle.State.RESUMED
            )
            val playlist by viewModel.playList.collectAsStateWithLifecycle(minActiveState = Lifecycle.State.RESUMED)
            VideoHomeScreen(
                videosState = videosState,
                videosStateFav = videosStateFav,
                playlist = playlist,
                bottomPadding=bottomPadding,
                onPlayVideo = {uri,name->
                    onVideoPlayerNavigate(uri,name)
                },
                onAddToSync = viewModel::addToMediaInfoSynchronizer,
                addToFav = {viewModel.updateVideoFav(it.id,true)},
                onRemoveFav = {viewModel.updateVideoFav(it.id,false)},
                addSongInPlaylist = {video, l ->  viewModel.addVideoToPlaylist(video,l)},
                addNewPlaylist = {  viewModel.addNewPlaylist(it)},
                onBack = onBack
            )
        }
        composable(VideoDestination.Folder.root) {
            val videosState by viewModel.videos.collectAsStateWithLifecycle()
            val foldersState by viewModel.foldersState.collectAsStateWithLifecycle()

            VideoFolderScreen(
                videosState = videosState,
                foldersState = foldersState,
                bottomPadding=bottomPadding,
                onPlayVideo = {uri,name->
                    onVideoPlayerNavigate(uri,name)
                },
                onFolderClick = {
                    viewModel.setFolderPath(it)
                    navController.navigate(VideoDestination.FolderDetail.root)
                },
                onBack = navController::navigateUp
            )
        }

        composable(VideoDestination.FolderDetail.root) {
            val videosState by viewModel.videosFromPath.collectAsStateWithLifecycle(minActiveState = Lifecycle.State.RESUMED)
            val playlist by viewModel.playList.collectAsStateWithLifecycle(minActiveState = Lifecycle.State.RESUMED)
            VideoFolderDetailsScreen(
                folderPath = viewModel.folderPathValue,
                videosState = videosState,
                bottomPadding=bottomPadding,
                playlist = playlist,
                onVideoClick = {uri,name->
                    onVideoPlayerNavigate(uri,name)
                },
                addSongInPlaylist = {video, l ->  viewModel.addVideoToPlaylist(video,l)},
                addNewPlaylist = {  viewModel.addNewPlaylist(it)},
                addFavourite = {viewModel.updateVideoFav(it,true)},
                onAddToSync = viewModel::addToMediaInfoSynchronizer,
                onBack = navController::navigateUp
            )
        }
        composable(VideoDestination.Playlist.root) {
            val playlist by viewModel.playList.collectAsStateWithLifecycle(minActiveState = Lifecycle.State.RESUMED)
            AudioPlayList(
                playlist = playlist,
                bottomPadding=bottomPadding,
                onPlaylist = {
                    viewModel.getVideosOfPlaylist(it)
                    navController.navigate("${VideoDestination.PlaylistDetail.root}/${it.id}/${it.name}")
                },
                onDeletePlaylist = { viewModel.deletePlaylist(it) },
                onBack = navController::navigateUp)
        }
        composable(
            "${VideoDestination.PlaylistDetail.root}/{id}/{name}",
            arguments = listOf(navArgument("id") {
                type = NavType.LongType
            }, navArgument("name") {
                type = NavType.StringType
            })
        ) { navBackStackEntry ->
            val playlistVideoState by viewModel.playlistVideo.collectAsStateWithLifecycle()
            val playlist by viewModel.playList.collectAsStateWithLifecycle(minActiveState = Lifecycle.State.RESUMED)
            VideoFolderDetailsScreen(
                folderPath = navBackStackEntry.arguments?.getString("name") ?: "",
                playlist=playlist,
                videosState = playlistVideoState,
                bottomPadding=bottomPadding,
                onVideoClick = {uri,name->
                    onVideoPlayerNavigate(uri,name)
                },
                addSongInPlaylist = {video, l ->  viewModel.addVideoToPlaylist(video,l)},
                addNewPlaylist = {  viewModel.addNewPlaylist(it)},
                addFavourite = {viewModel.updateVideoFav(it,true)},
                onAddToSync = viewModel::addToMediaInfoSynchronizer,
                onBack = navController::navigateUp
            )
        }
    }
}