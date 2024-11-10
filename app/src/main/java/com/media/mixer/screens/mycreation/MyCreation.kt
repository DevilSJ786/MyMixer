package com.media.mixer.screens.mycreation

import androidx.annotation.DrawableRes
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.basicMarquee
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.paint
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import com.media.mixer.R
import com.media.mixer.core.components.AppIcon
import com.media.mixer.core.utils.StoragePath


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MyCreation(
    videoToAudioCount: Int,
    audioCutterCount: Int,
    audioMergerCount: Int,
    voiceRecorderCount: Int,
    ringtoneCount: Int,
    notificationCount: Int,
    alarmCount: Int,
    goToCreation: (String) -> Unit,
    onBack: () -> Unit
) {
    Scaffold(
        topBar = {
            CenterAlignedTopAppBar(title = {
                Text(
                    text = "My Creation",
                    color = Color.White,
                    style = MaterialTheme.typography.titleLarge
                )
            }, navigationIcon = {
                IconButton(onClick = onBack) {
                    AppIcon(painter = painterResource(id = R.drawable.back_))
                }
            },
                colors = TopAppBarDefaults.centerAlignedTopAppBarColors(containerColor = Color.Transparent)
            )
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize().verticalScroll(rememberScrollState())
                .padding(paddingValues)
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            CreationItem(
                title = "Video To Audio",
                count = videoToAudioCount,
                id = R.drawable.video_to_audio,
                containerColor = listOf(Color(0xFFF87076), Color(0xFFB94D87)),
                onClick = { goToCreation(StoragePath.VIDEOTOAUDIO.path) })
            CreationItem(
                title = "Audio Cutter",
                count = audioCutterCount,
                id = R.drawable.audio_cutter,
                containerColor = listOf(Color(0xFFF5D6A4), Color(0xFFFDB34E)),
                onClick = { goToCreation(StoragePath.CUTTER.path) })
            CreationItem(
                title = "Audio Merger",
                count = audioMergerCount,
                id = R.drawable.merger,
                containerColor = listOf(Color(0xFF77D8F9), Color(0xFF4EB4FA)),
                onClick = { goToCreation(StoragePath.MARGER.path) })
            CreationItem(
                title = "Voice Recorder",
                count = voiceRecorderCount,
                id = R.drawable.microphone,
                containerColor = listOf(Color(0xFFA09EE9), Color(0xFFB47DF6)),
                onClick = { goToCreation(StoragePath.VOICERECORDING.path) })
            CreationItem(
                title = "Ringtone",
                count = ringtoneCount,
                id = R.drawable.call_calling,
                containerColor = listOf(Color(0xFFAACA1F), Color(0xFF64A327)),
                onClick = { goToCreation(StoragePath.RINGTONE.path) })
            CreationItem(
                title = "Notification Ringtone",
                count = notificationCount,
                id = R.drawable.notification,
                containerColor = listOf(Color(0xFF59BCF8), Color(0xFF106CFF)),
                onClick = { goToCreation(StoragePath.NOTIFICATIONS.path) })
            CreationItem(
                title = "Alarm Ringtone",
                count = alarmCount,
                id = R.drawable.timer,
                containerColor = listOf(Color(0xFFA09EE9), Color(0xFFB47DF6)),
                onClick = { goToCreation(StoragePath.ALARM.path) })
        }
    }
}

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun CreationItem(
    modifier: Modifier = Modifier,
    title: String,
    count: Int,
    @DrawableRes id: Int,
    containerColor: List<Color>,
    onClick: () -> Unit,
) {
    Card(
        modifier = modifier
            .fillMaxWidth(),
        onClick = { onClick() },
    ) {
        Row(
            modifier = modifier
                .fillMaxWidth()
                .padding(start = 16.dp, top = 16.dp, bottom = 16.dp, end = 8.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            IconButton(
                modifier = Modifier.size(60.dp)
                    .background(
                        Brush.linearGradient(colors = containerColor),
                        RoundedCornerShape(15.dp)
                    )
                    .clip(RoundedCornerShape(15.dp)),
                onClick = { onClick() },
            ) {
                AppIcon(
                    modifier = Modifier
                        .padding(16.dp)
                        .size(50.dp),
                    painter = painterResource(id = id),
                    contentDescription = null
                )
            }

            Column(
                modifier = Modifier
                    .weight(1f, fill = false)
                    .fillMaxWidth(),
                horizontalAlignment = Alignment.Start
            ) {
                Text(
                    modifier = Modifier.basicMarquee(),
                    text = title,
                    color = Color.White,
                    style = MaterialTheme.typography.titleMedium,
                    maxLines = 1
                )
                Text(
                    text = "$count Record",
                    color = Color.White,
                    style = MaterialTheme.typography.bodyMedium
                )
            }
            AppIcon(painter = painterResource(id = R.drawable.right_))
        }
    }

}