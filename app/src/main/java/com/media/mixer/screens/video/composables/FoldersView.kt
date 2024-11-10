package com.media.mixer.screens.video.composables

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.combinedClickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.lazy.items
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.media.mixer.R
import com.media.mixer.core.components.NoItemFound
import com.media.mixer.screens.video.screens.FoldersState

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun FoldersView(
    foldersState: FoldersState,
    onFolderClick: (String) -> Unit,
) {
    when (foldersState) {
        FoldersState.Loading -> CenterCircularProgressBar()
        is FoldersState.Success -> if (foldersState.data.isEmpty()) {
            NoItemFound(
                message = "No Folder File Available\nIn Your Folder",
                id = R.drawable.music_square_remove
            )
        } else {
                MediaLazyList(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    items(foldersState.data, key = { it.path }) {
                        FolderItem(
                            folder = it,
                            isRecentlyPlayedFolder = foldersState.recentPlayedVideo in it.mediaList,
                            modifier = Modifier.combinedClickable(
                                onClick = { onFolderClick(it.path) }
                            )
                        )
                    }
                }

        }
    }

}
