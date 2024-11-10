package com.media.mixer.core.utils

sealed class TransformerState{
    data object Success : TransformerState()
    data object Error : TransformerState()
    data object Loading : TransformerState()
}
