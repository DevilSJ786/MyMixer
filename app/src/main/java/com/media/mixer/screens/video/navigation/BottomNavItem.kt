package com.media.mixer.screens.video.navigation

import androidx.annotation.DrawableRes
import com.media.mixer.R

sealed class BottomNavItem(val route: String, @DrawableRes val icon: Int, @DrawableRes val selectionIcon: Int, val label: String) {
    data object Video :
        BottomNavItem(VideoDestination.Video.root,
            R.drawable.video_, R.drawable.video_color, "Videos")

    data object Folder :
        BottomNavItem(VideoDestination.Folder.root,
            R.drawable.folder_open_white, R.drawable.folder_open, "Folders")

    data object Playlist : BottomNavItem(
        VideoDestination.Playlist.root, R.drawable.music_filter_play,
        R.drawable.music_filter_color, "Playlist"
    )
}

val listOfNavItemVideo =
    listOf(
        BottomNavItem.Video,
        BottomNavItem.Folder,
        BottomNavItem.Playlist
    )