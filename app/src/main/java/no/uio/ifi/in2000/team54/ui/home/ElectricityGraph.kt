package no.uio.ifi.in2000.team54.ui.home

import android.text.Layout
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.core.graphics.ColorUtils
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.patrykandpatrick.vico.compose.cartesian.CartesianChartHost
import com.patrykandpatrick.vico.compose.cartesian.axis.rememberBottom
import com.patrykandpatrick.vico.compose.cartesian.axis.rememberStart
import com.patrykandpatrick.vico.compose.cartesian.layer.rememberLine
import com.patrykandpatrick.vico.compose.cartesian.layer.rememberLineCartesianLayer
import com.patrykandpatrick.vico.compose.cartesian.marker.rememberDefaultCartesianMarker
import com.patrykandpatrick.vico.compose.cartesian.rememberCartesianChart
import com.patrykandpatrick.vico.compose.cartesian.rememberVicoScrollState
import com.patrykandpatrick.vico.compose.common.component.fixed
import com.patrykandpatrick.vico.compose.common.component.rememberShapeComponent
import com.patrykandpatrick.vico.compose.common.component.rememberTextComponent
import com.patrykandpatrick.vico.compose.common.component.shapeComponent
import com.patrykandpatrick.vico.compose.common.fill
import com.patrykandpatrick.vico.compose.common.insets
import com.patrykandpatrick.vico.compose.common.rememberVerticalLegend
import com.patrykandpatrick.vico.compose.common.shape.markerCorneredShape
import com.patrykandpatrick.vico.compose.common.vicoTheme
import com.patrykandpatrick.vico.core.cartesian.axis.HorizontalAxis
import com.patrykandpatrick.vico.core.cartesian.axis.VerticalAxis
import com.patrykandpatrick.vico.core.cartesian.data.CartesianChartModelProducer
import com.patrykandpatrick.vico.core.cartesian.data.CartesianValueFormatter
import com.patrykandpatrick.vico.core.cartesian.data.lineSeries
import com.patrykandpatrick.vico.core.cartesian.layer.LineCartesianLayer
import com.patrykandpatrick.vico.core.cartesian.marker.CartesianMarker
import com.patrykandpatrick.vico.core.cartesian.marker.DefaultCartesianMarker
import com.patrykandpatrick.vico.core.common.Fill
import com.patrykandpatrick.vico.core.common.LayeredComponent
import com.patrykandpatrick.vico.core.common.LegendItem
import com.patrykandpatrick.vico.core.common.component.ShapeComponent
import com.patrykandpatrick.vico.core.common.component.TextComponent
import com.patrykandpatrick.vico.core.common.data.ExtraStore
import com.patrykandpatrick.vico.core.common.shader.ShaderProvider
import com.patrykandpatrick.vico.core.common.shape.CorneredShape
import no.uio.ifi.in2000.team54.ui.theme.BrightYellow
import no.uio.ifi.in2000.team54.ui.theme.DarkYellow

val monthFormatter =
    CartesianValueFormatter { _, value, _ -> // overriding "format" method in CastertianValueFormatter
        val months = listOf(
            "Jan", "Feb", "Mar", "Apr", "Mai", "Jun",
            "Jul", "Aug", "Sep", "Okt", "Nov", "Des"
        )
        val index = value.toInt().coerceIn(0, months.size - 1)  // Keep within bounds
        months[index]
    }

@Composable
fun GraphContainer(
    modifier: Modifier = Modifier,
    viewModel: HomeViewModel
) {
    val homeUiState by viewModel.homeUiState.collectAsStateWithLifecycle()
    val graphLoadingState by viewModel.graphLoadingState.collectAsStateWithLifecycle()
    if (graphLoadingState.loadingMessage != "") {
        Box(modifier.fillMaxSize(), Alignment.Center) {
            if (!graphLoadingState.isLoading) {
                Text(text = graphLoadingState.loadingMessage)
            } else {
                CircularProgressIndicator(
                    color = DarkYellow,
                    modifier = Modifier
                        .width(70.dp)
                )
            }
        }
    } else {
        ElectricityGraph(homeUiState = homeUiState)
    }
}

private val LegendLabelKey = ExtraStore.Key<Set<String>>()


@Composable
fun ElectricityGraph(
    modifier: Modifier = Modifier,
    homeUiState: HomeUiState
) {
    val modelProducer = remember { CartesianChartModelProducer() }

    Box(
        modifier = modifier,
        contentAlignment = Alignment.Center,
    ) {
        LaunchedEffect(homeUiState) {
            modelProducer.runTransaction {
                lineSeries {
                    homeUiState.electricityProductionData.forEach { (_, list) ->
                        series(
                            list
                        )
                    }
                }
                extras { extraStore ->
                    extraStore[LegendLabelKey] = homeUiState.electricityProductionData.keys
                }
            }
        }

        val lineColors = listOf(BrightYellow)
        val legendItemLabelComponent = rememberTextComponent(vicoTheme.textColor)

        CartesianChartHost(
            rememberCartesianChart(
                rememberLineCartesianLayer(
                    LineCartesianLayer.LineProvider.series(
                        lineColors.map { color ->
                            LineCartesianLayer.rememberLine(
                                fill = LineCartesianLayer.LineFill.double(fill(color), fill(color)),
                                areaFill = LineCartesianLayer.AreaFill.single(
                                    Fill(
                                        ShaderProvider.verticalGradient(
                                            ColorUtils.setAlphaComponent(0xFFF9B87D.toInt(), 102),
                                            android.graphics.Color.TRANSPARENT,
                                        )
                                    )
                                ),
                            )
                        })
                ),
                startAxis = VerticalAxis.rememberStart(
                    title = "Strøm produsert (kWh)",
                    titleComponent = TextComponent(),
                    guideline = null // gets rid of axis lines
                ),
                bottomAxis = HorizontalAxis.rememberBottom(
                    title = "Tid (Måneder)",
                    titleComponent = TextComponent(),
                    guideline = null,
                    valueFormatter = monthFormatter
                ),
                marker = rememberMarker(),
                legend = rememberVerticalLegend(
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
                    })
            ),
            modelProducer,
            modifier = modifier
                .padding(10.dp)
                .height(250.dp),
            scrollState = rememberVicoScrollState(scrollEnabled = false), // disabled so it fits the box

        )
    }
}

@Composable
fun rememberMarker(
    valueFormatter: DefaultCartesianMarker.ValueFormatter =
        DefaultCartesianMarker.ValueFormatter.default(colorCode = false), // allows us to set textcomponent color
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
            color = Color.Black,
            textAlignment = Layout.Alignment.ALIGN_CENTER,
            padding = insets(8.dp, 4.dp),
            background = labelBackground,
            minWidth = TextComponent.MinWidth.fixed(40.dp),
        )
    val indicatorFrontComponent =
        rememberShapeComponent(fill(MaterialTheme.colorScheme.surface), CorneredShape.Pill)
    //val guideline = rememberAxisGuidelineComponent()
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
        labelPosition = DefaultCartesianMarker.LabelPosition.AroundPoint
    )
}