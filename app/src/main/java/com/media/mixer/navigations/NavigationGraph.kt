package com.media.mixer.navigations

import android.Manifest
import android.app.Activity
import android.content.Intent
import android.util.Log
import androidx.annotation.OptIn
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.platform.LocalContext
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.media3.common.util.UnstableApi
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.navArgument
import com.media.mixer.core.components.animatedComposable
import com.media.mixer.core.utils.PermissionHelper
import com.media.mixer.core.utils.RecorderState
import com.media.mixer.core.utils.StoragePath
import com.media.mixer.core.utils.checkSystemWriteSettings
import com.media.mixer.core.utils.decode
import com.media.mixer.core.utils.encode
import com.media.mixer.core.utils.getAvailableFiles
import com.media.mixer.core.utils.setTone
import com.media.mixer.core.utils.shareContent
import com.media.mixer.screens.audiocutter.AudioCutterScreen
import com.media.mixer.screens.audiocutter.AudioCutterViewModel
import com.media.mixer.screens.audiocutter.AudioListViewModel
import com.media.mixer.screens.audioplayer.AudioActivity
import com.media.mixer.screens.home.HomeScreen
import com.media.mixer.screens.merger.AudioMerger
import com.media.mixer.screens.merger.AudioMergerViewModel
import com.media.mixer.screens.mycreation.CreationScreen
import com.media.mixer.screens.mycreation.MyCreation
import com.media.mixer.screens.player.video.VideoPlayerScreen
import com.media.mixer.screens.recorder.AudioRecorderScreen
import com.media.mixer.screens.recorder.RecorderUiState
import com.media.mixer.screens.recorder.RecorderViewModel
import com.media.mixer.screens.recordings.AudioRecordView
import com.media.mixer.screens.recordings.PlayerViewModel
import com.media.mixer.screens.ringtones.RingtoneScreen
import com.media.mixer.screens.ringtones.RingtonesViewModel
import com.media.mixer.screens.selectvideo.SelectVideoScreen
import com.media.mixer.screens.selectvideo.VideoListViewModel
import com.media.mixer.screens.settings.SettingScreen
import com.media.mixer.screens.video.VideoHome
import com.media.mixer.screens.video.screens.VideoPlayerViewModel
import com.media.mixer.screens.videotoaudio.AudioUiState
import com.media.mixer.screens.videotoaudio.CutterUiState
import com.media.mixer.screens.videotoaudio.MergerUiState
import com.media.mixer.screens.videotoaudio.RingtoneUiState
import com.media.mixer.screens.videotoaudio.TransformerViewModel
import com.media.mixer.screens.videotoaudio.VideoToAudioScreen


@OptIn(UnstableApi::class)
@Composable
fun NavigationGraph(
    navController: NavHostController,
    videoPlayerViewModel: VideoPlayerViewModel = hiltViewModel<VideoPlayerViewModel>()
) {

    NavHost(navController = navController, startDestination = Destination.Home.root) {
        animatedComposable(Destination.Home.root) {
            val context = LocalContext.current as Activity
            val playerViewModel: PlayerViewModel = hiltViewModel()
            val ringtones by playerViewModel.ringtones.collectAsStateWithLifecycle()
            LaunchedEffect(Unit) {
                if (ringtones.isEmpty()) {
                    playerViewModel.loadAllFiles(context)
                }
            }
            HomeScreen(
                ringtones = ringtones,
                onNavigate = {
                    playerViewModel.stopPlaying()
                    navController.navigateTo(it)
                },
                audioPlayer = {
                    playerViewModel.stopPlaying()
                    context.startActivity(Intent(context, AudioActivity::class.java))
                },
            )
        }
        animatedComposable(Destination.MyCreation.root) {
            val context = LocalContext.current
            MyCreation(
                videoToAudioCount = getAvailableFiles(context, StoragePath.VIDEOTOAUDIO.path).size,
                audioCutterCount = getAvailableFiles(context, StoragePath.CUTTER.path).size,
                audioMergerCount = getAvailableFiles(context, StoragePath.MARGER.path).size,
                ringtoneCount = getAvailableFiles(context, StoragePath.RINGTONE.path).size,
                notificationCount = getAvailableFiles(context, StoragePath.NOTIFICATIONS.path).size,
                alarmCount = getAvailableFiles(context, StoragePath.ALARM.path).size,
                voiceRecorderCount = getAvailableFiles(
                    context,
                    StoragePath.VOICERECORDING.path
                ).size,
                goToCreation = { navController.navigateTo("${Destination.Creation.root}/$it") }
            ) {
                navController.navigateUp()
            }
        }
        animatedComposable(
            "${Destination.Creation.root}/{name}",
            arguments = listOf(navArgument("name") {
                type = NavType.StringType
            })
        ) {
            val name = it.arguments?.getString("name")
            val playerViewModel: PlayerViewModel = hiltViewModel()
            val context = LocalContext.current
            LaunchedEffect(Unit) {
                if (name != null) {
                    playerViewModel.loadVoiceRecordingFiles(context, name)
                }
            }
            CreationScreen(files = playerViewModel.files, onBack = navController::navigateUp)
        }
        animatedComposable(Destination.Settings.root) {
            SettingScreen(onBack = navController::navigateUp)
        }
        animatedComposable(Destination.SelectVideo.root) {
            val videoListViewModel = hiltViewModel<VideoListViewModel>()
            val list by videoListViewModel.videosState.collectAsStateWithLifecycle()
            SelectVideoScreen(
                videoList = list,
                onVideo = { navController.navigateTo("${Destination.VideoToAudio.root}/hi/${it.id}/${it.displayName}") },
                onUrl = { navController.navigateTo("${Destination.VideoToAudio.root}/${encode(it)}/${0}/fromUrl") },
                onBack = navController::navigateUp
            )
        }
        animatedComposable(
            "${Destination.VideoToAudio.root}/{path}/{id}/{name}",
            arguments = listOf(navArgument("path") {
                type = NavType.StringType
            }, navArgument("id") {
                type = NavType.LongType
            }, navArgument("name") {
                type = NavType.StringType
            })
        ) {
            val context = LocalContext.current as Activity
            val url = it.arguments?.getString("path")
            val uri = it.arguments?.getLong("id")
            var name = it.arguments?.getString("name")
            if (url != "hi") name = decode(url!!).substringAfterLast("/").replace(".", "")
            val path = if (url == "hi") "content://media/external/video/media/$uri"
            else decode(url)

            val transformerViewModel = hiltViewModel<TransformerViewModel>()
            val musicState by transformerViewModel.musicState.collectAsStateWithLifecycle()
            LaunchedEffect(Unit) {
                if (path != null) {
                    transformerViewModel.filePath = path
                    transformerViewModel.setAndPlay(path)
                    Log.i("TAG1", "NavigationGraph: $path")
                }
            }
            val uiState by transformerViewModel.uiState.collectAsStateWithLifecycle()
            val transformerState by transformerViewModel.transformerState.collectAsStateWithLifecycle()
            val saveLocation by videoPlayerViewModel.saveLocation.collectAsStateWithLifecycle()
            VideoToAudioScreen(
                uiState = uiState,
                name = name!!,
                transformerState=transformerState,
                player = transformerViewModel.player!!,
                musicState = musicState,
                onOutPut = { transformerViewModel.updateUiState(AudioUiState.OutPut) },
                setRingtone = {
                    checkSystemWriteSettings(context) {
                        setTone(
                            context = context,
                            isRingtone = true,
                            sourcePath = transformerViewModel.outputPath
                        )
                    }
                },
                setNotification = {
                    checkSystemWriteSettings(context) {
                        setTone(
                            context = context,
                            isNotification = true,
                            sourcePath = transformerViewModel.outputPath
                        )
                    }
                },
                setAlarm = {
                    checkSystemWriteSettings(context) {
                        setTone(
                            context = context,
                            isAlarm = true,
                            sourcePath = transformerViewModel.outputPath
                        )
                    }
                },
                onSave = {
                    transformerViewModel.startAudioExport(
                        context = context,
                        file = transformerViewModel.filePath,
                        title = name,
                        storagePath = StoragePath.VIDEOTOAUDIO,
                    )
                },
                onSaveAgain = { range ->
                    transformerViewModel.startAudioExportTrim(
                        context = context,
                        file = transformerViewModel.outputPath,
                        start = range.start.toLong(),
                        end = range.endInclusive.toLong(),
                        title = name,
                        storagePath = StoragePath.VIDEOTOAUDIO
                    )
                },
                onCut = { transformerViewModel.updateState(AudioUiState.CutAudio) },
                onSaveAs = {transformerViewModel.saveAsFile(context,saveLocation)},
                onShare = {
                    shareContent(
                        path = transformerViewModel.outputPath,
                        context = context
                    )
                },
                onDelete = {
                    transformerViewModel.delete()
                    navController.navigateUp()
                },
                saveLocation = saveLocation,
                setDefaultDownloadLocation = videoPlayerViewModel::setDefaultDownloadLocation,
                onBack = navController::navigateUp
            )

        }

        animatedComposable(Destination.AudioRecorderScreen.root) {
            val recorderViewModel = hiltViewModel<RecorderViewModel>()
            val uiState by recorderViewModel.uiState.collectAsStateWithLifecycle()
            val transformerState by recorderViewModel.transformerState.collectAsStateWithLifecycle()
            val amplitudes by recorderViewModel.recordedAmplitudes.collectAsStateWithLifecycle()
            val recorderState by recorderViewModel.recorderState.collectAsStateWithLifecycle()
            val recordedTime by recorderViewModel.recordedTime.collectAsStateWithLifecycle()
            val context = LocalContext.current

            val transformerViewModel = hiltViewModel<TransformerViewModel>()
            val musicState by transformerViewModel.musicState.collectAsStateWithLifecycle()
            val saveLocation by videoPlayerViewModel.saveLocation.collectAsStateWithLifecycle()
            AudioRecorderScreen(
                uiState = uiState,
                transformerState=transformerState,
                amplitudes = amplitudes,
                recordedTime = recordedTime,
                recorderState = recorderState,
                onPlay = {
                    if (recorderState == RecorderState.ACTIVE) recorderViewModel.pauseRecording()
                    else recorderViewModel.resumeRecording()
                },
                onSave = {
                    recorderViewModel.stopRecording()
                    recorderViewModel.setUiState(RecorderUiState.LoadingState)
                },
                onRecord = { navController.navigateTo(Destination.AudioRecords.root) },
                onCounterScreen = { recorderViewModel.setUiState(RecorderUiState.WaitState) },
                onStart = {
                    if (PermissionHelper.checkPermissions(
                            context,
                            arrayOf(Manifest.permission.RECORD_AUDIO)
                        )
                    ) {
                        recorderViewModel.startAudioRecorder(context)
                        recorderViewModel.setUiState(RecorderUiState.RecordingState)
                    }
                },
                onSaveProcessState = {
                    recorderViewModel.setUiState(RecorderUiState.SaveProcessState)
                    transformerViewModel.outputPath = recorderViewModel.outputPath
                    transformerViewModel.setAndPlay("file://${recorderViewModel.outputPath}")
                },
                player = transformerViewModel.player!!,
                musicState = musicState,
                title = "OutPutAudio",
                setRingtone = {
                    checkSystemWriteSettings(context) {
                        setTone(
                            context = context,
                            isRingtone = true,
                            sourcePath = transformerViewModel.outputPath
                        )
                    }
                },
                setNotification = {
                    checkSystemWriteSettings(context) {
                        setTone(
                            context = context,
                            isNotification = true,
                            sourcePath = transformerViewModel.outputPath
                        )
                    }
                },
                setAlarm = {
                    checkSystemWriteSettings(context) {
                        setTone(
                            context = context,
                            isAlarm = true,
                            sourcePath = transformerViewModel.outputPath
                        )
                    }
                },
                onCut = {recorderViewModel.setUiState(RecorderUiState.CutState)},
                onSaveAs = {transformerViewModel.saveAsFile(context,saveLocation)},
                onShare = {
                    shareContent(
                        path = transformerViewModel.outputPath,
                        context = context
                    )
                },
                onClose = {
                    recorderViewModel.stopRecording()
                    recorderViewModel.delete()
                    recorderViewModel.setUiState(RecorderUiState.StartState)
                },
                onDelete = {
                    navController.navigateUp()
                    transformerViewModel.delete()
                },
                saveLocation = saveLocation,
                setDefaultDownloadLocation = videoPlayerViewModel::setDefaultDownloadLocation,
                onSaveCut = { range ->
                    recorderViewModel.setUiState(RecorderUiState.CutLoadingState)
                    recorderViewModel.startAudioExportTrim(
                        context = context,
                        file = recorderViewModel.outputPath,
                        start = range.start.toLong(),
                        end = range.endInclusive.toLong(),
                        format = "MP3",
                        title = "voiceRecording",
                        storagePath = StoragePath.VOICERECORDING
                    )
                },
                onBack = {
                    recorderViewModel.stopRecording()
                    navController.navigateUp()
                })
        }
        animatedComposable(Destination.AudioRecords.root) {
            AudioRecordView(onBack = {
                navController.navigateUp()
            })
        }
        animatedComposable(Destination.AudioMerger.root) {
            val audioMergerViewModel = hiltViewModel<AudioMergerViewModel>()
            val margeState by audioMergerViewModel.margeState.collectAsStateWithLifecycle()
            val musicState by audioMergerViewModel.musicState.collectAsStateWithLifecycle()
            val selectedSong by audioMergerViewModel.selectedSong.collectAsStateWithLifecycle()
            val transformerState by audioMergerViewModel.transformerState.collectAsStateWithLifecycle()
            val audioListViewModel = hiltViewModel<AudioListViewModel>()
            val allAudio by audioListViewModel.audioList.collectAsStateWithLifecycle()
            val context = LocalContext.current
            val saveLocation by videoPlayerViewModel.saveLocation.collectAsStateWithLifecycle()
            AudioMerger(
                allAudio = allAudio,
                mergerState = margeState,
                player = audioMergerViewModel.player!!,
                listOfSong = selectedSong,
                musicState = musicState,
                transformerState = transformerState,
                removeAt = { audioMergerViewModel.removeAt(it) },
                setPlayer = {
                    audioMergerViewModel.setAndPlay(it.mediaUri.toString())
                },
                onSave = { rangeList ->
                    audioMergerViewModel.startAudioCutting(
                        rangeList,
                        context = context
                    )
                    audioMergerViewModel.updateMergeState(MergerUiState.Loading)
                },
                onSaveAgain = { range ->
                    audioMergerViewModel.updateMergeState(MergerUiState.Loading)
                    audioMergerViewModel.startAudioExportTrim(
                        context = context,
                        file = audioMergerViewModel.finalOutput,
                        start = range.start.toLong(),
                        end = range.endInclusive.toLong(),
                        format = "MP3",
                        title = "merge",
                        storagePath = StoragePath.MARGER
                    )
                },
                onCut = {
                    audioMergerViewModel.updateMergeState(MergerUiState.CutAgain)
                },
                onOutPut = { audioMergerViewModel.updateMergeState(MergerUiState.OutPut) },
                onNext = { song ->
                    audioMergerViewModel.setAudioPath(song)
                    audioMergerViewModel.updateMergeState(MergerUiState.AudioMerger)
                },
                setRingtone = {
                    checkSystemWriteSettings(context) {
                        setTone(
                            context = context,
                            isRingtone = true,
                            sourcePath = audioMergerViewModel.finalOutput
                        )
                    }
                },
                setNotification = {
                    checkSystemWriteSettings(context) {
                        setTone(
                            context = context,
                            isNotification = true,
                            sourcePath = audioMergerViewModel.finalOutput
                        )
                    }
                },
                setAlarm = {
                    checkSystemWriteSettings(context) {
                        setTone(
                            context = context,
                            isAlarm = true,
                            sourcePath = audioMergerViewModel.finalOutput
                        )
                    }
                },
                onSaveAs = {audioMergerViewModel.saveAsFile(context,saveLocation)},
                onShare = {
                    shareContent(
                        path = audioMergerViewModel.finalOutput,
                        context = context
                    )
                },
                onDelete = {
                    navController.navigateUp()
                },
                saveLocation = saveLocation,
                setDefaultDownloadLocation = videoPlayerViewModel::setDefaultDownloadLocation,
                onBack = {
                    navController.navigateUp()
                })
        }
        animatedComposable(Destination.AudioCutter.root) {
            val audioCutterViewModel = hiltViewModel<AudioCutterViewModel>()
            val trimState by audioCutterViewModel.trimState.collectAsStateWithLifecycle()
            val musicState by audioCutterViewModel.musicState.collectAsStateWithLifecycle()
            val transformerState by audioCutterViewModel.transformerState.collectAsStateWithLifecycle()
            val audioListViewModel = hiltViewModel<AudioListViewModel>()
            val allAudio by audioListViewModel.audioList.collectAsStateWithLifecycle()
            val saveLocation by videoPlayerViewModel.saveLocation.collectAsStateWithLifecycle()
            val context = LocalContext.current

            AudioCutterScreen(
                allAudio = allAudio,
                trimState = trimState,
                transformerState = transformerState,
                player = audioCutterViewModel.player!!,
                title = audioCutterViewModel.song?.title ?: "",
                duration = audioCutterViewModel.song?.duration?.toLong() ?: 0,
                musicState = musicState,
                onSave = { range ->
                    audioCutterViewModel.updateTrimState(CutterUiState.Loading)
                    audioCutterViewModel.startAudioExport(
                        context = context,
                        file = audioCutterViewModel.audioPath,
                        start = range.start.toLong(),
                        end = range.endInclusive.toLong(),
                        format = "MP3",
                        title = audioCutterViewModel.song?.title ?: "output",
                        storagePath = StoragePath.CUTTER
                    )
                },
                onSaveAgain = { range ->
                    audioCutterViewModel.updateTrimState(CutterUiState.Loading)
                    audioCutterViewModel.startAudioExport(
                        context = context,
                        file = audioCutterViewModel.outputAudioPath,
                        start = range.start.toLong(),
                        end = range.endInclusive.toLong(),
                        format = "MP3",
                        title = audioCutterViewModel.song?.title ?: "output",
                        storagePath = StoragePath.CUTTER
                    )
                },
                onCut = {
                    audioCutterViewModel.updateTrimState(CutterUiState.CutAgain)
                },
                onOutPut = { audioCutterViewModel.updateTrimState(CutterUiState.OutPut) },
                onNext = { song ->
                    audioCutterViewModel.song = song
                    audioCutterViewModel.audioPath = song.mediaUri.toString()
                    audioCutterViewModel.setAndPlay(song.mediaUri.toString())
                    audioCutterViewModel.updateTrimState(CutterUiState.AudioCutter)
                },
                setRingtone = {
                    checkSystemWriteSettings(context) {
                        setTone(
                            context = context,
                            isRingtone = true,
                            sourcePath = audioCutterViewModel.outputAudioPath
                        )
                    }
                },
                setNotification = {
                    setTone(
                        context = context,
                        isNotification = true,
                        sourcePath = audioCutterViewModel.outputAudioPath
                    )
                },
                setAlarm = {
                    setTone(
                        context = context,
                        isAlarm = true,
                        sourcePath = audioCutterViewModel.outputAudioPath
                    )
                },
                onSaveAs = {
                    audioCutterViewModel.saveAsFile(context,saveLocation)
                },
                onShare = {
                    shareContent(
                        path = audioCutterViewModel.outputAudioPath,
                        context = context
                    )
                },
                onDelete = {
                    navController.navigateUp()
                    audioCutterViewModel.delete()
                },
                saveLocation = saveLocation,
                setDefaultDownloadLocation = videoPlayerViewModel::setDefaultDownloadLocation,
                onBack = navController::navigateUp
            )
        }
        animatedComposable(Destination.Ringtones.root) {
            val audioListViewModel = hiltViewModel<AudioListViewModel>()
            val allAudio by audioListViewModel.audioList.collectAsStateWithLifecycle()
            val ringtonesViewModel = hiltViewModel<RingtonesViewModel>()
            val ringtoneUiState by ringtonesViewModel.ringtone.collectAsStateWithLifecycle()
            val transformerState by ringtonesViewModel.transformerState.collectAsStateWithLifecycle()
            val musicState by ringtonesViewModel.musicState.collectAsStateWithLifecycle()
            val selectedSong by ringtonesViewModel.selectedSong.collectAsStateWithLifecycle()
            val context = LocalContext.current
            RingtoneScreen(
                allAudio = allAudio,
                ringtoneUiState = ringtoneUiState,
                transformerState=transformerState,
                player = ringtonesViewModel.player!!,
                musicState = musicState,
                title = ringtonesViewModel.currentSong?.title ?: "",
                duration = ringtonesViewModel.currentSong?.duration?.toLong() ?: 0,
                listOfSong = selectedSong,
                goToOutPut = {
                    ringtonesViewModel.currentSong = it
                    Log.i("TAG", "NavigationGraph: currentSong:$it")
                    ringtonesViewModel.setAndPlay(it.mediaUri.toString())
                    ringtonesViewModel.updateUiState(RingtoneUiState.OutPut)
                },
                onCut = { ringtonesViewModel.updateUiState(RingtoneUiState.Cut) },
                onMerge = { ringtonesViewModel.updateUiState(RingtoneUiState.Selection) },
                setPlayer = { ringtonesViewModel.setAndPlay(it.mediaUri.toString()) },
                onOutPut = { ringtonesViewModel.updateUiState(RingtoneUiState.OutPut) },
                onSaveMerge = { rangeList ->
                    ringtonesViewModel.updateUiState(RingtoneUiState.Loading)
                    ringtonesViewModel.startAudioCutting(
                        rangeList,
                        context = context
                    )
                },
                removeAt = { ringtonesViewModel.removeAt(it) },
                onSaveCut = { range ->
                    ringtonesViewModel.updateUiState(RingtoneUiState.Loading)
                    ringtonesViewModel.startAudioExportTrim(
                        context = context,
                        file = ringtonesViewModel.finalOutput.ifEmpty { ringtonesViewModel.currentSong!!.mediaUri.toString() },
                        start = range.start.toLong(),
                        end = range.endInclusive.toLong(),
                        format = "MP3",
                        title = "ringtone",
                        storagePath = StoragePath.CUTTER
                    )
                },
                onStartMerge = { song ->
                    ringtonesViewModel.setAudioPath(listOf(ringtonesViewModel.currentSong!!, song))
                    ringtonesViewModel.updateUiState(RingtoneUiState.Merge)
                },
                setRingtone = {
                    checkSystemWriteSettings(context) {
                        setTone(
                            context = context,
                            isRingtone = true,
                            sourcePath = ringtonesViewModel.finalOutput
                        )
                    }
                },
                setNotification = {
                    checkSystemWriteSettings(context) {
                        setTone(
                            context = context,
                            isNotification = true,
                            sourcePath = ringtonesViewModel.finalOutput
                        )
                    }
                },
                setAlarm = {
                    checkSystemWriteSettings(context) {
                        setTone(
                            context = context,
                            isAlarm = true,
                            sourcePath = ringtonesViewModel.finalOutput
                        )
                    }
                },
                onDelete = {
                    ringtonesViewModel.delete()
                    navController.navigateUp()
                },
                onBack = navController::navigateUp
            )

        }
        animatedComposable(Destination.VideoPlayer.root) {
            VideoHome(
                onBack = navController::navigateUp,
                onVideoClick = { uri, name ->
                    videoPlayerViewModel.startVideo(uri)
                    navController.navigate(Destination.VideoPlayerView.root + "/${encode(uri.toString())}/$name")
                })
        }
        animatedComposable(
            "${Destination.VideoPlayerView.root}/{path}/{name}",
            arguments = listOf(navArgument("path") {
                type = NavType.StringType
            }, navArgument("name") {
                type = NavType.StringType
            })
        ) {
            val url = it.arguments?.getString("path") ?: "dgag"
            var name = it.arguments?.getString("name") ?: "video"
            val videoUiState by videoPlayerViewModel.videoUiState.collectAsStateWithLifecycle()
            val context = LocalContext.current

            VideoPlayerScreen(
                title = name,
                player = videoPlayerViewModel.getVideoPlayer()!!,
                uiState = videoUiState,
                onAudioEvent = videoPlayerViewModel::onAudioEvent,
                onResize = videoPlayerViewModel::setVideoSize,
                onShare = {
                    shareContent(
                        path = decode(url),
                        context = context
                    )
                },
                onBack = {
                    videoPlayerViewModel.saveState()
                    navController.navigateUp()
                }
            )
        }
    }

}

fun NavHostController.navigateTo(route: String) = this.navigate(route) {
    launchSingleTop = true
    restoreState = true
}