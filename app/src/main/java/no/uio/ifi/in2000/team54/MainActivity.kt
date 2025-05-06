package no.uio.ifi.in2000.team54

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen
import dagger.hilt.android.AndroidEntryPoint
import no.uio.ifi.in2000.team54.ui.splashscreen.SunSplashScreen

import no.uio.ifi.in2000.team54.ui.theme.Team54Theme

@AndroidEntryPoint
class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        installSplashScreen()
        super.onCreate(savedInstanceState)
        setContent {
            Team54Theme {
                SunSplashScreen()
            }
        }
    }
}
