@file:OptIn(ExperimentalPermissionsApi::class)

package com.media.mixer.screens.video.screens.media

import android.net.Uri
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.WindowInsetsSides
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.only
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.safeDrawing
import androidx.compose.foundation.layout.windowInsetsPadding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.PlayArrow
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FabPosition
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.google.accompanist.permissions.ExperimentalPermissionsApi
import com.google.accompanist.permissions.PermissionState
import com.google.accompanist.permissions.isGranted
import com.google.accompanist.permissions.rememberPermissionState
import com.google.accompanist.permissions.shouldShowRationale
import com.media.mixer.R
import com.media.mixer.core.components.AppIcon
import com.media.mixer.core.utils.storagePermission
import com.media.mixer.screens.video.composables.FoldersView
import com.media.mixer.screens.video.composables.PermissionMissingView
import com.media.mixer.screens.video.screens.FoldersState
import com.media.mixer.screens.video.screens.VideosState

const val CIRCULAR_PROGRESS_INDICATOR_TEST_TAG = "circularProgressIndicator"

@Composable
fun VideoFolderScreen(
    videosState: VideosState,
    foldersState: FoldersState,
    bottomPadding: Dp,
    onPlayVideo: (uri: Uri, String) -> Unit,
    onFolderClick: (folderPath: String) -> Unit,
    onBack: () -> Unit
) {

    val permissionState = rememberPermissionState(permission = storagePermission)

    MediaPickerScreen(
        videosState = videosState,
        foldersState = foldersState,
        bottomPadding = bottomPadding,
        permissionState = permissionState,
        onPlayVideo = onPlayVideo,
        onFolderClick = onFolderClick,
        onBack = onBack
    )
}

@OptIn(ExperimentalPermissionsApi::class, ExperimentalMaterial3Api::class)
@Composable
internal fun MediaPickerScreen(
    videosState: VideosState,
    foldersState: FoldersState,
    bottomPadding: Dp,
    permissionState: PermissionState ,
    onPlayVideo: (uri: Uri, String) -> Unit = { uri, name -> },
    onFolderClick: (folderPath: String) -> Unit = {},
    onBack: () -> Unit ,
) {

    Scaffold(
        modifier = Modifier
            .fillMaxSize()
            .windowInsetsPadding(WindowInsets.safeDrawing.only(WindowInsetsSides.Horizontal)),
        floatingActionButtonPosition = FabPosition.Center,
        floatingActionButton = {
            FloatingActionButton(
                onClick = {
                    val state = videosState as? VideosState.Success
                    onPlayVideo(
                        Uri.parse((state?.recentPlayedVideo ?: state?.firstVideo)?.uriString),
                        state?.recentPlayedVideo?.displayName ?: state?.firstVideo?.displayName
                        ?: "Video"
                    )
                }
            ) {
                Icon(
                    imageVector = Icons.Rounded.PlayArrow,
                    contentDescription = null
                )
            }
        },
        topBar = {
            CenterAlignedTopAppBar(
                title = {
                    Text(
                        text = "My Folders",
                        color = Color.White,
                        style = MaterialTheme.typography.titleLarge
                    )
                },
                colors = TopAppBarDefaults.centerAlignedTopAppBarColors(containerColor = Color.Transparent),
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        AppIcon(painter = painterResource(id = R.drawable.back_))
                    }
                },
            )
        }) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .padding(start = 16.dp, bottom = bottomPadding - 10.dp, end = 16.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            PermissionMissingView(
                isGranted = permissionState.status.isGranted,
                showRationale = permissionState.status.shouldShowRationale,
                permission = permissionState.permission,
                launchPermissionRequest = { permissionState.launchPermissionRequest() }
            ) {
                FoldersView(
                    foldersState = foldersState,
                    onFolderClick = onFolderClick,
                )
            }
        }
    }


}

