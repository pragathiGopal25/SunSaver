package no.uio.ifi.in2000.team54.ui.home

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.patrykandpatrick.vico.compose.cartesian.CartesianChartHost
import com.patrykandpatrick.vico.compose.cartesian.axis.rememberBottom
import com.patrykandpatrick.vico.compose.cartesian.axis.rememberStart
import com.patrykandpatrick.vico.compose.cartesian.layer.rememberLineCartesianLayer
import com.patrykandpatrick.vico.compose.cartesian.rememberCartesianChart
import com.patrykandpatrick.vico.compose.cartesian.rememberVicoScrollState
import com.patrykandpatrick.vico.core.cartesian.axis.HorizontalAxis
import com.patrykandpatrick.vico.core.cartesian.axis.VerticalAxis
import com.patrykandpatrick.vico.core.cartesian.data.CartesianChartModelProducer
import com.patrykandpatrick.vico.core.cartesian.data.CartesianValueFormatter
import com.patrykandpatrick.vico.core.cartesian.data.lineSeries
import com.patrykandpatrick.vico.core.common.component.TextComponent
@Composable
fun ElectricityGraph (modifier: Modifier = Modifier){
    val modelProducer = remember { CartesianChartModelProducer() }
    val monthFormatter = CartesianValueFormatter { _, value, _ -> // overriding "format" method in CastertianValueFormatter
        val months = listOf(
            "Jan", "Feb", "Mar", "Apr", "Mai", "Jun",
            "Jul", "Aug", "Sep", "Okt", "Nov", "Des"
        )
        val index = value.toInt().coerceIn(0, months.size - 1)  // Keep within bounds
        months[index]
    }

    Box(modifier = modifier.fillMaxSize(),
        contentAlignment = Alignment.Center,
        ) {
        LaunchedEffect(Unit) {
            modelProducer.runTransaction {
                lineSeries { series(16, 41, 91, 118, 133, 137, 130, 112, 84, 52, 21, 10) }
            }
        }
        CartesianChartHost(
            rememberCartesianChart(
                rememberLineCartesianLayer(),
                startAxis = VerticalAxis.rememberStart(
                    title = "Strøm produsert (kWh)",
                    titleComponent = TextComponent()
                ),
                bottomAxis = HorizontalAxis.rememberBottom(
                    title = "Tid",
                    titleComponent = TextComponent(),
                    valueFormatter = monthFormatter
                )
            ),
            modelProducer,
            modifier = modifier.padding(16.dp),
            scrollState = rememberVicoScrollState(scrollEnabled = false) // disabled so it fits the box
        )
    }
}
