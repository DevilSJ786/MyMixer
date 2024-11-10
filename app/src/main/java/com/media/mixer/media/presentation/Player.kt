package com.media.mixer.media.presentation

import android.content.Context
import android.util.Log
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.RememberObserver
import androidx.compose.runtime.Stable
import androidx.compose.runtime.State
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalLifecycleOwner
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleEventObserver
import androidx.media3.common.Player
import androidx.media3.common.Timeline

@Composable
fun RememberPlayer(
    lifecycle: Lifecycle = LocalLifecycleOwner.current.lifecycle,
    onPause: (Context) -> Unit,
    onStart: (Context) -> Unit
) {
    val currentContext = LocalContext.current.applicationContext
    DisposableEffect(lifecycle) {
        val observer = LifecycleEventObserver { _, event ->
            when {
                (event == Lifecycle.Event.ON_START)
                        || (event == Lifecycle.Event.ON_RESUME) -> {
                    onStart(currentContext)
                }

                (event == Lifecycle.Event.ON_PAUSE)
                        || (event == Lifecycle.Event.ON_STOP) -> {
                    onPause(currentContext)
                }
            }
        }
        lifecycle.addObserver(observer)
        onDispose {
            lifecycle.removeObserver(observer)
        }
    }
}

