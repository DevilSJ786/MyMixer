package com.media.mixer.core.components

import androidx.compose.material3.Icon
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.graphics.vector.ImageVector


@Composable
fun AppIcon(
    modifier: Modifier = Modifier,
    imageVector: ImageVector,
    contentDescription: String? = null
) {
    Icon(imageVector = imageVector, contentDescription = contentDescription, modifier)
}

@Composable
fun AppIcon(
    modifier: Modifier = Modifier,
    painter: Painter,
    contentDescription: String? = null,
) {
    Icon(painter = painter, contentDescription = contentDescription, modifier)
}