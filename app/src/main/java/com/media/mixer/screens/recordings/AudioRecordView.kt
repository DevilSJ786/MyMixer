package com.media.mixer.screens.recordings

import android.util.Log
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.paint
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.media.mixer.R
import com.media.mixer.core.components.AppIcon
import com.media.mixer.core.utils.StoragePath
import com.media.mixer.screens.components.BackHandler
import com.media.mixer.screens.components.RecordingItem

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AudioRecordView(onBack: () -> Unit) {
    BackHandler {
        onBack()
    }
    val playerViewModel: PlayerViewModel = hiltViewModel()
    val context = LocalContext.current

    LaunchedEffect(Unit) {
        playerViewModel.loadVoiceRecordingFiles(context, StoragePath.VOICERECORDING.path)
    }

    val files = playerViewModel.files
    Scaffold( topBar = {
            CenterAlignedTopAppBar(
                title = {
                    Text(
                        text = "Audio Records",
                        color = Color.White,
                        style = MaterialTheme.typography.titleLarge
                    )
                },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        AppIcon(painter = painterResource(id = R.drawable.back_))
                    }
                },
                colors = TopAppBarDefaults.centerAlignedTopAppBarColors(containerColor = Color.Transparent)
            )
        }) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .padding(16.dp)
        ) {
            if (files.isNotEmpty()) {
                Text(
                    text = "Recordings",
                    color = Color.White,
                    fontSize = MaterialTheme.typography.headlineMedium.fontSize
                )
                Spacer(modifier = Modifier.height(15.dp))
                LazyColumn(
                    modifier = Modifier.padding(top = 10.dp),
                    verticalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    items(files) {
                        Log.d("TAG", "AudioRecordView: ${it.uri}")
                        RecordingItem(recordingFile = it)
                    }
                }
            }
        }
    }
}