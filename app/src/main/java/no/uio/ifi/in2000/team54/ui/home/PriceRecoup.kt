package no.uio.ifi.in2000.team54.ui.home

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import no.uio.ifi.in2000.team54.ui.theme.YellowText
import java.util.Locale

@Composable
fun TimeUntilRecouped(viewModel: HomeViewModel) {
    val uiState by viewModel.homeUiState.collectAsState()
    val loadingState by viewModel.priceLoadingState.collectAsState()

    if (loadingState.loadingMessage != "") {
        Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
            Text(loadingState.loadingMessage)
        }
        return
    }

    Box(
        modifier = Modifier.fillMaxWidth(),
        contentAlignment = Alignment.Center,
    ) {
        Column(
            modifier = Modifier
                .padding(bottom = 8.dp, top = 3.dp, start = 4.dp, end = 4.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Text("Du vil ha tjent tilbake investeringen din i solcellepaneler om", textAlign = TextAlign.Center)
            Spacer(Modifier.height(10.dp))
            RecoupText(uiState.timeUntilRecoup)
        }
    }
}
@Composable
private fun RecoupText(time: Double) {
    Row(
        verticalAlignment = Alignment.Bottom,
        horizontalArrangement = Arrangement.spacedBy(2.dp)
    ) {
        Text(
            text = time.toString(),
            color = YellowText,
            fontSize = 40.sp,
            fontWeight = FontWeight.Bold
        )

        Text(
            text = "år",
            color = YellowText,
            fontSize = 36.sp,
            fontWeight = FontWeight.Medium
        )
    }
}
