package no.uio.ifi.in2000.team54.ui.home

import android.text.Layout
import android.util.Log
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.patrykandpatrick.vico.compose.cartesian.CartesianChartHost
import com.patrykandpatrick.vico.compose.cartesian.axis.rememberAxisGuidelineComponent
import com.patrykandpatrick.vico.compose.cartesian.axis.rememberBottom
import com.patrykandpatrick.vico.compose.cartesian.axis.rememberStart
import com.patrykandpatrick.vico.compose.cartesian.layer.rememberLineCartesianLayer
import com.patrykandpatrick.vico.compose.cartesian.marker.rememberDefaultCartesianMarker
import com.patrykandpatrick.vico.compose.cartesian.rememberCartesianChart
import com.patrykandpatrick.vico.compose.cartesian.rememberVicoScrollState
import com.patrykandpatrick.vico.compose.common.component.fixed
import com.patrykandpatrick.vico.compose.common.component.rememberShapeComponent
import com.patrykandpatrick.vico.compose.common.component.rememberTextComponent
import com.patrykandpatrick.vico.compose.common.fill
import com.patrykandpatrick.vico.compose.common.insets
import com.patrykandpatrick.vico.compose.common.shape.markerCorneredShape
import com.patrykandpatrick.vico.core.cartesian.axis.HorizontalAxis
import com.patrykandpatrick.vico.core.cartesian.axis.VerticalAxis
import com.patrykandpatrick.vico.core.cartesian.data.CartesianChartModelProducer
import com.patrykandpatrick.vico.core.cartesian.data.CartesianValueFormatter
import com.patrykandpatrick.vico.core.cartesian.data.lineSeries
import com.patrykandpatrick.vico.core.cartesian.marker.CartesianMarker
import com.patrykandpatrick.vico.core.cartesian.marker.DefaultCartesianMarker
import com.patrykandpatrick.vico.core.common.LayeredComponent
import com.patrykandpatrick.vico.core.common.component.ShapeComponent
import com.patrykandpatrick.vico.core.common.component.TextComponent
import com.patrykandpatrick.vico.core.common.shape.CorneredShape

/*
val data = mapOf( // test data for both components
    "Strøm" to listOf( 16.0, 41.2, 91.0, 118.2, 133.3, 137.0, 130.0, 112.0, 84.0, 52.0, 21.0, 10.0),
    "Solenergi" to listOf(6.0, 21.2, 41.0, 50.2, 64.3, 79.0, 90.0, 62.0, 34.0, 22.0, 5.0, 10.0)
)
*/
// x values
val monthFormatter = CartesianValueFormatter { _, value, _ -> // overriding "format" method in CastertianValueFormatter
    val months = listOf(
        "Jan", "Feb", "Mar", "Apr", "Mai", "Jun",
        "Jul", "Aug", "Sep", "Okt", "Nov", "Des"
    )
    val index = value.toInt().coerceIn(0, months.size - 1)  // Keep within bounds
    months[index]
}

@Composable
fun EletricityGraphContainer(
    modifier: Modifier = Modifier,
    viewModel: HomeScreenViewModel = HomeScreenViewModel()
) {

    val graphDataUiState by viewModel.graphDataUiState.collectAsStateWithLifecycle()

    if (graphDataUiState.solarIrradianceData == emptyMap<String, List<Double>>()) {
        Text("laster")
    } else {
        ElectricityGraph(data = graphDataUiState.solarIrradianceData)
    }

}

@Composable
fun ElectricityGraph (
    modifier: Modifier = Modifier,
    data: Map<String, List<Double>>
){
    val modelProducer = remember { CartesianChartModelProducer() }
    Log.i("testGraph", data.toString())

    Box(modifier = modifier,
        contentAlignment = Alignment.Center,
        ) {
        LaunchedEffect(Unit) {
            modelProducer.runTransaction {
                lineSeries { data.forEach{(_, list) -> series(list)}}
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
                    title = "Tid (Måneder)",
                    titleComponent = TextComponent(),
                    valueFormatter = monthFormatter
                ),
                marker = rememberMarker()
            ),
            modelProducer,
            modifier = modifier.padding(10.dp).height(250.dp),
            scrollState = rememberVicoScrollState(scrollEnabled = false), // disabled so it fits the box

        )
    }
}
/*
private val LegendLabelKey = ExtraStore.Key<Set<String>>()
@Composable
fun ElectricityGraph2(modifier: Modifier = Modifier) {
    val modelProducer = remember { CartesianChartModelProducer() }

    LaunchedEffect(Unit) {
        modelProducer.runTransaction {
            lineSeries { data.forEach { (_, map) -> series(map) } }
            extras { extraStore -> extraStore[LegendLabelKey] = data.keys }
        }
    }

    val lineColors = listOf(Color(0xff916cda), Color(0xffd877d8))
    val legendItemLabelComponent = rememberTextComponent(vicoTheme.textColor)
    CartesianChartHost(
        rememberCartesianChart(
            rememberLineCartesianLayer(
                LineCartesianLayer.LineProvider.series(
                    lineColors.map { color ->
                        LineCartesianLayer.rememberLine(
                            fill = LineCartesianLayer.LineFill.single(fill(color)),
                            areaFill = null,
                            pointProvider =
                            LineCartesianLayer.PointProvider.single(
                                LineCartesianLayer.point(rememberShapeComponent(fill(color), CorneredShape.Pill))
                            ),
                        )
                    }
                )
            ),
            startAxis = VerticalAxis.rememberStart(
                title = "Strøm produsert (kWh)",
                titleComponent = TextComponent()
            ),
            bottomAxis = HorizontalAxis.rememberBottom(
                title = "Tid (Måneder)",
                titleComponent = TextComponent(),
                valueFormatter = monthFormatter
            ),
            marker = rememberMarker(),
            legend =
            rememberVerticalLegend(
                items = { extraStore ->
                    extraStore[LegendLabelKey].forEachIndexed { index, label ->
                        add(
                            LegendItem(
                                shapeComponent(fill(lineColors[index]), CorneredShape.Pill),
                                legendItemLabelComponent,
                                label,
                            )
                        )
                    }
                },
                padding = insets(top = 10.dp),
            ),
        ),
        modelProducer,
        modifier.padding(10.dp).height(300.dp),
        rememberVicoScrollState(scrollEnabled = false),
    )
}

*/
// copied from vicos github, some adjustments to be made
@Composable
fun rememberMarker(
    valueFormatter: DefaultCartesianMarker.ValueFormatter =
        DefaultCartesianMarker.ValueFormatter.default(),
    showIndicator: Boolean = true,
): CartesianMarker {
    val labelBackgroundShape = markerCorneredShape(CorneredShape.Corner.Rounded)
    val labelBackground =
        rememberShapeComponent(
            fill = fill(MaterialTheme.colorScheme.background),
            shape = labelBackgroundShape,
            strokeThickness = 1.dp,
            strokeFill = fill(MaterialTheme.colorScheme.outline),
        )
    val label =
        rememberTextComponent(
            color = MaterialTheme.colorScheme.onSurface,
            textAlignment = Layout.Alignment.ALIGN_CENTER,
            padding = insets(8.dp, 4.dp),
            background = labelBackground,
            minWidth = TextComponent.MinWidth.fixed(40.dp),
        )
    val indicatorFrontComponent =
        rememberShapeComponent(fill(MaterialTheme.colorScheme.surface), CorneredShape.Pill)
    val guideline = rememberAxisGuidelineComponent()
    return rememberDefaultCartesianMarker(
        label = label,
        valueFormatter = valueFormatter,
        indicator =
        if (showIndicator) {
            { color ->
                LayeredComponent(
                    back = ShapeComponent(fill(color.copy(alpha = 0.15f)), CorneredShape.Pill),
                    front =
                    LayeredComponent(
                        back = ShapeComponent(fill = fill(color), shape = CorneredShape.Pill),
                        front = indicatorFrontComponent,
                        padding = insets(5.dp),
                    ),
                    padding = insets(16.dp),
                )
            }
        } else {
            null
        },
        indicatorSize = 36.dp,
        guideline = guideline,
    )
}