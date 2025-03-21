package no.uio.ifi.in2000.team54.ui.home

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

val viewModel = ElectricityPriceViewModel()

@Composable
fun ElectricityBillOverview() {
    val uiState by viewModel.uiState.collectAsState()
    Box(
        modifier = Modifier
            .shadow(
                elevation = 10.dp,
                shape = RoundedCornerShape(8.dp)
            )
            .width(350.dp)
            .background(color = MaterialTheme.colorScheme.tertiary) // Vi endrer farger senere
            .height(170.dp),
        contentAlignment = Alignment.TopCenter,
    ) {
        Column(
            modifier = Modifier
                .padding(bottom = 8.dp, top = 3.dp, start = 4.dp, end = 4.dp),
        ) {
            Text(
                text = "Electricity price comparison by NOK per kWh the last 24 hours",
                fontSize = 13.sp
            )
            Spacer(Modifier.height(10.dp))
            Text(
                text = "Average : ${uiState.realPrice}",
                fontSize = 17.sp,
            )
            Text(
                "Average with solar array: ${uiState.solarPrice}",
                fontSize = 17.sp,
            )
            Spacer(Modifier.height(10.dp))
            HorizontalDivider(
                thickness = 1.dp,
            )
            Spacer(Modifier.height(10.dp))
            Text(
                "Saved: ${uiState.realPrice - uiState.solarPrice} NOK",
                fontSize = 17.sp,
                fontWeight = FontWeight.Bold
            )
        }
    }
}