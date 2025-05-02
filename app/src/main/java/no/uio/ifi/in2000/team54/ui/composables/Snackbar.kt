package no.uio.ifi.in2000.team54.ui.composables

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxHeight
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
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import no.uio.ifi.in2000.team54.ui.theme.Red
import no.uio.ifi.in2000.team54.ui.theme.SoftRed

@Composable
fun Snackbar(snackbarState: SnackbarHostState) {

    SnackbarHost(hostState = snackbarState) {
        val message = snackbarState.currentSnackbarData?.visuals?.message

        Box(
            modifier = Modifier
                .fillMaxWidth()
                .fillMaxHeight()
                .padding(horizontal = 16.dp)
                .padding(top = 100.dp)
        ) {
            Text(
                modifier = Modifier
                    .padding(4.dp)
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(20))
                    .background(SoftRed)
                    .border(3.dp, Red, RoundedCornerShape(20))
                    .padding(vertical = 10.dp)
                    .align(Alignment.TopCenter),
                text = message ?: "", // need to do this or it breaks the fade-out animation
                color = Red,
                fontSize = 16.sp,
                fontWeight = FontWeight.SemiBold,
                textAlign = TextAlign.Center,
            )
        }
    }
}