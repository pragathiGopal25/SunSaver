package no.uio.ifi.in2000.team54

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent

import no.uio.ifi.in2000.team54.ui.theme.Team54Theme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            Team54Theme {
            }
        }
    }
}
