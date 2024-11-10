package com.media.mixer.navigations

sealed class Destination(val root: String) {
    data object Home : Destination(root = "home_screen")
    data object SelectVideo : Destination(root = "select_video_screen")
    data object VideoToAudio : Destination("video_to_audio_screen")
    data object AudioRecorderScreen : Destination("audio_recorder_screen")
    data object AudioRecords : Destination("audio_records_screen")
    data object AudioMerger : Destination("audio_merger_screen")
    data object AudioCutter : Destination("audio_cutter_screen")
    data object Ringtones : Destination("audio_ringtone_screen")
    data object VideoPlayer : Destination("video_player_screen")
    data object VideoPlayerView : Destination("video_player_view_screen")
    data object MyCreation : Destination("my_creation_screen")
    data object Creation : Destination("creation_screen")
    data object Settings : Destination("settings_screen")
}