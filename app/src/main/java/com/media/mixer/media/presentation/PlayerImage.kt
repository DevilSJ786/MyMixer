package com.media.mixer.media.presentation

import android.net.Uri
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.documentfile.provider.DocumentFile
import coil.compose.AsyncImage
import coil.request.ImageRequest
import com.media.mixer.R
import com.media.mixer.core.components.AppIcon


@Composable
fun PlayerImage(
    modifier: Modifier = Modifier,
    trackImageUrl: Uri,
    imageBitmap: ByteArray? = null,
) {

    Box(
        modifier = modifier
    ) {
        AsyncImage(
            model = ImageRequest.Builder(LocalContext.current)
                .data(imageBitmap ?: trackImageUrl)
                .crossfade(true)
                .build(),
            contentDescription = null,
            contentScale = ContentScale.Crop,
            modifier = modifier
                .aspectRatio(1f)
                .clip(MaterialTheme.shapes.small),
            error = painterResource(id = R.drawable.music_cover),
            placeholder = painterResource(id = R.drawable.music_cover)
        )
    }
}

@Composable
fun PlayerImage2(
    modifier: Modifier = Modifier,
    trackImageUrl: Uri,
    imageBitmap: ByteArray? = null,
) {
val context= LocalContext.current
    Box(
        modifier = modifier.background(
            color = Color.White.copy(alpha = 0.3f),
            shape = MaterialTheme.shapes.small
        )
    ) {
        val sourceFile = DocumentFile.fromSingleUri(context, trackImageUrl)  // not working in url
        if (sourceFile?.exists()==false||trackImageUrl == Uri.EMPTY) {
            AppIcon(
                modifier = Modifier
                    .aspectRatio(1f)
                    .padding(8.dp),
                painter = painterResource(id = R.drawable.music_1),
            )
        } else {
            AsyncImage(
                model = ImageRequest.Builder(LocalContext.current)
                    .data(imageBitmap ?: trackImageUrl)
                    .crossfade(true)
                    .build(),
                contentDescription = null,
                contentScale = ContentScale.Crop,
                modifier = Modifier
                    .aspectRatio(1f)
                    .clip(MaterialTheme.shapes.small),
                error = painterResource(id = R.drawable.music_1),
                placeholder = painterResource(id = R.drawable.music_1)
            )
        }
    }
}
