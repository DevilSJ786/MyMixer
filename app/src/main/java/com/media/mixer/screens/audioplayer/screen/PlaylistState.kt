package com.media.mixer.screens.audioplayer.screen

data class PlaylistState(
    val index: Int = 0,
    val from: State = State.SONGS,
)

enum class State {
    SONGS, FAVORITE, ARTIST, LIBRARY, PLAYLIST
}