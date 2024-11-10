package com.media.mixer.core.components

import androidx.compose.animation.AnimatedContent
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.material3.BottomSheetDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.SheetState
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CustomModelBottomSheet(
    targetState: Boolean,
    sheetState: SheetState,
    content: @Composable ColumnScope.() -> Unit,
    dragHandle: @Composable (() -> Unit)?=null,
    onClose: () -> Unit,
) {
    AnimatedContent(
        targetState = targetState,
        content = { isVisible ->
            if (isVisible) {
                ModalBottomSheet(
                    modifier = Modifier
                        .imePadding()
                        .navigationBarsPadding(),
                    onDismissRequest = onClose,
                    sheetState = sheetState,
                    windowInsets = BottomSheetDefaults.windowInsets,
                    dragHandle = dragHandle,
                    content = content,
                )
            }
        },
        label = "",
    )
}