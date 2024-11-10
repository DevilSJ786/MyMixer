package com.media.mixer.screens.videotoaudio

sealed interface AudioUiState {
    data object AudioFormat:AudioUiState
    data object Loading:AudioUiState
    data object OutPut:AudioUiState
    data object CutAudio:AudioUiState
}

sealed interface CutterUiState {
    data object AudioSelection:CutterUiState
    data object AudioCutter:CutterUiState
    data object Loading:CutterUiState
    data object OutPut:CutterUiState
    data object CutAgain:CutterUiState
}

sealed interface MergerUiState {
    data object AudioSelection:MergerUiState
    data object AudioMerger:MergerUiState
    data object Loading:MergerUiState
    data object OutPut:MergerUiState
    data object CutAgain:MergerUiState
}

sealed interface RingtoneUiState {
    data object RingtoneHome:RingtoneUiState
    data object Selection:RingtoneUiState
    data object Merge:RingtoneUiState
    data object Loading:RingtoneUiState
    data object OutPut:RingtoneUiState
    data object Cut:RingtoneUiState
}