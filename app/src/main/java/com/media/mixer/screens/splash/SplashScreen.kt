package com.media.mixer.screens.splash

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.media.mixer.R
import com.media.mixer.core.components.TypewriterText

@Composable
fun SplashScreen() {
    Box(modifier = Modifier.fillMaxSize()) {
        val configuration = LocalConfiguration.current

        val screenHeight = configuration.screenHeightDp.dp
//        val screenWidth = configuration.screenWidthDp.dp
        Image(
            modifier = Modifier.fillMaxSize(),
            painter = painterResource(id = R.drawable.splash_bg_image),
            contentDescription = null,
            contentScale = ContentScale.FillBounds
        )
        TypewriterText(
            textAlign = TextAlign.Center,
            modifier = Modifier.align(Alignment.BottomCenter).navigationBarsPadding()
                .fillMaxWidth()
                .padding(vertical = screenHeight/8, horizontal = 16.dp),
            text = "MP3 Song Cutter & Joiner App"
        )
    }
}