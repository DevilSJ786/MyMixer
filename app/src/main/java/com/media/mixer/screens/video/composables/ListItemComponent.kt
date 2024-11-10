package com.media.mixer.screens.video.composables

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Card
import androidx.compose.material3.ListItemColors
import androidx.compose.material3.ListItemDefaults
import androidx.compose.material3.LocalContentColor
import androidx.compose.material3.LocalTextStyle
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.unit.dp

@Composable
fun ListItemComponent(
    headlineContent: @Composable () -> Unit,
    modifier: Modifier = Modifier,
    supportingContent: @Composable (() -> Unit)? = null,
    leadingContent: @Composable (() -> Unit)? = null,
    trailingContent: @Composable (() -> Unit)? = null,
    colors: ListItemColors = ListItemDefaults.colors(),
    onClick: () -> Unit={}
) {
    Card(onClick = onClick) {
        Row(
            modifier = modifier
                .fillMaxWidth().padding(start = 16.dp, top = 16.dp, bottom = 16.dp, end = 8.dp)
                .semantics(mergeDescendants = true) {},
            horizontalArrangement = Arrangement.spacedBy(8.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            leadingContent?.invoke()
            Column(
                modifier = Modifier.weight(1f)
            ) {
                CompositionLocalProvider(
                    LocalContentColor provides colors.headlineColor,
                    LocalTextStyle provides MaterialTheme.typography.bodyLarge
                ) {
                    headlineContent.invoke()
                }
                CompositionLocalProvider(
                    LocalContentColor provides colors.supportingTextColor,
                    LocalTextStyle provides MaterialTheme.typography.bodyMedium
                ) {
                    supportingContent?.invoke()
                }
            }
            trailingContent?.invoke()
        }
    }

}

