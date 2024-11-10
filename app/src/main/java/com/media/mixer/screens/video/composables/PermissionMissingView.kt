package com.media.mixer.screens.video.composables

import androidx.compose.runtime.Composable

@Composable
fun PermissionMissingView(
    isGranted: Boolean,
    showRationale: Boolean,
    permission: String,
    launchPermissionRequest: () -> Unit,
    content: @Composable () -> Unit
) {
    if (isGranted) {
        content()
    } else if (showRationale) {
        PermissionRationaleDialog(
            text = permission,
            onConfirmButtonClick = launchPermissionRequest
        )
    } else {
        PermissionDetailView(
            text = permission
        )
    }
}
