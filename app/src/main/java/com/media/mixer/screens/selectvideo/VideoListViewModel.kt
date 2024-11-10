package com.media.mixer.screens.selectvideo

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.media.mixer.domain.usecase.GetSortedVideosUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.stateIn
import javax.inject.Inject

@HiltViewModel
class VideoListViewModel @Inject constructor(getSortedVideosUseCase: GetSortedVideosUseCase) :
    ViewModel() {

    val videosState = getSortedVideosUseCase.invoke()
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = emptyList()
        )

}