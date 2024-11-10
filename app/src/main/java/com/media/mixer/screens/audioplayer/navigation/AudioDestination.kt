package com.media.mixer.screens.audioplayer.navigation


sealed class AudioDestination(val root: String) {
    data object Music : AudioDestination(root = "audio_music_screen")

    data object Artist : AudioDestination(root = "audio_artist_screen")
    data object ArtistDetail : AudioDestination(root = "audio_artist_detail_screen")

    data object Library : AudioDestination(root = "audio_library_screen")
    data object LibraryDetail : AudioDestination(root = "audio_library_detail_screen")

    data object Playlist : AudioDestination(root = "audio_playlist_screen")
    data object PlaylistDetail : AudioDestination(root = "audio_playlist_detail_screen")

    data object AudioPlayer : AudioDestination(root = "audio_audio_player_screen")
}
