package com.media.mixer.screens.recorder


sealed interface RecorderUiState {
    data object StartState:RecorderUiState
    data object WaitState:RecorderUiState
    data object RecordingState:RecorderUiState
    data object LoadingState:RecorderUiState
    data object SaveProcessState:RecorderUiState
    data object CutState:RecorderUiState
    data object CutLoadingState:RecorderUiState
}