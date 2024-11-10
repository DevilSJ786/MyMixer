package com.media.mixer.domain.usecase

import com.media.mixer.data.entities.Song
import com.media.mixer.domain.repository.LocalRepository
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class GetSortedAudioUseCase @Inject constructor(
    private val localRepository: LocalRepository
) {

    operator fun invoke(): Flow<List<Song>> {
        return localRepository.getPlaylistSong()
    }
}