package com.media.mixer.screens.components

import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import com.media.mixer.core.components.GradientButton

@Composable
fun ConfirmationDialog(
     title: String,
    onDismissRequest: () -> Unit,
    onConfirm: () -> Unit
) {
    AlertDialog(
        onDismissRequest = onDismissRequest,
        title = {
            Text(title)
        },
        text = {
            Text("Are you sure? This can't be undone!")
        },
        confirmButton = {
            GradientButton(onClick = {
                onConfirm.invoke()
                onDismissRequest.invoke()
            }) {
                Text(text = "Okay")
            }
        },
        dismissButton = {
            Button(
                onClick = {
                    onDismissRequest.invoke()
                }) {
                Text(text = "Cancel")
            }
        }
    )
}
