package com.media.mixer.domain.usecase

import android.content.Context
import android.net.Uri
import com.media.mixer.core.utils.getPath
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.withContext
import java.io.File
import javax.inject.Inject

class GetSortedPlaylistUseCase  @Inject constructor(
    private val getSortedVideosUseCase: GetSortedVideosUseCase,
    @ApplicationContext private val context: Context,
) {
    suspend operator fun invoke(uri: Uri): List<Uri> = withContext(Dispatchers.IO) {
        val path = context.getPath(uri) ?: return@withContext emptyList()
        val parent = File(path).parent

        getSortedVideosUseCase.invoke(parent).first().map { Uri.parse(it.uriString) }
    }
}
