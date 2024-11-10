package com.media.mixer.domain.repository

import com.media.mixer.domain.model.Folder
import com.media.mixer.domain.model.Video
import com.media.mixer.domain.model.VideoState
import kotlinx.coroutines.flow.Flow

interface MediaRepository {
    fun getVideosFlow(): Flow<List<Video>>
    fun getVideosFlowFromFolderPath(folderPath: String): Flow<List<Video>>
    fun getFoldersFlow(): Flow<List<Folder>>
    suspend fun updateVideoFav(id: Long, fav: Boolean)
    suspend fun updateVideoPlaylist(id: Long, map: Map<Long, Boolean>)
    suspend fun saveVideoState(
        uri: String,
        position: Long,
        audioTrackIndex: Int?,
        playbackSpeed: Float?,
    )

    suspend fun getVideoState(uri: String): VideoState?
}
