package com.media.mixer.core.components

import android.app.Activity
import android.content.Intent
import android.net.Uri
import android.provider.DocumentsContract
import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.animation.Crossfade
import androidx.compose.animation.core.spring
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableLongStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.DialogProperties
import androidx.media3.common.Player
import com.media.mixer.R
import com.media.mixer.core.utils.convertToText
import com.media.mixer.core.utils.getFolderPermissionIntent
import com.media.mixer.media.audio.common.MusicState
import com.media.mixer.media.presentation.PlayerImage2
import com.media.mixer.screens.components.ActionItem
import com.media.mixer.screens.components.SetAsPopUp
import com.media.mixer.screens.components.SetAudioPopUp
import com.media.mixer.screens.components.TrackSlider
import com.media.mixer.ui.theme.buttonBg
import kotlinx.coroutines.delay

@Composable
fun OutPutScreen(
    title: String,
    saveLocation: String,
    player: Player,
    musicState: MusicState,
    padding: PaddingValues,
    setRingtone: () -> Unit,
    setAlarm: () -> Unit,
    setNotification: () -> Unit,
    onShare: () -> Unit,
    onSaveAs: () -> Unit,
    onCut: () -> Unit,
    onDelete: () -> Unit,
    setDefaultDownloadLocation: (String) -> Unit,
) {
    var popupSetAs by remember {
        mutableStateOf(false)
    }
    var popupRingtone by remember {
        mutableStateOf(false)
    }
    var popupAlarm by remember {
        mutableStateOf(false)
    }
    var popupMessage by remember {
        mutableStateOf(false)
    }
    var popupContact by remember {
        mutableStateOf(false)
    }
    var openFileSavePopup by remember {
        mutableStateOf(false)
    }
    val context = LocalContext.current
    val launcher =
        rememberLauncherForActivityResult(contract = ActivityResultContracts.StartActivityForResult()) {
            if (it.resultCode == Activity.RESULT_OK) {
                it.data?.data?.let { uri ->
                    context.contentResolver?.takePersistableUriPermission(
                        uri,
                        Intent.FLAG_GRANT_READ_URI_PERMISSION or Intent.FLAG_GRANT_WRITE_URI_PERMISSION
                    )
                    setDefaultDownloadLocation(uri.toString())
                }
            }
        }
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(padding)
            .padding(16.dp),
        verticalArrangement = Arrangement.Top,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        OutPutTrack(
            title = title,
            player = player,
            musicState = musicState,
            onCut = onCut,
            onSaveAs = {  openFileSavePopup=true},
            onSet = { popupSetAs = true },
            onShare = onShare,
            onDelete = onDelete
        )
    }
    if (openFileSavePopup) {
        val path = if (saveLocation.isNotBlank()) {
            val treeUri = Uri.parse(saveLocation)
            val docId = DocumentsContract.getTreeDocumentId(treeUri)
            val destDir = DocumentsContract.buildDocumentUriUsingTree(treeUri, docId)
            "${destDir.path?.substringAfterLast(":")}"
        } else "Not Set"
        AlertDialog(
            properties = DialogProperties(dismissOnBackPress = true, dismissOnClickOutside = true),
            onDismissRequest = { openFileSavePopup = false },
            title = { Text(text = "Save  Location") },
            text = { Text(text = path) },
            confirmButton = {
                GradientButton(onClick = {
                    openFileSavePopup = false
                    if (saveLocation.isNotBlank()) {
                        onSaveAs()
                    }else Toast.makeText(
                        context,
                        "Please set save location",
                        Toast.LENGTH_SHORT
                    ).show()
                }) {
                    Text(text = "OK")
                }
            },
            dismissButton = {
                Button(
                    colors = ButtonDefaults.buttonColors(containerColor = buttonBg),
                    onClick = {
                    openFileSavePopup = false
                    launcher.launch(getFolderPermissionIntent())
                }) {
                    Text(text = "CHOOSE FOLDER")
                }
            }
        )
    }
    if (popupSetAs) {
        SetAsPopUp(
            player = player,
            trackImageUrl = Uri.EMPTY,
            title = title,
            onRingtone = {
                popupSetAs = false
                popupRingtone = true
            },
            onAlarm = {
                popupSetAs = false
                popupAlarm = true
            },
            onMessage = {
                popupSetAs = false
                popupMessage = true
            },
            onContact = {
                popupSetAs = false
                popupContact = true
            }) {
            popupSetAs = false
        }
    }
    if (popupRingtone) {
        SetAudioPopUp(
            title = "Set Ringtone?",
            description = "Your Ringtone is now ready !\n" +
                    "Proceed with this Ringtone ?",
            titleSong = "Song name",
            player = player,
            trackImageUrl = Uri.EMPTY,
            onSave = {
                setRingtone()
                popupRingtone = false
            }) {
            popupRingtone = false
        }
    }
    if (popupAlarm) {
        SetAudioPopUp(
            title = "Set Alarm?",
            titleSong = "Song name",
            description = "Your Ringtone is now ready !\n" +
                    "Proceed with this Ringtone ?",
            player = player,
            trackImageUrl = Uri.EMPTY,
            onSave = {
                setAlarm()
                popupAlarm = false
            }) {
            popupAlarm = false
        }
    }
    if (popupMessage) {
        SetAudioPopUp(
            title = "Set Message?",
            description = "Your Ringtone is now ready !\n" +
                    "Proceed with this Ringtone ?",
            titleSong = "Song name",
            player = player,
            trackImageUrl = Uri.EMPTY,
            onSave = {
                setNotification()
                popupMessage = false
            }) {
            popupMessage = false
        }
    }
    if (popupContact) {
        SetAudioPopUp(
            title = "Set Contact?",
            description = "Your Ringtone is now ready !\n" +
                    "Proceed with this Ringtone ?",
            titleSong = "Song name",
            player = player,
            trackImageUrl = Uri.EMPTY,
            onSave = {
                popupContact = false
            }) {
            popupContact = false
        }
    }
}

@Composable
fun OutPutTrack(
    title: String,
    player: Player,
    musicState: MusicState,
    onSet: () -> Unit,
    onShare: () -> Unit,
    onSaveAs: () -> Unit,
    onCut: () -> Unit,
    onDelete: () -> Unit,
) {

    val currentPosition = remember {
        mutableLongStateOf(0)
    }

    val sliderPosition = remember {
        mutableLongStateOf(0)
    }

    val totalDuration = remember {
        mutableLongStateOf(0)
    }


    LaunchedEffect(key1 = player.currentPosition, key2 = player.isPlaying) {
        delay(1000)
        currentPosition.longValue = player.currentPosition
    }

    LaunchedEffect(currentPosition.longValue) {
        sliderPosition.longValue = currentPosition.longValue
    }

    LaunchedEffect(player.duration) {
        if (player.duration > 0) {
            totalDuration.longValue = player.duration
        }
    }
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(12.dp),
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(0.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Box(
                    contentAlignment = Alignment.Center
                ) {
                    PlayerImage2(
                        modifier = Modifier.size(45.dp),
                        trackImageUrl = musicState.currentSong.artworkUri,
                    )
                    Crossfade(
                        targetState = musicState.playWhenReady,
                        animationSpec = spring(),
                        label = ""
                    ) { targetPlayWhenReady ->
                        if (targetPlayWhenReady) {
                            IconButton(
                                onClick = { player.pause() },
                            ) {
                                AppIcon(
                                    painter = painterResource(id = R.drawable.pause_circle),
                                    modifier = Modifier.size(25.dp),
                                    contentDescription = stringResource(id = R.string.pause)
                                )
                            }
                        } else {
                            IconButton(
                                onClick = { player.play() },
                            ) {
                                AppIcon(
                                    painter = painterResource(id = R.drawable.play_circle),
                                    modifier = Modifier.size(25.dp),
                                    contentDescription = stringResource(id = R.string.play)
                                )
                            }
                        }
                    }
                }
                Column {
                    Text(
                        text = title,
                        color = Color.White,
                        style = MaterialTheme.typography.titleMedium,
                        overflow = TextOverflow.Ellipsis
                    )
                    Text(
                        text = "size",
                        color = Color.White,
                        style = MaterialTheme.typography.bodyMedium
                    )
                }
                Spacer(modifier = Modifier.weight(1f))
                IconButton(onClick = {
                    player.pause()
                    onDelete()
                }) {
                    AppIcon(
                        painter = painterResource(id = R.drawable.trash),
                        contentDescription = null
                    )
                }
            }
            Box(modifier = Modifier.fillMaxWidth(), contentAlignment = Alignment.Center){
               Image(painter = painterResource(id = R.drawable.play_anim), contentDescription =null )
            }
            TrackSlider(
                value = sliderPosition.longValue.toFloat(),
                onValueChange = {
                    sliderPosition.longValue = it.toLong()
                },
                onValueChangeFinished = {
                    currentPosition.longValue = sliderPosition.longValue
                    player.seekTo(sliderPosition.longValue)
                },
                songDuration = totalDuration.longValue.toFloat()
            )
            Row(
                modifier = Modifier.fillMaxWidth(),
            ) {
                Text(
                    text = (currentPosition.longValue).convertToText(),
                    modifier = Modifier.weight(1f),
                    color = Color.White,
                    style = TextStyle(fontWeight = FontWeight.Bold)
                )

                val remainTime = totalDuration.longValue - currentPosition.longValue
                Text(
                    text = if (remainTime >= 0) remainTime.convertToText() else "",
                    color = Color.White,
                    style = TextStyle(fontWeight = FontWeight.Bold)
                )
            }
            Spacer(modifier = Modifier.height(16.dp))
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                ActionItem(
                    title = "Set",
                    id = R.drawable.notification,
                    onClick = {
                        player.pause()
                        onSet()
                    }
                )
                ActionItem(
                    title = "Share",
                    id = R.drawable.share,
                    onClick = {
                        player.pause()
                        onShare()
                    })
                ActionItem(
                    title = "Download",
                    id = R.drawable.download,
                    onClick = {
                        player.pause()
                        onSaveAs()
                    }
                )
                ActionItem(
                    title = "Cut",
                    id = R.drawable.audio_cutter,
                    onClick = {
                        player.pause()
                        onCut()
                    })
            }
        }
    }
}