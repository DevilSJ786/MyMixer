package com.media.mixer.screens.components

import androidx.compose.foundation.Canvas
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.snapshots.SnapshotStateList
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.drawscope.translate

@Composable
fun AudioVisualizer(
    modifier: Modifier = Modifier,
    amplitudes:SnapshotStateList<Int>
) {
    /*
    * set max amplitude to tune the sensitivity
    * Higher the value, lower the sensitivity of the visualizer
     */
    val maxAmplitude = 10000
    val primary = MaterialTheme.colorScheme.primary
    val primaryMuted = primary.copy(alpha = 0.3f)
    Canvas(modifier = modifier) {
        val height = this.size.height / 2f
        val width = this.size.width

        translate(width, height) {
            amplitudes.forEachIndexed { index, amplitude ->
                val amplitudePercentage = (amplitude.toFloat() / maxAmplitude).coerceAtMost(1f)
                val boxHeight = height * amplitudePercentage
                drawRoundRect(
                    color = if (amplitudePercentage > 0.05f) primary else primaryMuted,
                    topLeft = Offset(
                        30f * (index - amplitudes.size),
                        -boxHeight / 2f
                    ),
                    size = Size(15f, boxHeight),
                    cornerRadius = CornerRadius(3f, 3f)
                )
            }
        }
    }
}
