package com.media.mixer.core.components

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.RowScope
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.FilledTonalButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.unit.dp
import com.media.mixer.core.utils.listOfColorForBorder

@Composable
fun GradientButton(modifier: Modifier= Modifier, onClick:()->Unit, content: @Composable() (RowScope.() -> Unit)) {
    val brush = Brush.linearGradient(listOfColorForBorder)
    FilledTonalButton(
        modifier=modifier,
        onClick = onClick,
        border = BorderStroke(2.dp, brush),
        shape = RoundedCornerShape(50),
        content = content,
    )
}