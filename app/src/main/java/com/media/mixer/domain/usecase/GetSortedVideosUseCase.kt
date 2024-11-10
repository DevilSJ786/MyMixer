package com.media.mixer.domain.usecase

import com.media.mixer.domain.model.Video
import com.media.mixer.domain.repository.MediaRepository
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class GetSortedVideosUseCase @Inject constructor(
    private val mediaRepository: MediaRepository,
) {

    operator fun invoke(folderPath: String? = null): Flow<List<Video>> {
       return if (folderPath != null) {
            mediaRepository.getVideosFlowFromFolderPath(folderPath)
        } else {
            mediaRepository.getVideosFlow()
        }
    }
}
