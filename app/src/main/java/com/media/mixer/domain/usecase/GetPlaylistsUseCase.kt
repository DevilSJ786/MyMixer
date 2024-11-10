package com.media.mixer.domain.usecase

import com.media.mixer.data.entities.Playlist
import com.media.mixer.domain.repository.LocalRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.withContext
import javax.inject.Inject


class GetPlaylistsUseCase @Inject constructor(
    private val localRepository: LocalRepository,
) {
    suspend operator fun invoke(isVideo: Boolean): Flow<List<Playlist>> =
        withContext(Dispatchers.IO) {
            localRepository.getPlaylist(isVideo = isVideo)
        }
}