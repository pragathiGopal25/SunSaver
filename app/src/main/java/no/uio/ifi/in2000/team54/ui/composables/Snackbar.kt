package no.uio.ifi.in2000.team54.ui.composables

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Box
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
import no.uio.ifi.in2000.team54.ui.theme.Tamarillo
import no.uio.ifi.in2000.team54.ui.theme.Shilo

@Composable
fun Snackbar(snackbarState: SnackbarHostState) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .padding(top = 15.dp)
            .padding(horizontal = 35.dp),
        contentAlignment = Alignment.TopCenter
    ) {
        SnackbarHost(hostState = snackbarState) {
            val message = snackbarState.currentSnackbarData?.visuals?.message
            Text(
                modifier = Modifier
                    .padding(4.dp)
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(20))
                    .background(Shilo)
                    .border(3.dp, Tamarillo, RoundedCornerShape(20))
                    .padding(vertical = 10.dp),
                text = message ?: "",
                color = Tamarillo,
                fontSize = 16.sp,
                fontWeight = FontWeight.SemiBold,
                textAlign = TextAlign.Center,
            )
        }
    }
}