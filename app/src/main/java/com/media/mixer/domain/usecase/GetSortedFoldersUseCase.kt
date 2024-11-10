package com.media.mixer.domain.usecase

import com.media.mixer.domain.model.Folder
import com.media.mixer.domain.repository.MediaRepository
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class GetSortedFoldersUseCase @Inject constructor(
    private val mediaRepository: MediaRepository,
) {
    operator fun invoke(): Flow<List<Folder>> {
        return mediaRepository.getFoldersFlow()
    }
}
