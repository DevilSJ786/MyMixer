package com.media.mixer.core.components

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material3.Card
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import com.airbnb.lottie.compose.LottieAnimation
import com.airbnb.lottie.compose.LottieCompositionSpec
import com.airbnb.lottie.compose.LottieConstants
import com.airbnb.lottie.compose.animateLottieCompositionAsState
import com.airbnb.lottie.compose.rememberLottieComposition
import com.media.mixer.R
import com.media.mixer.core.utils.TransformerState
import kotlinx.coroutines.delay

@Composable
fun ProgressScreen(
    paddingValues: PaddingValues,
    transformerState: TransformerState,
    onSaveProcessState: () -> Unit,
    onBack: () -> Unit
) {

    var progressT by remember {
        mutableIntStateOf(0)
    }
    LaunchedEffect(Unit) {
        repeat(5) {
            delay(1000)
            progressT += 20
        }
    }
    var popupState by remember {
        mutableStateOf(false)
    }
    LaunchedEffect(transformerState) {
        if (transformerState is TransformerState.Error) {
            popupState = true
        }
    }
    val composition by rememberLottieComposition(
        spec = LottieCompositionSpec.RawRes(R.raw.cd)
    )
    var isPlaying by remember {
        mutableStateOf(true)
    }
    val progress by animateLottieCompositionAsState(
        composition = composition,
        iterations = LottieConstants.IterateForever,
        isPlaying = isPlaying
    )
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(paddingValues),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(0.dp)
    ) {
        Box(
            modifier = Modifier.fillMaxWidth(),
            contentAlignment = Alignment.Center
        ) {
            LottieAnimation(
                modifier = Modifier.size(200.dp),
                composition = composition,
                progress = {
                    progress
                }
            )
            Text(
                modifier = Modifier.align(Alignment.Center),
                text = "$progressT %",
                color = Color.White,
                style = MaterialTheme.typography.titleMedium
            )
            Image(
                modifier = Modifier
                    .align(Alignment.BottomEnd)
                    .size(100.dp)
                    .padding(end = 16.dp),
                painter = painterResource(id = R.drawable.player_tip),
                contentDescription = null
            )
        }
        Spacer(modifier = Modifier.height(40.dp))
        Text(
            modifier = Modifier.fillMaxWidth(),
            color = Color.White,
            textAlign = TextAlign.Center,
            text = "Wait a movement While your audio get \n ready...",
            style = MaterialTheme.typography.bodyMedium
        )
        Spacer(modifier = Modifier.height(20.dp))
        Text(
            modifier = Modifier.fillMaxWidth(),
            color = Color.White,
            textAlign = TextAlign.Center,
            text = "Do not need to stay here, once the \n process is finished. Click on DONE button \nto see your result",
            style = MaterialTheme.typography.bodyMedium
        )
        Spacer(modifier = Modifier.weight(1F))
        AnimatedVisibility(visible = transformerState is TransformerState.Success) {
            GradientButton(onClick = onSaveProcessState) {
                Text(text = "DONE")
            }
        }
    }
    if (popupState) {
        Dialog(
            onDismissRequest = { popupState = false },
            properties = DialogProperties(dismissOnBackPress = false, dismissOnClickOutside = false)
        ) {
            Card {
                Column(
                    modifier = Modifier.padding(16.dp),
                    verticalArrangement = Arrangement.spacedBy(16.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    AppIcon(modifier = Modifier.size(100.dp).drawBehind {
                        drawCircle(color = Color.Red, radius = size.height / 2)
                    }, imageVector = Icons.Default.Close)
                    Text(
                        modifier = Modifier.fillMaxWidth(),
                        text = "Error!",
                        textAlign = TextAlign.Center,
                        style = MaterialTheme.typography.titleLarge,
                        color = Color.Red
                    )
                    Text(
                        modifier = Modifier.fillMaxWidth(),
                        text = "Sorry Something went wrong",
                        textAlign = TextAlign.Center,
                        color = Color.White,
                        style = MaterialTheme.typography.bodyMedium,
                    )
                    GradientButton(onClick = { popupState = false
                        onBack()}) {
                        Text(text = "Try Again")
                    }
                }
            }
        }
    }

}