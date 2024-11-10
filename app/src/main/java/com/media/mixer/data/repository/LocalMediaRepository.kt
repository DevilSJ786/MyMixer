package com.media.mixer.data.repository

import com.media.mixer.data.dao.DirectoryDao
import com.media.mixer.data.dao.MediumDao
import com.media.mixer.data.entities.MediumEntity
import com.media.mixer.data.relations.DirectoryWithMedia
import com.media.mixer.domain.mappers.toFolder
import com.media.mixer.domain.mappers.toVideo
import com.media.mixer.domain.mappers.toVideoState
import com.media.mixer.domain.model.Folder
import com.media.mixer.domain.model.Video
import com.media.mixer.domain.model.VideoState
import com.media.mixer.domain.repository.MediaRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject


class LocalMediaRepository @Inject constructor(
    private val mediumDao: MediumDao,
    private val directoryDao: DirectoryDao,
) : MediaRepository {

    override fun getVideosFlow(): Flow<List<Video>> {
        return mediumDao.getAllWithInfo().map { it.map(MediumEntity::toVideo) }
    }

    override fun getVideosFlowFromFolderPath(folderPath: String): Flow<List<Video>> {
        return mediumDao.getAllWithInfoFromDirectory(folderPath).map { it.map(MediumEntity::toVideo) }
    }

    override fun getFoldersFlow(): Flow<List<Folder>> {
        return directoryDao.getAllWithMedia().map { it.map(DirectoryWithMedia::toFolder) }
    }

    override suspend fun updateVideoFav(id: Long, fav: Boolean) {
        mediumDao.updateVideoFav(id, fav)
    }

    override suspend fun updateVideoPlaylist(id: Long, map: Map<Long, Boolean>) {
      mediumDao.updateVideoPlaylist(id, map)
    }

    override suspend fun getVideoState(uri: String): VideoState? {
        return mediumDao.get(uri)?.toVideoState()
    }

    override suspend fun saveVideoState(
        uri: String,
        position: Long,
        audioTrackIndex: Int?,
        playbackSpeed: Float?,
    ) {
            mediumDao.updateMediumState(
                uri = uri,
                position = position,
                audioTrackIndex = audioTrackIndex,
                playbackSpeed = playbackSpeed,
                lastPlayedTime = System.currentTimeMillis()
            )
    }
}
