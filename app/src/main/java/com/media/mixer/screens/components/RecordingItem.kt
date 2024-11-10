package com.media.mixer.screens.components

import android.net.Uri
import androidx.compose.animation.Crossfade
import androidx.compose.animation.core.spring
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.basicMarquee
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.documentfile.provider.DocumentFile
import androidx.hilt.navigation.compose.hiltViewModel
import com.media.mixer.R
import com.media.mixer.core.components.AppIcon
import com.media.mixer.media.presentation.PlayerImage2
import com.media.mixer.screens.recordings.PlayerViewModel


@OptIn(ExperimentalFoundationApi::class)
@Composable
fun RecordingItem(
    recordingFile: DocumentFile,
    onClick: () -> Unit={}
) {
    val playerViewModel: PlayerViewModel = hiltViewModel()
    val context = LocalContext.current

    var showDeleteDialog by remember {
        mutableStateOf(false)
    }
    var showDropDown by remember {
        mutableStateOf(false)
    }
    var isPlaying by remember {
        mutableStateOf(false)
    }

    Card(
        modifier = Modifier,
        onClick = {
            playerViewModel.stopPlaying()
            onClick()
        },
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(16.dp),
            modifier = Modifier
                .fillMaxWidth()
                .padding(start = 16.dp, top = 16.dp, bottom = 16.dp, end = 8.dp),
        ) {
            Box(
                contentAlignment = Alignment.Center
            ) {
                PlayerImage2(
                    modifier = Modifier.size(45.dp),
                    trackImageUrl = Uri.EMPTY,
                )
                Crossfade(targetState = isPlaying, animationSpec = spring(), label = "") {
                    if (it) {
                        IconButton(onClick = {
                            playerViewModel.stopPlaying()
                            isPlaying = false
                        }) {
                            AppIcon(
                                painter = painterResource(id = R.drawable.pause_circle),
                                modifier = Modifier.size(25.dp),
                                contentDescription = stringResource(id = R.string.pause)
                            )
                        }
                    } else {
                        IconButton(onClick = {
                            playerViewModel.startPlaying(context, recordingFile.uri) {
                                isPlaying = false
                            }
                            isPlaying = true
                        }) {
                            AppIcon(
                                painter = painterResource(id = R.drawable.play_circle),
                                modifier = Modifier.size(25.dp),
                                contentDescription = stringResource(id = R.string.play)
                            )
                        }
                    }
                }
            }
            Text(
                modifier = Modifier.fillMaxWidth().weight(1f, fill = false).basicMarquee(),
                maxLines = 1,
                overflow = TextOverflow.Ellipsis,
                color = Color.White,
                text = recordingFile.name.orEmpty()
            )

            Box {
                AppIcon(imageVector = Icons.Default.MoreVert, modifier = Modifier.clickable {
                    showDropDown = true
                })
                DropdownMenu(
                    expanded = showDropDown,
                    onDismissRequest = { showDropDown = false }
                ) {
                    DropdownMenuItemCustom(
                        contentPadding = PaddingValues(vertical = 4.dp, horizontal = 8.dp ),
                        text = {
                            Text("Delete", color = Color.White)
                        },
                        onClick = {
                            showDeleteDialog = true
                            showDropDown = false
                        }
                    )
                }
            }
        }
    }

    if (showDeleteDialog) {
        ConfirmationDialog(
            title = "Delete",
            onDismissRequest = { showDeleteDialog = false }
        ) {
            playerViewModel.stopPlaying()
            recordingFile.delete()
            playerViewModel.files.remove(recordingFile)
        }
    }
}

@Composable
 fun DropdownMenuItemCustom(
    text: @Composable () -> Unit,
    onClick: () -> Unit,
    modifier: Modifier=Modifier,
    trailingIcon: @Composable (() -> Unit)?=null,
    enabled: Boolean=true,
    contentPadding: PaddingValues,
    interactionSource: MutableInteractionSource=remember { MutableInteractionSource() }
) {
    Row(
        modifier = modifier
            .clickable(
                enabled = enabled,
                onClick = onClick,
                interactionSource = interactionSource,
                indication = androidx.compose.material.ripple.rememberRipple(true)
            )
            .fillMaxWidth()
            .padding(contentPadding),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        Box(
            Modifier.weight(1f)
        ) {
            text()
        }
        if (trailingIcon != null) {
            trailingIcon()
        }
    }
}
