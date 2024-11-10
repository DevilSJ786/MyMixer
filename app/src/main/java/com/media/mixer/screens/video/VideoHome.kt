package com.media.mixer.screens.video

import android.net.Uri
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.PlayArrow
import androidx.compose.material3.FabPosition
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavController
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.media.mixer.screens.video.navigation.VideoNavGraph
import com.media.mixer.screens.video.navigation.listOfNavItemVideo
import com.media.mixer.screens.video.screens.VideosState
import com.media.mixer.screens.video.screens.videohome.VideoHomeViewModel


@Composable
fun VideoHome(onBack: () -> Unit, onVideoClick: (Uri, String) -> Unit) {
    val navController = rememberNavController()
    val viewModel: VideoHomeViewModel = hiltViewModel()
    val videosState by viewModel.videos.collectAsStateWithLifecycle()
    Scaffold(
        floatingActionButtonPosition = FabPosition.End,
        floatingActionButton = {
            val state = videosState as? VideosState.Success
            state?.recentPlayedVideo?.let {
                FloatingActionButton(
                    shape = CircleShape,
                    onClick = {
                        onVideoClick(
                            Uri.parse(state.recentPlayedVideo.uriString),
                            state.recentPlayedVideo.displayName
                        )
                    }
                ) {
                    Icon(
                        imageVector = Icons.Rounded.PlayArrow,
                        contentDescription = null
                    )
                }
            }
        },
        bottomBar = {
            BottomNavigationBarVideo(navController = navController)
        }) {
        VideoNavGraph(
            navController = navController,
            viewModel = viewModel,
            bottomPadding = it.calculateBottomPadding() - 16.dp,
            onBack = onBack,
            onVideoPlayerNavigate = onVideoClick
        )
    }
}

@Composable
fun BottomNavigationBarVideo(navController: NavController) {
    Column(
        modifier = Modifier.fillMaxWidth(),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(0.dp)
    ) {
        NavigationBar(
            modifier = Modifier.fillMaxWidth()
        ) {
            val navBackStackEntry by navController.currentBackStackEntryAsState()
            val currentRoute = navBackStackEntry?.destination?.route

            listOfNavItemVideo.forEach { item ->
                NavigationBarItem(
                    selected = currentRoute == item.route,
                    onClick = {
                        navController.navigate(item.route) {
                            popUpTo(navController.graph.startDestinationId)
                            launchSingleTop = true
                        }
                    },
                    icon = {
                        Icon(
                            painter = painterResource(id = if (currentRoute == item.route) item.selectionIcon else item.icon),
                            contentDescription = null
                        )
                    },
                    label = { Text(item.label) }
                )
            }
        }
    }
}