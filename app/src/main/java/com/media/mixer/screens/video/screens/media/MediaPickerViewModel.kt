package com.media.mixer.screens.video.screens.media

import android.net.Uri
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.media.mixer.data.sync.MediaInfoSynchronizer
import com.media.mixer.domain.usecase.GetSortedFoldersUseCase
import com.media.mixer.domain.usecase.GetSortedVideosUseCase
import com.media.mixer.screens.video.screens.FoldersState
import com.media.mixer.screens.video.screens.VideosState
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class MediaPickerViewModel @Inject constructor(
    getSortedVideosUseCase: GetSortedVideosUseCase,
    getSortedFoldersUseCase: GetSortedFoldersUseCase,
    private val mediaInfoSynchronizer: MediaInfoSynchronizer
) : ViewModel() {

    val videosState = getSortedVideosUseCase.invoke()
        .map { VideosState.Success(it) }
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = VideosState.Loading
        )

    val foldersState = getSortedFoldersUseCase.invoke()
        .map { FoldersState.Success(it) }
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = FoldersState.Loading
        )
    fun addToMediaInfoSynchronizer(uri: Uri) {
        viewModelScope.launch {
            mediaInfoSynchronizer.addMedia(uri)
        }
    }
}
