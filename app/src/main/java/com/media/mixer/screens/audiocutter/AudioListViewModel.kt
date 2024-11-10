package com.media.mixer.screens.audiocutter

import android.content.Context
import android.util.Log
import androidx.compose.runtime.mutableStateListOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.media.mixer.core.utils.StorageHelper
import com.media.mixer.core.utils.StoragePath
import com.media.mixer.core.utils.getAvailableFiles
import com.media.mixer.data.entities.Song
import com.media.mixer.domain.repository.LocalRepository
import com.media.mixer.domain.usecase.GetSortedAudioUseCase
import com.media.mixer.media.domain.mapper.asSong
import com.media.mixer.media.domain.mapper.toSong
import dagger.hilt.android.lifecycle.HiltViewModel
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import javax.inject.Inject

@HiltViewModel
class AudioListViewModel @Inject constructor(
    getSortedAudioUseCase: GetSortedAudioUseCase
) : ViewModel() {
    val audioList = getSortedAudioUseCase.invoke()
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = emptyList()
        )
}