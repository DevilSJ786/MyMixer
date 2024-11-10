package com.media.mixer.screens.video.composables

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material3.ListItemDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.min
import com.media.mixer.R
import com.media.mixer.domain.model.Folder

@OptIn(ExperimentalLayoutApi::class)
@Composable
fun FolderItem(
    folder: Folder,
    isRecentlyPlayedFolder: Boolean,
    modifier: Modifier = Modifier
) {
    ListItemComponent(
        colors = ListItemDefaults.colors(
            headlineColor = if (isRecentlyPlayedFolder) {
                MaterialTheme.colorScheme.onSurface
            } else {
                MaterialTheme.colorScheme.primary
            },
            supportingColor = if (isRecentlyPlayedFolder ) {
                MaterialTheme.colorScheme.primary
            } else {
                ListItemDefaults.colors().supportingTextColor
            }
        ),
        leadingContent = {
            Image(painter = painterResource(id = R.drawable.folder), modifier = Modifier
                .width(min(80.dp, LocalConfiguration.current.screenWidthDp.dp * 0.3f))
                .aspectRatio(20 / 15f), contentDescription =null )
        },
        headlineContent = {
            Text(
                text = folder.name,
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
                InfoChip(
                    text = "${folder.mediaCount} video"
                )
                InfoChip(text = folder.formattedMediaSize)
            }
        },
        modifier = modifier
    )
}