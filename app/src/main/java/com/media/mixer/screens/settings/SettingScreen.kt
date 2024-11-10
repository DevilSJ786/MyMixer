package com.media.mixer.screens.settings

import androidx.annotation.DrawableRes
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.paint
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.media.mixer.R
import com.media.mixer.core.components.AppIcon
import com.media.mixer.core.utils.listOfColorForLine


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SettingScreen(onBack: () -> Unit) {
    Scaffold(modifier = Modifier
        .fillMaxSize(),
        topBar = {
            CenterAlignedTopAppBar(
                title = {
                    Text(
                        text = "Settings",
                        color = Color.White,
                        style = MaterialTheme.typography.titleLarge
                    )
                },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        AppIcon(painter = painterResource(id = R.drawable.back_))
                    }
                },
                colors = TopAppBarDefaults.centerAlignedTopAppBarColors(containerColor = Color.Transparent)
            )
        }) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            SettingCard(
                first = SettingItem(title = "Rate Us", icon = R.drawable.star),
                sec = SettingItem(title = "feedback", icon = R.drawable.like),
                third = SettingItem(title = "Share app", icon = R.drawable.share),
                onFirstClick = { },
                onSecClick = { },
                onThirdClick = {}
            )
            SettingCard(
                first = SettingItem(title = "Privacy Policy", icon = R.drawable.lock),
                sec = SettingItem(title = "Terms of Service", icon = R.drawable.lock),
                third = SettingItem(title = "More Apps", icon = R.drawable.element_3),
                onFirstClick = { },
                onSecClick = { },
                onThirdClick = { }
            )
            Text(
                modifier = Modifier.fillMaxWidth(),
                text = "Version 1.0.0.2",
                color = Color.White,
                style = MaterialTheme.typography.titleMedium,
                textAlign = TextAlign.Center
            )
            Spacer(modifier = Modifier.weight(1f))
            Row(horizontalArrangement = Arrangement.spacedBy(16.dp)) {
                Image(painter = painterResource(id = R.drawable.facebook),contentDescription = null)
                AppIcon(painter = painterResource(id = R.drawable.twitter))
                Image(painter = painterResource(id = R.drawable.instagram),contentDescription = null)
                Image(painter = painterResource(id = R.drawable.youtube),contentDescription = null)
                AppIcon(painter = painterResource(id = R.drawable.linkedin))
            }
            Column(
                verticalArrangement = Arrangement.spacedBy(2.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(
                    text = "For quick support, Write to us at",
                    color = Color.White,
                    style = MaterialTheme.typography.titleMedium
                )
                Text(
                    text = "test@example.com",
                    color = Color(0xFFCB5B97),
                    style = MaterialTheme.typography.bodyMedium
                )
            }

        }
    }
}

@Composable
fun SettingCard(
    first: SettingItem,
    sec: SettingItem,
    third: SettingItem,
    onFirstClick: () -> Unit,
    onSecClick: () -> Unit,
    onThirdClick: () -> Unit
) {
    Card {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(start = 16.dp, top = 16.dp, bottom = 16.dp, end = 8.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Row(
                modifier = Modifier.fillMaxWidth().clickable { onFirstClick() },
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                AppIcon(painter = painterResource(id = first.icon))
                Text(
                    text = first.title,
                    style = MaterialTheme.typography.titleMedium,
                    color = Color.White
                )
                Spacer(modifier = Modifier.weight(1f))
                AppIcon(painter = painterResource(id = R.drawable.right_))
            }
            Spacer(
                modifier = Modifier
                    .height(2.dp)
                    .fillMaxWidth()
                    .background(Brush.linearGradient(listOfColorForLine))
            )
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .clickable { onSecClick() },
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                AppIcon(painter = painterResource(id = sec.icon))
                Text(
                    text = sec.title,
                    style = MaterialTheme.typography.titleMedium,
                    color = Color.White
                )
                Spacer(modifier = Modifier.weight(1f))
                AppIcon(painter = painterResource(id = R.drawable.right_))
            }
            Spacer(
                modifier = Modifier
                    .height(2.dp)
                    .fillMaxWidth()
                    .background(Brush.linearGradient(listOfColorForLine))
            )
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .clickable { onThirdClick() },
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                AppIcon(painter = painterResource(id = third.icon))
                Text(
                    text = third.title,
                    style = MaterialTheme.typography.titleMedium,
                    color = Color.White
                )
                Spacer(modifier = Modifier.weight(1f))
                AppIcon(painter = painterResource(id = R.drawable.right_))
            }
        }
    }
}

data class SettingItem(@DrawableRes val icon: Int, val title: String)