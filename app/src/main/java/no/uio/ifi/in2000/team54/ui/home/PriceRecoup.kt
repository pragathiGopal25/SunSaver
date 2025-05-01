package no.uio.ifi.in2000.team54.ui.home

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp

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

    viewModel.calculateRecoup(uiState.totalPrice)
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
            Text("Tid til du har tjent inn din investering i solcellepaneler:")
            Text("${uiState.timeUntilRecoup} ÅR")
        }
    }
}