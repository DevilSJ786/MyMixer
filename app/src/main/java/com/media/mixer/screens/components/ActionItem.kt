package com.media.mixer.screens.components

import androidx.annotation.DrawableRes
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import com.media.mixer.core.components.AppIcon
import com.media.mixer.ui.theme.buttonBg

@Composable
 fun ActionItem(title: String, @DrawableRes id: Int, color:Color= buttonBg, onClick: () -> Unit) {
    Column(
        verticalArrangement = Arrangement.spacedBy(8.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        IconButton(modifier = Modifier
            .size(45.dp)
            .background(color = color, shape = CircleShape)
            .clip(CircleShape), onClick =onClick) {
            AppIcon(
                modifier = Modifier.padding(4.dp),
                painter = painterResource(id =id ),
                contentDescription = null
            )
        }
        Text(text = title,color = Color.White, style = MaterialTheme.typography.labelSmall)
    }
}
