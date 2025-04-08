package no.uio.ifi.in2000.team54

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen
import no.uio.ifi.in2000.team54.ui.home.HomeScreen
import no.uio.ifi.in2000.team54.ui.home.MainScreen
import no.uio.ifi.in2000.team54.ui.theme.Team54Theme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        installSplashScreen()
        super.onCreate(savedInstanceState)
        setContent {
            Team54Theme {
                MainScreen()
            }
        }
    }
}
