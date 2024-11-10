package com.media.mixer.screens.video.navigation

sealed class VideoDestination(val root: String) {
    data object Video : VideoDestination(root = "video_music_screen")

    data object Folder : VideoDestination(root = "video_library_screen")
    data object FolderDetail : VideoDestination(root = "video_library_detail_screen")

    data object Playlist : VideoDestination(root = "video_playlist_screen")
    data object PlaylistDetail : VideoDestination(root = "video_playlist_detail_screen")
}