package com.media.mixer.screens.splash

sealed class SplashDestination(val root: String) {
    data object SplashScreen : SplashDestination(root = "splash_screen")
    data object ContinueScreen : SplashDestination("continue_screen")
}