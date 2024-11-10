package com.media.mixer.screens.splash

import androidx.compose.foundation.Image
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.paint
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.media.mixer.R
import com.media.mixer.core.components.GradientButton
import com.media.mixer.core.components.TypewriterText
import com.media.mixer.core.utils.listOfColorForBorder

@Composable
fun ContinueScreen(onClick: () -> Unit) {
    Box(
        modifier = Modifier
            .fillMaxSize().paint(
                painterResource(id = R.drawable.bg_continue),
                contentScale = ContentScale.FillBounds)
            .padding(16.dp)
    ) {
        Image(
            modifier = Modifier
                .statusBarsPadding().padding(top = 32.dp)
                .size(250.dp).border(4.dp, Brush.linearGradient(listOfColorForBorder), CircleShape)
                .clip(CircleShape)
                .align(Alignment.TopCenter),
            painter = painterResource(id = R.drawable.splash_logo_img),
            contentDescription = null
        )


        Column(
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .fillMaxWidth()
                .padding(16.dp, 20.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            TypewriterText(
                textAlign = TextAlign.Start,
                modifier = Modifier.fillMaxWidth(),
                text = "MP3 Song Cutter &\nJoiner App"
            )
            Text(
                modifier = Modifier.fillMaxWidth(),
                text = "Your new favourite Music & Audio\n" + "Cutter application.",
                style = MaterialTheme.typography.labelMedium,
                color = Color.White
            )
            GradientButton(modifier = Modifier, onClick = onClick) {
                Text(text = "Lets Start")
            }
            Text(
                text = "Terms of Use & Privacy Policy",
                style = MaterialTheme.typography.labelMedium,
                color = Color.White
            )
        }
    }
}