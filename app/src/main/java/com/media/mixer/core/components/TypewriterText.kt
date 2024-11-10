package com.media.mixer.core.components

import androidx.compose.material3.Text
import androidx.compose.material3.Typography
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.sp
import com.media.mixer.R
import kotlinx.coroutines.delay

@Composable
fun TypewriterText(
    modifier: Modifier,
    text: String,
    textAlign: TextAlign? = null
) {

    var textToDisplay by remember {
        mutableStateOf("")
    }

    LaunchedEffect(
        key1 = text,
    ) {
        text.forEachIndexed { charIndex, _ ->
            textToDisplay = text
                .substring(
                    startIndex = 0,
                    endIndex = charIndex + 1,
                )
            delay(120)
        }
    }

    Text(
        textAlign =textAlign ,
        modifier = modifier,
        text = textToDisplay,
        color = Color.White,
        style = typography.titleLarge,
        fontFamily = fontFamily
    )
}

val fontFamily = FontFamily(
    Font(R.font.red_hat_bold, weight = FontWeight.Bold),
)
val typography = Typography(
    titleLarge = TextStyle(fontFamily = fontFamily, fontWeight = FontWeight.Bold, fontSize =36.sp)
)