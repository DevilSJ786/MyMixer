package com.media.mixer.screens.audioplayer.navigation

import androidx.annotation.DrawableRes
import com.media.mixer.R

sealed class BottomNavItem(val route: String,  @DrawableRes val icon: Int, @DrawableRes val selectionIcon: Int, val label: String) {
    data object Music : BottomNavItem(AudioDestination.Music.root,
        R.drawable.music_, R.drawable.music, "Music")
    data object Artist : BottomNavItem(AudioDestination.Artist.root,
        R.drawable.user, R.drawable.user_color,"Artist")
    data object Library :
        BottomNavItem(AudioDestination.Library.root, R.drawable.music_list,
            R.drawable.music_list_color, "Library")

    data object Playlist : BottomNavItem(
        AudioDestination.Playlist.root, R.drawable.music_filter_white, R.drawable.music_filter, "Playlist"
    )
}

val listOfNavItem =
    listOf(BottomNavItem.Music, BottomNavItem.Artist, BottomNavItem.Library, BottomNavItem.Playlist)