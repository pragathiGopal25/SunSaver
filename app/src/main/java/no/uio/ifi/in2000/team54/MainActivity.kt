package no.uio.ifi.in2000.team54

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import no.uio.ifi.in2000.team54.ui.managesolararray.ManageSolarArrayScreen
import no.uio.ifi.in2000.team54.ui.managesolararray.ManageSolarArrayViewModel
import no.uio.ifi.in2000.team54.ui.theme.Team54Theme

class MainActivity : ComponentActivity() {
    private val manageSolarArrayViewModel = ManageSolarArrayViewModel()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            Team54Theme {
                ManageSolarArrayScreen(manageSolarArrayViewModel)
            }
        }
    }
}

