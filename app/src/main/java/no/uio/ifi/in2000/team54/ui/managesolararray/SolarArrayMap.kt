package no.uio.ifi.in2000.team54.ui.managesolararray

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.snapshots.SnapshotStateList
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.mapbox.geojson.Polygon
import com.mapbox.maps.Style
import com.mapbox.maps.extension.compose.MapState
import com.mapbox.maps.extension.compose.MapboxMap
import com.mapbox.maps.extension.compose.animation.viewport.MapViewportState
import com.mapbox.maps.extension.compose.annotation.ViewAnnotation
import com.mapbox.maps.extension.compose.annotation.generated.PolygonAnnotation
import com.mapbox.maps.extension.compose.annotation.generated.PolylineAnnotation
import com.mapbox.maps.extension.compose.style.MapStyle
import com.mapbox.maps.viewannotation.geometry
import com.mapbox.maps.viewannotation.viewAnnotationOptions
import kotlinx.coroutines.launch
import no.uio.ifi.in2000.team54.domain.RoofSection
import no.uio.ifi.in2000.team54.enums.SolarPanelType
import no.uio.ifi.in2000.team54.model.building.Pos
import no.uio.ifi.in2000.team54.ui.theme.DarkYellow
import no.uio.ifi.in2000.team54.ui.theme.Light
import no.uio.ifi.in2000.team54.ui.theme.LightYellow

@Composable
fun SolarArrayMap(
    mapState: MapState,
    mapViewportState: MapViewportState,
    snackbarState: SnackbarHostState,
    viewModel: ManageSolarArrayViewModel,
    solarPanelType: SolarPanelType,
    roofSections: SnapshotStateList<RoofSection>
) {
    val mapRoofSectionsState by viewModel.mapRoofSections.collectAsState()
    val coroutineScope = rememberCoroutineScope()


    MapboxMap(
        Modifier
            .fillMaxSize(),
        mapState = mapState,
        mapViewportState = mapViewportState,
        style = {
            MapStyle(style = Style.STANDARD)
        },
        scaleBar = {},
        onMapClickListener = { point ->
            val targetRoofSection = mapRoofSectionsState.roofSections.find {
                it.geometry.contains(point)
            }
            // if there aren't any roof sections at this position, we wan't to try to find a new address
            if (targetRoofSection == null && viewModel.currentSolarArray.value == null) {
                viewModel.queryAddressAtPos(Pos.fromPoint(point))
            } else if (targetRoofSection != null){
                if (!roofSections.removeIf { it.mapId == targetRoofSection!!.id }) {
                    val area = targetRoofSection!!.width * targetRoofSection.length

                    roofSections.add(
                        RoofSection(
                            id = null,
                            area,
                            targetRoofSection.incline,
                            targetRoofSection.direction,
                            (area / solarPanelType.area()).toInt(),
                            targetRoofSection.id
                        )
                    )
                }
            } else {
                coroutineScope.launch {// makes sure that user cannot move to address by clicking on the map when editing
                    snackbarState.showSnackbar("Du kan ikke endre adressen når du redigerer et solcelleanlegg.")
                }
            }
            false
        }
    ) {
        LaunchedEffect(key1 = mapRoofSectionsState) {
            if (mapRoofSectionsState.isError) {
                snackbarState.showSnackbar("Vi fant ikke noe data om takflater på denne adressen.")
            }
        }

        mapRoofSectionsState.roofSections.forEach { roofSection ->
            val points = roofSection.geometry.toPoints()
            val localRoofSection = roofSections.find { it.mapId == roofSection.id }

            PolygonAnnotation(listOf(points)) {
                fillColor = if (localRoofSection == null) LightYellow else DarkYellow
                fillOpacity = 0.9
            }
            PolylineAnnotation(points) {
                lineColor = Color.Black
                lineWidth = 3.0
            }

            if (localRoofSection != null) {
                ViewAnnotation(
                    options = viewAnnotationOptions {
                        geometry(Polygon.fromLngLats(listOf(points)))
                        allowOverlap(true)
                        allowOverlapWithPuck(true)
                    }
                ) {
                    Text(
                        "Tak ${roofSections.indexOf(localRoofSection) + 1}",
                        modifier =
                        Modifier
                            .clip(RoundedCornerShape(10.dp))
                            .background(Light)
                            .padding(horizontal = 3.dp, vertical = 2.dp)
                    )
                }
            }
        }
    }
}