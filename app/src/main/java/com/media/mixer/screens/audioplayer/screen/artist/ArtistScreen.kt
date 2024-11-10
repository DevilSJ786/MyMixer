package com.media.mixer.screens.audioplayer.screen.artist


import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.snapshots.SnapshotStateList
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import com.media.mixer.R
import com.media.mixer.core.components.AppIcon
import com.media.mixer.domain.model.Artist
import com.media.mixer.screens.audioplayer.component.ArtistItem


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ArtistScreen(artist: SnapshotStateList<Artist>, onArtist: (Int) -> Unit,onBack: () -> Unit) {
    val lazyListState = rememberLazyListState()
    Scaffold(
        topBar = {
            CenterAlignedTopAppBar(
                title = { Text(text = "Artist", style = MaterialTheme.typography.titleLarge, color = Color.White) },
                colors = TopAppBarDefaults.centerAlignedTopAppBarColors(containerColor = Color.Transparent),
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        AppIcon(painter = painterResource(id = R.drawable.back_))
                    }
                },
            )
        }) {
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(it)
                .padding(horizontal = 16.dp),
            state = lazyListState,
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            itemsIndexed(artist) { index, item ->
                ArtistItem(artist = item.name, image = item.image) {
                    onArtist(index)
                }
            }
        }
    }
}