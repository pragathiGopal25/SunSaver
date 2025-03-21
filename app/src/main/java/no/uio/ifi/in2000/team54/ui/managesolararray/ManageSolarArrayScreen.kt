package no.uio.ifi.in2000.team54.ui.managesolararray


import android.util.Log
import androidx.compose.animation.core.tween
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.AnchoredDraggableState
import androidx.compose.foundation.gestures.DraggableAnchors
import androidx.compose.foundation.gestures.Orientation
import androidx.compose.foundation.gestures.anchoredDraggable
import androidx.compose.foundation.gestures.animateTo
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.absoluteOffset
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonColors
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.TextFieldColors
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.focus.onFocusChanged
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Popup
import com.mapbox.geojson.Point
import com.mapbox.maps.CameraOptions
import com.mapbox.maps.Style
import com.mapbox.maps.extension.compose.MapboxMap
import com.mapbox.maps.extension.compose.animation.viewport.MapViewportState
import com.mapbox.maps.extension.compose.animation.viewport.rememberMapViewportState
import com.mapbox.maps.extension.compose.style.MapStyle
import com.mapbox.search.autocomplete.PlaceAutocomplete
import com.mapbox.search.autocomplete.PlaceAutocompleteSuggestion
import kotlinx.coroutines.launch
import kotlin.math.roundToInt

private val placeAutocomplete = PlaceAutocomplete.create()

enum class ArraySettingsMenuAnchors { Bottom, Center, Top }

@Composable
fun ManageSolarArrayScreen() {
    val mapState = rememberMapViewportState {
        setCameraOptions {
            center(Point.fromLngLat( 10.7522, 59.9139))
            zoom(10.0)
        }
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
    ) {
        Map(mapState)
        Box(
            modifier = Modifier
                .fillMaxSize()
        ) {
            ArraySettingsMenu(mapState)
        }
    }

}

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun ArraySettingsMenu(mapState: MapViewportState) {
    val screenSizeDp = LocalConfiguration.current.screenHeightDp.dp + 20.dp
    val screenSizePx = with(LocalDensity.current) { screenSizeDp.toPx() }
    val anchors = DraggableAnchors {
        ArraySettingsMenuAnchors.Bottom at screenSizePx - 500f
        //ArraySettingsMenuAnchors.Center at screenSizePx / 2
        ArraySettingsMenuAnchors.Top at 250f
    }
    val draggableState = remember {
        AnchoredDraggableState(
            initialValue = ArraySettingsMenuAnchors.Bottom,
            anchors = anchors,
            positionalThreshold = { it },
            velocityThreshold = { 0f },
            animationSpec = tween(durationMillis = 100),
        )
    }

    Column(
        modifier = Modifier
    ) {
        Box(
            modifier = Modifier
                .offset {
                    IntOffset(
                        x = 0,
                        y = draggableState
                            .requireOffset()
                            .roundToInt()
                    )
                }
                .size(screenSizeDp)
                .clip(shape = RoundedCornerShape(25.dp, 25.dp, 0.dp, 0.dp))
                .background(MaterialTheme.colorScheme.primary)
                .anchoredDraggable(draggableState, Orientation.Vertical)
        ) {
            Column(
                verticalArrangement = Arrangement.SpaceBetween,
                horizontalAlignment = Alignment.CenterHorizontally,
                modifier = Modifier
                    .fillMaxSize()
                    .padding(10.dp)
            ) {
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Box(modifier = Modifier
                        .padding(bottom = 8.dp)
                        .width(50.dp)
                        .height(5.dp)
                        .clip(shape = RoundedCornerShape(100.dp))
                        .background(Color.Gray)
                    )
                    SearchField(mapState, draggableState)
                }
                Button(
                    onClick = {},
                    colors = ButtonDefaults.buttonColors(
                        containerColor = MaterialTheme.colorScheme.secondary
                    ),
                    modifier = Modifier
                        .padding(horizontal = 40.dp)
                        .padding(bottom = 100.dp)
                        .fillMaxWidth()

                ) {
                    Text("Lagre")
                }
            }
        }
    }
}

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun SearchField(mapState: MapViewportState, draggableState: AnchoredDraggableState<ArraySettingsMenuAnchors>) {
    val focusManager = LocalFocusManager.current
    val keyboardController = LocalSoftwareKeyboardController.current

    val scope = rememberCoroutineScope()
    var address by remember { mutableStateOf("") }
    val suggestions = remember { mutableStateOf<List<PlaceAutocompleteSuggestion>>(listOf()) }
    var showSuggestions by remember { mutableStateOf(false) }

    val selectSuggestion: (PlaceAutocompleteSuggestion) -> Unit = { suggestion ->
        address = suggestion.formattedAddress ?: address

        scope.launch {
            draggableState.animateTo(ArraySettingsMenuAnchors.Bottom, 0F)

            val selectionResponse = placeAutocomplete.select(suggestion)
            selectionResponse.onValue { result ->
                mapState.easeTo(
                    CameraOptions.Builder()
                        .center(result.coordinate)
                        .zoom(18.0)
                        .build()
                )
            }.onError { e ->
                Log.i("Address search", "An error occurred during selection", e)
            }
        }
    }

    Column {
        TextField(
            value = address,
            onValueChange = {
                address = it

                scope.launch {
                    val response = placeAutocomplete.suggestions(
                        query = it,
                    )

                    if (!response.isValue) {
                        // no results found
                        return@launch
                    }

                    suggestions.value = requireNotNull(response.value)
                }
            },
            label = { Text("Søk adresse") },
            keyboardOptions = KeyboardOptions(imeAction = ImeAction.Done),
            keyboardActions = KeyboardActions(onDone = {
                keyboardController?.hide()
                focusManager.clearFocus()

                val suggestion = suggestions.value.firstOrNull()
                if (suggestion == null) {
                    return@KeyboardActions
                }

                selectSuggestion(suggestion)
            }),
            colors = TextFieldDefaults.colors(
                unfocusedContainerColor = MaterialTheme.colorScheme.secondary,
                focusedContainerColor = MaterialTheme.colorScheme.secondary
            ),
            modifier = Modifier
                .fillMaxWidth()
                .height(50.dp)
                .clip(shape = RoundedCornerShape(100.dp))
                .onFocusChanged {
                    showSuggestions = it.isFocused

                    if (it.isFocused) {
                        scope.launch {
                            draggableState.animateTo(ArraySettingsMenuAnchors.Top, 0F)
                        }
                    }
                }
        )

        if (showSuggestions) {
            Popup(
                Alignment.TopStart,
                onDismissRequest = { showSuggestions = false }
            ) {
                Column(
                    modifier = Modifier
                        .padding(top = 60.dp)
                ) {
                    suggestions.value.forEach { suggestion ->
                        if (suggestion.formattedAddress == null) {
                            return@forEach
                        }

                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .background(Color.White)
                                .padding(10.dp)
                                .clickable {
                                    keyboardController?.hide()
                                    focusManager.clearFocus()
                                    selectSuggestion(suggestion)
                                },
                        ) {
                            Text(
                                text = suggestion.formattedAddress!!
                            )
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun Map(mapState: MapViewportState) {
    MapboxMap(
        Modifier
            .fillMaxSize(),
        mapViewportState = mapState,
        style = {
            MapStyle(style = Style.STANDARD)
        },
    ) {}
}