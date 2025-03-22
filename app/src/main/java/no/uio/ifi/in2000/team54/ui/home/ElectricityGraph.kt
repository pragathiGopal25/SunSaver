package no.uio.ifi.in2000.team54.ui.home

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.foundation.layout.wrapContentWidth
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.unit.TextUnit
import androidx.compose.ui.unit.dp
import com.patrykandpatrick.vico.compose.cartesian.CartesianChartHost
import com.patrykandpatrick.vico.compose.cartesian.axis.rememberBottom
import com.patrykandpatrick.vico.compose.cartesian.axis.rememberStart
import com.patrykandpatrick.vico.compose.cartesian.cartesianLayerPadding
import com.patrykandpatrick.vico.compose.cartesian.layer.rememberLineCartesianLayer
import com.patrykandpatrick.vico.compose.cartesian.rememberCartesianChart
import com.patrykandpatrick.vico.core.cartesian.axis.HorizontalAxis
import com.patrykandpatrick.vico.core.cartesian.axis.VerticalAxis
import com.patrykandpatrick.vico.core.cartesian.data.CartesianChartModelProducer
import com.patrykandpatrick.vico.core.cartesian.data.CartesianValueFormatter
import com.patrykandpatrick.vico.core.cartesian.data.lineSeries
import com.patrykandpatrick.vico.core.common.component.TextComponent

@Composable
fun ElectricityGraph (modifier: Modifier){
    val modelProducer = remember { CartesianChartModelProducer() }
    val monthFormatter = CartesianValueFormatter { _, value, _ ->
    val months = listOf(
        "Jan", "Feb", "Mar", "Apr", "May", "Jun",
        "Jul", "Aug", "Sep", "Oct", "Nov", "Dec"    )
    val index = value.toInt().coerceIn(0, months.size - 1)  // Keep within bounds
    months[index]
    }
    Box(modifier = Modifier
        .fillMaxSize(),
        contentAlignment = Alignment.Center) {
        LaunchedEffect(Unit) {
            modelProducer.runTransaction {
                lineSeries { series(13, 8, 7, 12, 0, 1, 15, 14, 0, 11, 6, 12) }
            }
        }


        CartesianChartHost(
            rememberCartesianChart(
                rememberLineCartesianLayer(),
                startAxis = VerticalAxis.rememberStart(
                    title = "Electricity (kwM)", titleComponent = TextComponent()
                ),
                bottomAxis = HorizontalAxis.rememberBottom(
                    title = "Time (Months)",
                    titleComponent = TextComponent(),
                    valueFormatter = monthFormatter
                )
            ),
            modelProducer,
            modifier = Modifier.padding(2.dp)
        )

    }

}