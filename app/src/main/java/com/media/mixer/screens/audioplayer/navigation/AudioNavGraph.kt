package com.media.mixer.screens.audioplayer.navigation

import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.navArgument
import com.media.mixer.media.presentation.PlayerScreen
import com.media.mixer.screens.audioplayer.screen.PlaylistState
import com.media.mixer.screens.audioplayer.screen.State
import com.media.mixer.screens.audioplayer.screen.artist.ArtistDetailScreen
import com.media.mixer.screens.audioplayer.screen.artist.ArtistScreen
import com.media.mixer.screens.audioplayer.screen.library.LibraryScreen
import com.media.mixer.screens.audioplayer.screen.music.AudioHomeContent
import com.media.mixer.screens.audioplayer.screen.playlist.AudioPlayList
import com.media.mixer.screens.audioplayer.screen.playlist.PlaylistScreen
import com.media.mixer.screens.player.PlayListViewModel


@Composable
fun AudioNavGraph(
    navController: NavHostController,
    playListViewModel: PlayListViewModel,
    onBack: () -> Unit,
) {
    NavHost(navController = navController, startDestination = AudioDestination.Music.root) {

        composable(AudioDestination.Music.root) {
            val musicState by playListViewModel.musicState.collectAsStateWithLifecycle()
            val musicList by playListViewModel.list.collectAsStateWithLifecycle()
            val musicListFav by playListViewModel.musicListFav.collectAsStateWithLifecycle()
            val playlist by playListViewModel.playList.collectAsStateWithLifecycle()
            AudioHomeContent(
                musicList = musicList,
                musicListFav = musicListFav,
                playlist = playlist,
                musicState = musicState,
                onAudioEvent = { index, isRunning, from ->
                    if (!isRunning) playListViewModel.setPlayerListBaseOnState(
                        PlaylistState(
                            index,
                            from
                        )
                    )
                },
                addFavourite = { playListViewModel.updateSongFav(it, true) },
                addNewPlaylist = { playListViewModel.addNewPlaylist(it) },
                removeFavourite = { playListViewModel.updateSongFav(it, false) },
                addSongInPlaylist = { song, id -> playListViewModel.addSongToPlaylist(song, id) },
                onBack = onBack
            )
        }
        composable(AudioDestination.Artist.root) {
            val artists by playListViewModel.artists.collectAsStateWithLifecycle()
            ArtistScreen(artist = artists, onBack = { navController.navigateUp() }, onArtist = {
                playListViewModel.getArtistMusicList(artists[it])
                navController.navigate(AudioDestination.ArtistDetail.root)
            })
        }
        composable(AudioDestination.ArtistDetail.root) {
            val musicState by playListViewModel.musicState.collectAsStateWithLifecycle()
            val artistMusic by playListViewModel.artistMusic.collectAsStateWithLifecycle()
            val playlist by playListViewModel.playList.collectAsStateWithLifecycle()
            ArtistDetailScreen(
                title = "Artist Details",
                artist = playListViewModel.artist!!,
                songs = artistMusic,
                musicState = musicState,
                playlist = playlist,
                addFavourite = { playListViewModel.updateSongFav(it, true) },
                addNewPlaylist = { playListViewModel.addNewPlaylist(it) },
                addSongInPlaylist = { song, id -> playListViewModel.addSongToPlaylist(song, id) },
                onAudioEvent = { index, isRunning ->
                    if (!isRunning) playListViewModel.setPlayerListBaseOnState(
                        PlaylistState(
                            index,
                            State.ARTIST
                        )
                    )
                },
                onBack = { navController.navigateUp() }
            )
        }

        composable(AudioDestination.Library.root) {
            val library by playListViewModel.libraryList.collectAsStateWithLifecycle()
            LibraryScreen(library = library, onBack = { navController.navigateUp() }, onLibrary =  {
                playListViewModel.getLibraryMusicList(library[it])
                navController.navigate(AudioDestination.LibraryDetail.root)
            })
        }
        composable(AudioDestination.LibraryDetail.root) {
            val musicState by playListViewModel.musicState.collectAsStateWithLifecycle()
            val libraryMusic by playListViewModel.libraryMusic.collectAsStateWithLifecycle()
            val playlist by playListViewModel.playList.collectAsStateWithLifecycle()
            ArtistDetailScreen(
                title = "Library Details",
                artist = playListViewModel.library!!,
                songs = libraryMusic,
                musicState = musicState,
                playlist = playlist,
                addFavourite = { playListViewModel.updateSongFav(it, true) },
                addNewPlaylist = { playListViewModel.addNewPlaylist(it) },
                addSongInPlaylist = { song, id -> playListViewModel.addSongToPlaylist(song, id) },
                onAudioEvent = { index, isRunning ->
                    if (!isRunning) playListViewModel.setPlayerListBaseOnState(
                        PlaylistState(
                            index,
                            State.LIBRARY
                        )
                    )
                },
                onBack = { navController.navigateUp() }
            )
        }

        composable(AudioDestination.AudioPlayer.root) {
            val musicState by playListViewModel.musicState.collectAsStateWithLifecycle()
            val playlist by playListViewModel.playList.collectAsStateWithLifecycle()
            PlayerScreen(
                player = playListViewModel.getPlayer(),
                musicState = musicState,
                playlist = playlist,
                addFavourite = { id, state -> playListViewModel.updateSongFav(id, state) },
                addNewPlaylist = { playListViewModel.addNewPlaylist(it) },
                addSongInPlaylist = { song, id -> playListViewModel.addSongToPlaylist(song, id) },
                onBack = {
                    navController.navigateUp()
                }
            )
        }
        composable(AudioDestination.Playlist.root) {
            val playlist by playListViewModel.playList.collectAsStateWithLifecycle()
            AudioPlayList(
                playlist = playlist,
                bottomPadding = 0.dp,
                onPlaylist = {
                    playListViewModel.getAudioList(it)
                    navController.navigate("${AudioDestination.PlaylistDetail.root}/${it.id}/${it.name}")
                },
                onDeletePlaylist = { playListViewModel.deletePlaylist(it) },
                onBack = { navController.navigateUp() })
        }
        composable(
            "${AudioDestination.PlaylistDetail.root}/{id}/{name}",
            arguments = listOf(navArgument("id") {
                type = NavType.LongType
            }, navArgument("name") {
                type = NavType.StringType
            })
        ) { navBackStackEntry ->
            val musicState by playListViewModel.musicState.collectAsStateWithLifecycle()
            val playlistSong by playListViewModel.playlistSong.collectAsStateWithLifecycle()
            val playlist by playListViewModel.playList.collectAsStateWithLifecycle()

            PlaylistScreen(
                playlistName = navBackStackEntry.arguments?.getString("name") ?: "",
                list = playlistSong,
                musicState = musicState,
                playlist = playlist,
                addFavourite = { playListViewModel.updateSongFav(it, true) },
                addNewPlaylist = { playListViewModel.addNewPlaylist(it) },
                addSongInPlaylist = { song, id -> playListViewModel.addSongToPlaylist(song, id) },
                onAudioEvent = { index, isRunning ->
                    if (!isRunning) playListViewModel.setPlayerListBaseOnState(
                        PlaylistState(
                            index,
                            State.PLAYLIST
                        )
                    )
                },
            ) {
                navController.navigateUp()
            }
        }
    }
}