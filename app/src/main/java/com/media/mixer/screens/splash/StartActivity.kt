package com.media.mixer.screens.splash

import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.view.WindowManager
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Surface
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.paint
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.core.view.WindowCompat
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.media.mixer.MainActivity
import com.media.mixer.R
import com.media.mixer.core.utils.START_ACTIVITY
import com.media.mixer.core.utils.hideSystemUI
import com.media.mixer.ui.theme.MyMixerTheme
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import javax.inject.Inject

@AndroidEntryPoint
class StartActivity : ComponentActivity() {

    @Inject
    lateinit var dataStore: DataStore<Preferences>
    private var newUser: Boolean = true
    override fun onCreate(savedInstanceState: Bundle?) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P) {
            window.attributes.layoutInDisplayCutoutMode =
                WindowManager.LayoutParams.LAYOUT_IN_DISPLAY_CUTOUT_MODE_SHORT_EDGES
        }
        WindowCompat.setDecorFitsSystemWindows(window, false)
        hideSystemUI()
        super.onCreate(savedInstanceState)
        setContent {
            MyMixerTheme {
                // A surface container using the 'background' color from the theme
//                val brush = Brush.linearGradient(listOfColor)
                val scope = rememberCoroutineScope()
                LaunchedEffect(Unit) {
                    launch {
                        dataStore.data.collectLatest {
                            newUser = it[booleanPreferencesKey(START_ACTIVITY)]?:true
                        }
                    }
                }
                Surface(
                    modifier = Modifier.fillMaxSize().paint(
                        painterResource(id = R.drawable.bg_video_audio),
                        contentScale = ContentScale.FillBounds
                    ),
                    color = Color.Transparent
                ) {
                    val navController = rememberNavController()
                    NavHost(
                        navController = navController,
                        startDestination = SplashDestination.SplashScreen.root
                    ) {
                        composable(SplashDestination.SplashScreen.root) {
                            LaunchedEffect(Unit) {
                                delay(5000)
                                if (newUser) navController.navigate(SplashDestination.ContinueScreen.root)
                                else {
                                    startActivity(
                                        Intent(
                                            this@StartActivity,
                                            MainActivity::class.java
                                        )
                                    )
                                    finish()
                                }
                            }
                            SplashScreen()
                        }
                        composable(SplashDestination.ContinueScreen.root) {
                            ContinueScreen {
                                scope.launch {
                                    withContext(Dispatchers.IO){
                                        dataStore.edit {
                                            it[booleanPreferencesKey(START_ACTIVITY)] = false
                                        }
                                    }
                                    startActivity(Intent(this@StartActivity, MainActivity::class.java))
                                    finish()
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}

