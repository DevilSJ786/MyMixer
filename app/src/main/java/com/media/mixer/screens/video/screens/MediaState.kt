package com.media.mixer.screens.video.screens

import com.media.mixer.domain.model.Folder
import com.media.mixer.domain.model.Video

sealed interface VideosState {
    data object Loading : VideosState
    data class Success(val data: List<Video>) : VideosState {
        val recentPlayedVideo = data.recentPlayed()
        val firstVideo = data.firstOrNull()
    }
}

sealed interface FoldersState {
    data object Loading : FoldersState
    data class Success(val data: List<Folder>) : FoldersState {
        private val media = data.flatMap { it.mediaList }
        val recentPlayedVideo = media.recentPlayed()
    }
}

private fun List<Video>.recentPlayed(): Video? =
    filter { it.lastPlayedAt != null }.sortedByDescending { it.lastPlayedAt?.time }.firstOrNull()
 fun List<Video>.recentPlayedList(): List<Video> =
    filter { it.lastPlayedAt != null }.sortedByDescending { it.lastPlayedAt?.time }.take(10)