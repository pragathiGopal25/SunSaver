package no.uio.ifi.in2000.team54.ui.managesolararray


import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.mapbox.geojson.Point
import com.mapbox.maps.Style
import com.mapbox.maps.extension.compose.MapboxMap
import com.mapbox.maps.extension.compose.animation.viewport.rememberMapViewportState
import com.mapbox.maps.extension.compose.annotation.generated.PolygonAnnotation
import com.mapbox.maps.extension.compose.style.MapStyle


val ZOOM: Double = 18.0 // Perfekt zoom
    val CAMERA_CENTER: Point = Point.fromLngLat(11.362084, 59.506741)
    val POLYGON_POINTS = listOf(
        listOf(
            Point.fromLngLat(-89.857177734375, 24.51713945052515),
            Point.fromLngLat(-87.967529296875, 24.51713945052515),
            Point.fromLngLat(-87.967529296875, 26.244156283890756),
            Point.fromLngLat(-89.857177734375, 26.244156283890756),
            Point.fromLngLat(-89.857177734375, 24.51713945052515)
        )
    )

@Composable
fun ManageSolarArrayScreen() {
    Box (
        Modifier.fillMaxSize()
    ){

        
        MapboxMap(
            Modifier
                .padding(),
            mapViewportState = rememberMapViewportState {
                setCameraOptions {
                    zoom(ZOOM)
                    CAMERA_CENTER
                    center(CAMERA_CENTER)
                }
            },
            style = {
                MapStyle(style = Style.STANDARD)
            },
        ) {


            PolygonAnnotation(
                points = POLYGON_POINTS,
            ) {
                interactionsState.isDraggable = true
                interactionsState.onClicked {

                    interactionsState.isDraggable = false
                    true
                }
                    .onLongClicked {

                        interactionsState.isDraggable = true
                        true
                    }
                fillColor = Color.Red
            }
        }

        TextField(
            value = "",
            onValueChange = { },
            label = { Text("Label") },
            modifier = Modifier.fillMaxWidth().height(60.dp)
        )

    }

}


