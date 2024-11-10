package com.media.mixer.core.components

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import com.media.mixer.R
import com.media.mixer.core.utils.listOfColorForLine
import com.media.mixer.data.entities.Playlist

@Composable
fun CreatePlaylistPopUp(onSave: (String) -> Unit, onDismiss: () -> Unit) {
    var value by remember {
        mutableStateOf("")
    }
    AlertDialog(
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp),
        onDismissRequest = onDismiss,
        title = {
            Text(
                modifier = Modifier.fillMaxWidth(),
                text = "New Playlist",
                style = MaterialTheme.typography.titleLarge,
                textAlign = TextAlign.Center
            )
        },
        text = {
            TextField(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(MaterialTheme.shapes.medium),
                value = value,
                onValueChange = { value = it },
                label = { Text(text = "Playlist name") })
        },
        confirmButton = {
            GradientButton(
                onClick = {
                    onSave(value)
                }) {
                Text("Add")
            }
        },
        dismissButton = {
            Button(
                onClick = onDismiss
            ) {
                Text("Cancel")
            }
        }
    )
}

@Composable
fun PlaylistPopUp(
    list: List<Playlist>,
    newPlaylist: () -> Unit,
    onSave: (Long) -> Unit,
    onDismiss: () -> Unit
) {
    Dialog(
        onDismissRequest = onDismiss,
        properties = DialogProperties(dismissOnBackPress = true, dismissOnClickOutside = true)
    ) {
        Card(
            shape = MaterialTheme.shapes.medium,
        ) {
            Column(
                modifier = Modifier.padding(16.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(
                    text = "Playlists",
                    style = MaterialTheme.typography.titleLarge,
                    modifier = Modifier.fillMaxWidth(),
                    textAlign = TextAlign.Center,
                )
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clickable { newPlaylist() },
                    horizontalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    AppIcon(imageVector = Icons.Default.Add)
                    Text(
                        text = "New Playlist",
                        style = MaterialTheme.typography.titleMedium
                    )
                }
                list.forEach {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clickable { onSave(it.id) },
                        horizontalArrangement = Arrangement.spacedBy(16.dp)
                    ) {
                        AppIcon(painter = painterResource(id = R.drawable.music_filter_white))
                        Text(
                            text = it.name,
                            style = MaterialTheme.typography.titleMedium,
                        )
                    }
                    if (it != list.last()) {
                        Spacer(
                            modifier = Modifier
                                .fillMaxWidth()
                                .background(Brush.linearGradient(listOfColorForLine))
                        )
                    }
                }
            }
        }
    }
}