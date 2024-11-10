package com.media.mixer.screens.home

import android.Manifest
import android.app.Activity
import android.content.pm.PackageManager
import android.os.Build
import androidx.annotation.DrawableRes
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Card
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.snapshots.SnapshotStateList
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.core.app.ActivityCompat
import androidx.documentfile.provider.DocumentFile
import com.media.mixer.R
import com.media.mixer.core.components.AppIcon
import com.media.mixer.core.utils.listOfColorForBorder
import com.media.mixer.navigations.Destination
import com.media.mixer.screens.components.RecordingItem

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeScreen(
    audioPlayer: () -> Unit,
    ringtones: SnapshotStateList<DocumentFile>,
    onNavigate: (String) -> Unit,
) {
    val context = LocalContext.current as Activity
    val lazyListState = rememberLazyListState()

    Scaffold(
        modifier = Modifier.fillMaxSize(),
        topBar = {
            CenterAlignedTopAppBar(
                modifier = Modifier.statusBarsPadding(),
                title = {
                    Text(
                        text = "MP3 Editor",
                        color = Color.White,
                        style = MaterialTheme.typography.titleLarge
                    )
                },
                colors = TopAppBarDefaults.centerAlignedTopAppBarColors(containerColor = Color.Transparent),
                navigationIcon = {
                    IconButton(onClick = { onNavigate(Destination.Settings.root) }) {
                        AppIcon(
                            painter = painterResource(id = R.drawable.setting),
                            contentDescription = null
                        )
                    }
                },
                actions = {
                    IconButton(onClick = { onNavigate(Destination.MyCreation.root) }) {
                        AppIcon(
                            painter = painterResource(id = R.drawable.music_playlist),
                            contentDescription = null
                        )
                    }
                })
        }) { it ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
                .padding(it)
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {

            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                ItemCardBlur(
                    modifier = Modifier.weight(0.5f),
                    name = "Audio\nCutter",
                    id = R.drawable.audio_cutter,
                    onClickAction = {
                        val permission =
                            if (Build.VERSION.SDK_INT >= 33) Manifest.permission.READ_MEDIA_AUDIO else Manifest.permission.READ_EXTERNAL_STORAGE
                        if (ActivityCompat.checkSelfPermission(context, permission)
                            != PackageManager.PERMISSION_GRANTED
                        ) {
                            ActivityCompat.requestPermissions(
                                context,
                                arrayOf(permission),  /* requestCode= */
                                0
                            )
                        } else {
                            onNavigate(Destination.AudioCutter.root)
                        }
                    }
                )
                ItemCardBlur(
                    modifier = Modifier.weight(0.5f),
                    name = "Audio\nMerger",
                    id = R.drawable.merger,
                    onClickAction = { onNavigate(Destination.AudioMerger.root) }
                )
            }
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                ItemCardBlur(
                    modifier = Modifier.weight(0.5f),
                    name = "Voice\nRecorder",
                    id = R.drawable.microphone,
                    onClickAction = { onNavigate(Destination.AudioRecorderScreen.root) }
                )
                ItemCardBlur(
                    modifier = Modifier.weight(0.5f),
                    name = "Audio\nPlayer",
                    id = R.drawable.play_circle_,
                    onClickAction = audioPlayer
                )
            }
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                ItemCardBlur(
                    modifier = Modifier.weight(0.5f),
                    name = "Video\nPlayer",
                    id = R.drawable.video_,
                    onClickAction = { onNavigate(Destination.VideoPlayer.root) }
                )
                ItemCardBlur(
                    modifier = Modifier.weight(0.5f),
                    name = "Video to Audio",
                    id = R.drawable.video_to_audio,
                    onClickAction = {
                        val permission =
                            if (Build.VERSION.SDK_INT >= 33) Manifest.permission.READ_MEDIA_VIDEO else Manifest.permission.READ_EXTERNAL_STORAGE
                        if (ActivityCompat.checkSelfPermission(context, permission)
                            != PackageManager.PERMISSION_GRANTED
                        ) {
                            ActivityCompat.requestPermissions(
                                context,
                                arrayOf(permission),  /* requestCode= */
                                0
                            )
                        } else {
                            onNavigate(Destination.SelectVideo.root)
                        }
                    }
                )
            }
            if (ringtones.isNotEmpty()) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically,
                ) {
                    Text(
                        text = "Ringtone",
                        color = Color.White,
                        style = MaterialTheme.typography.titleMedium
                    )
                    Spacer(modifier = Modifier.weight(1f))
                    TextButton(onClick = { onNavigate(Destination.Ringtones.root) }) {
                        Text(
                            text = "See All",
                            color = Color.White,
                            style = MaterialTheme.typography.titleMedium
                        )
                    }
                }
                LazyColumn(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(400.dp),
                    state = lazyListState,
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    items(ringtones) { item ->
                        RecordingItem(recordingFile = item, onClick = {

                        })
                    }
                }
            }
        }
    }
}


@Composable
fun ItemCard(modifier: Modifier, name: String, onClickAction: () -> Unit) {
    Card(
        modifier = modifier,
        onClick = onClickAction,
        shape = RoundedCornerShape(16.dp),
        border = BorderStroke(
            2.dp,
            Brush.linearGradient(listOfColorForBorder)
        ),
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            horizontalArrangement = Arrangement.Center,
            verticalAlignment = Alignment.CenterVertically
        ) {
            AppIcon(
                painter = painterResource(id = R.drawable.video_to_audio),
                contentDescription = null
            )
            Spacer(modifier = Modifier.width(16.dp))
            Text(
                text = name,
                style = MaterialTheme.typography.titleLarge,
                color = Color.White,
                maxLines = 1
            )
        }
    }
}

@Composable
fun ItemCardBlur(
    modifier: Modifier,
    name: String,
    @DrawableRes id: Int,
    onClickAction: () -> Unit
) {
    Card(
        modifier = modifier,
        onClick = onClickAction,
        shape = RoundedCornerShape(16.dp),
        border = BorderStroke(
            2.dp,
            Brush.linearGradient(listOfColorForBorder)
        )
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            horizontalArrangement = Arrangement.spacedBy(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            IconButton(
                modifier = Modifier
                    .size(45.dp)
                    .border(BorderStroke(
                        2.dp,
                        Brush.linearGradient(listOfColorForBorder)
                    ), RoundedCornerShape(12.dp)),
                onClick = onClickAction) {
                AppIcon(
                    painter = painterResource(id = id),
                    contentDescription = null
                )
            }

            Text(
                text = name,
                style = MaterialTheme.typography.bodyMedium,
                color = Color.White,
                textAlign = TextAlign.Start,
                maxLines = 2
            )
        }
    }
}

