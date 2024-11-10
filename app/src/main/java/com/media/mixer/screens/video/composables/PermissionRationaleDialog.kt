package com.media.mixer.screens.video.composables


import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier


@Composable
fun PermissionRationaleDialog(
    text: String,
    modifier: Modifier = Modifier,
    onConfirmButtonClick: () -> Unit
) {
    AlertDialog(
        onDismissRequest = {},
        modifier = modifier,
        title = {
            Text(
                text = "Permission Required"
            )
        },
        text = {
            Text(text = text)
        },
        confirmButton = {
            Button(onClick = onConfirmButtonClick) {
                Text("Grant Permission")
            }
        }
    )
}