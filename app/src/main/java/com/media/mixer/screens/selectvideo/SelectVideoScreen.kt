package com.media.mixer.screens.selectvideo

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.sizeIn
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.CheckCircle
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.paint
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImage
import coil.request.ImageRequest
import com.media.mixer.R
import com.media.mixer.core.components.AppIcon
import com.media.mixer.core.components.GradientButton
import com.media.mixer.domain.model.Video
import com.media.mixer.ui.theme.buttonBg

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SelectVideoScreen(
    videoList: List<Video>,
    onVideo: (Video) -> Unit,
    onUrl: (String) -> Unit,
    onBack: () -> Unit
) {
    var url by remember {
        mutableStateOf("")
    }
    var video by remember {
        mutableStateOf<Video?>(null)
    }
    val context = LocalContext.current
    Scaffold(
        topBar = {
            CenterAlignedTopAppBar(title = {
                Text(
                    text = "Select Video",
                    color = Color.White,
                    style = MaterialTheme.typography.titleLarge
                )
            }, navigationIcon = {
                IconButton(onClick = onBack) {
                    AppIcon(painter = painterResource(id = R.drawable.back_))
                }
            }, actions = {
                GradientButton(modifier = Modifier.padding(end = 16.dp),
                    onClick = {
                        if ((video?.id ?: 0) > 0) video?.let { onVideo(it) }
                        else if (url.isNotEmpty()) onUrl(url.replace("http", "https"))

                    }) {
                    Text(text = "Next", color = Color.White)
                }
            },
                colors = TopAppBarDefaults.centerAlignedTopAppBarColors(containerColor = Color.Transparent)
            )
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                TextField(
                    colors = TextFieldDefaults.colors(
                        focusedTextColor = Color.White,
                        unfocusedTextColor = Color.White,
                        unfocusedLabelColor = Color.White,
                        focusedLabelColor = Color.White,
                        focusedContainerColor = Color.White.copy(alpha = 0.2f),
                        unfocusedContainerColor = Color.White.copy(alpha = 0.2f),
                    ),
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(MaterialTheme.shapes.medium)
                        .weight(1f, fill = false),
                    value = url,
                    leadingIcon = { AppIcon(painter = painterResource(id = R.drawable.link)) },
                    onValueChange = { url = it },
                    label = { Text(text = "Past Your Url Here") }
                )
                IconButton(
                    onClick = { if (url.isNotEmpty()) onUrl(url.replace("http", "https")) },
                    modifier = Modifier
                        .border(
                            2.dp, Color.White.copy(alpha = 0.5f),
                            RoundedCornerShape(16.dp)
                        )
                        .background(buttonBg, RoundedCornerShape(16.dp))
                ) {
                    AppIcon(painter = painterResource(id = R.drawable.search_normal))
                }
            }


            LazyVerticalGrid(modifier = Modifier.fillMaxWidth(), columns = GridCells.Fixed(3)) {
                items(videoList) {
                    Box(
                        modifier = Modifier.padding(4.dp).clickable {
                            video = if(it==video) null
                            else it
                        },
                        contentAlignment = Alignment.Center
                    ) {
                        AsyncImage(
                            model = ImageRequest.Builder(context)
                                .data(it.thumbnailPath)
                                .crossfade(true)
                                .build(),
                            contentDescription = null,
                            alignment = Alignment.Center,
                            contentScale = ContentScale.Crop,
                            modifier = Modifier.fillMaxWidth().heightIn(40.dp,120.dp)
                        )
                        if (video?.id == it.id) AppIcon(
                            imageVector = Icons.Rounded.CheckCircle,
                            contentDescription = null
                        )
                    }
                }
            }
        }
    }
}