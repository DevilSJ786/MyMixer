package com.media.mixer.screens.video.composables

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.basicMarquee
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material.icons.rounded.VideoCameraBack
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.Icon
import androidx.compose.material3.ListItemDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.surfaceColorAtElevation
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.min
import coil.compose.AsyncImage
import coil.request.ImageRequest
import com.media.mixer.core.components.AppIcon
import com.media.mixer.core.utils.colorOfDropDown
import com.media.mixer.domain.model.Video
import com.media.mixer.screens.components.DropdownMenuItemCustom


@OptIn(ExperimentalLayoutApi::class, ExperimentalFoundationApi::class)
@Composable
fun VideoItem(
    modifier: Modifier = Modifier,
    video: Video,
    isRecentlyPlayedVideo: Boolean,
    onClick: () -> Unit,
    addToFav: () -> Unit,
    addToPlaylist: () -> Unit,
    onShare: () -> Unit,
) {
    val context = LocalContext.current
    var isContextMenuVisible by remember {
        mutableStateOf(false)
    }
    ListItemComponent(
        onClick = onClick,
        colors = ListItemDefaults.colors(
            headlineColor = if (isRecentlyPlayedVideo) {
               MaterialTheme.colorScheme.surface
            } else {
                MaterialTheme.colorScheme.onSurface
            },
            supportingColor = if (isRecentlyPlayedVideo) {
                MaterialTheme.colorScheme.primary
            } else {
                ListItemDefaults.colors().supportingTextColor
            }
        ),
        leadingContent = {
            Box(
                modifier = Modifier
                    .clip(MaterialTheme.shapes.small)
                    .background(MaterialTheme.colorScheme.surfaceColorAtElevation(1.dp))
                    .width(min(100.dp, LocalConfiguration.current.screenWidthDp.dp * 0.35f))
                    .aspectRatio(16f / 10f)
            ) {
                Icon(
                    imageVector = Icons.Rounded.VideoCameraBack,
                    contentDescription = null,
                    tint = MaterialTheme.colorScheme.surfaceColorAtElevation(100.dp),
                    modifier = Modifier
                        .align(Alignment.Center)
                        .fillMaxSize(0.5f)
                )
                AsyncImage(
                    model = ImageRequest.Builder(context)
                        .data(video.thumbnailPath)
                        .crossfade(true)
                        .build(),
                    contentDescription = null,
                    alignment = Alignment.Center,
                    contentScale = ContentScale.Crop,
                    modifier = Modifier.fillMaxSize()
                )
                InfoChip(
                    text = video.formattedDuration,
                    modifier = Modifier
                        .padding(5.dp)
                        .align(Alignment.BottomEnd),
                    backgroundColor =Color.Black.copy(alpha = 0.6f),
                    contentColor = Color.White,
                    shape = MaterialTheme.shapes.extraSmall
                )
            }
        },
        headlineContent = {
            Text(
                modifier = Modifier.basicMarquee(),
                text = video.displayName,
                maxLines = 1,
                style = MaterialTheme.typography.titleMedium,
                overflow = TextOverflow.Ellipsis
            )
        },
        supportingContent = {
            FlowRow(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 5.dp),
                horizontalArrangement = Arrangement.spacedBy(5.dp),
                verticalArrangement = Arrangement.spacedBy(5.dp)
            ) {
                InfoChip(text = video.formattedFileSize)
                InfoChip(text = "${video.height}p")
            }
        },
        trailingContent = {
            Box {
                AppIcon(
                    modifier = Modifier.clickable { isContextMenuVisible = true },
                    imageVector = Icons.Default.MoreVert
                )
                DropdownMenu(
                    expanded = isContextMenuVisible,
                    onDismissRequest = {
                        isContextMenuVisible = false
                    }
                ) {
                    DropdownMenuItemCustom(
                        contentPadding = PaddingValues(vertical = 4.dp, horizontal = 8.dp),
                        text = {
                            Text(
                                "Add to Favourite",
                                color = Color.White,
                                style = MaterialTheme.typography.titleSmall
                            )
                        },
                        onClick = {
                            isContextMenuVisible = false
                            addToFav()
                        }
                    )
                    DropdownMenuItemCustom(
                        contentPadding = PaddingValues(vertical = 4.dp, horizontal = 8.dp),
                        text = {
                            Text(
                                "Add to Playlist",
                                color = Color.White,
                                style = MaterialTheme.typography.titleSmall
                            )
                        },
                        onClick = {
                            isContextMenuVisible = false
                            addToPlaylist()
                        }
                    )
                    DropdownMenuItemCustom(
                        contentPadding = PaddingValues(vertical = 4.dp, horizontal = 8.dp),
                        text = {
                            Text(
                                "Share",
                                color = Color.White,
                                style = MaterialTheme.typography.titleSmall
                            )
                        },
                        onClick = {
                            isContextMenuVisible = false
                            onShare()
                        }
                    )
                }
            }
        },
        modifier = modifier
    )
}

@OptIn(ExperimentalFoundationApi::class, ExperimentalLayoutApi::class)
@Composable
fun VideoItemFav(
    modifier: Modifier = Modifier,
    video: Video,
    isRecentlyPlayedVideo: Boolean,
    onClick: () -> Unit,
    onRemoveFav: () -> Unit,
) {
    val context = LocalContext.current
    var isContextMenuVisible by remember {
        mutableStateOf(false)
    }
    ListItemComponent(
        onClick = onClick,
        colors = ListItemDefaults.colors(
            headlineColor = if (isRecentlyPlayedVideo) {
                MaterialTheme.colorScheme.primary
            } else {
                MaterialTheme.colorScheme.onSurface
            },
            supportingColor = if (isRecentlyPlayedVideo) {
                MaterialTheme.colorScheme.primary
            } else {
                ListItemDefaults.colors().supportingTextColor
            }
        ),
        leadingContent = {
            Box(
                modifier = Modifier
                    .clip(MaterialTheme.shapes.small)
                    .background(MaterialTheme.colorScheme.surfaceColorAtElevation(1.dp))
                    .width(min(100.dp, LocalConfiguration.current.screenWidthDp.dp * 0.35f))
                    .aspectRatio(16f / 10f)
            ) {
                Icon(
                    imageVector = Icons.Rounded.VideoCameraBack,
                    contentDescription = null,
                    tint = MaterialTheme.colorScheme.surfaceColorAtElevation(100.dp),
                    modifier = Modifier
                        .align(Alignment.Center)
                        .fillMaxSize(0.5f)
                )
                AsyncImage(
                    model = ImageRequest.Builder(context)
                        .data(video.thumbnailPath)
                        .crossfade(true)
                        .build(),
                    contentDescription = null,
                    alignment = Alignment.Center,
                    contentScale = ContentScale.Crop,
                    modifier = Modifier.fillMaxSize()
                )
                InfoChip(
                    text = video.formattedDuration,
                    modifier = Modifier
                        .padding(5.dp)
                        .align(Alignment.BottomEnd),
                    backgroundColor = Color.Black.copy(alpha = 0.6f),
                    contentColor = Color.White,
                    shape = MaterialTheme.shapes.extraSmall
                )
            }
        },
        headlineContent = {
            Text(
                modifier = Modifier.basicMarquee(),
                text = video.displayName,
                maxLines = 1,
                style = MaterialTheme.typography.titleMedium,
                overflow = TextOverflow.Ellipsis
            )
        },
        supportingContent = {
            FlowRow(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 5.dp),
                horizontalArrangement = Arrangement.spacedBy(5.dp),
                verticalArrangement = Arrangement.spacedBy(5.dp)
            ) {
                InfoChip(text = video.formattedFileSize)
                InfoChip(text = "${video.height}p")
            }
        },
        trailingContent = {
            Box {
                AppIcon(
                    modifier = Modifier.clickable { isContextMenuVisible = true },
                    imageVector = Icons.Default.MoreVert
                )
                DropdownMenu(
                    modifier = Modifier.background(colorOfDropDown),
                    expanded = isContextMenuVisible,
                    onDismissRequest = {
                        isContextMenuVisible = false
                    },
                ) {
                    DropdownMenuItemCustom(
                        contentPadding = PaddingValues(vertical = 4.dp, horizontal = 8.dp),
                        text = {
                            Text(
                                "Remove Favourite",
                                color = Color.White,
                                style = MaterialTheme.typography.titleSmall
                            )
                        },
                        onClick = {
                            isContextMenuVisible = false
                            onRemoveFav()
                        }
                    )
                }
            }
        },
        modifier = modifier
    )
}

