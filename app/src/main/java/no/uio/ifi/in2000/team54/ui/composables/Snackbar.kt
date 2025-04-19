package no.uio.ifi.in2000.team54.ui.composables

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import no.uio.ifi.in2000.team54.ui.theme.LightestYellow
import no.uio.ifi.in2000.team54.ui.theme.Red

@Composable
fun Snackbar(snackbarState: SnackbarHostState) {
    SnackbarHost(hostState = snackbarState) {
        val message = snackbarState.currentSnackbarData?.visuals?.message

        Box(
            modifier = Modifier
                .fillMaxSize()
        ) {
            Text(
                modifier = Modifier
                    .padding(4.dp)
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(15))
                    .background(LightestYellow)
                    .border(1.dp, Red, RoundedCornerShape(15))
                    .padding(vertical = 10.dp)
                    .align(Alignment.TopCenter),
                text = message ?: "",
                color = Color.Black,
                fontSize = 16.sp,
                textAlign = TextAlign.Center,
            )
        }
    }
}