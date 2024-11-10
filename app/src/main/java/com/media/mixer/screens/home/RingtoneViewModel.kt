package com.media.mixer.screens.home

import android.content.Context
import android.util.Log
import androidx.compose.runtime.mutableStateListOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.media.mixer.core.utils.getAvailableFiles
import com.media.mixer.data.entities.Song
import com.media.mixer.media.domain.mapper.toSong
import dagger.hilt.android.lifecycle.HiltViewModel
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import javax.inject.Inject

@HiltViewModel
class RingtoneViewModel @Inject constructor(@ApplicationContext private val context: Context) : ViewModel() {


    val files = mutableStateListOf<Song>()

//    init {
//        loadVoiceRecordingFiles(context, StoragePath.RINGTONE.path)
////        loadVoiceRecordingFiles(context, StoragePath.NOTIFICATIONS.path)
////        loadVoiceRecordingFiles(context, StoragePath.ALARM.path)
//    }
    fun loadVoiceRecordingFiles(context: Context, path: String) {
        viewModelScope.launch {
            withContext(Dispatchers.IO) {
                val f = getAvailableFiles(context, path)
                files.clear()
                files.addAll(f.map { it.toSong() })
                Log.d("TAG", "loadVoiceRecordingFiles: ${f.first().name} ${f.first().uri} ${f.first().type}")
            }
        }
    }
}