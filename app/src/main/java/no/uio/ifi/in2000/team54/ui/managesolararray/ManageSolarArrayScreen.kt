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
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.LocalTextStyle
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.focus.onFocusChanged
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.TextUnit
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Popup
import androidx.core.text.isDigitsOnly
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
import no.uio.ifi.in2000.team54.enums.SolarPanelType
import kotlin.math.roundToInt

private val placeAutocomplete = PlaceAutocomplete.create()

enum class ArraySettingsMenuAnchors { Bottom, Top }

class Roof(
    val area: Double,
    val helning: Double,
    val retning: Double,
    val solarPanelType: SolarPanelType,
)

@Composable
fun ManageSolarArrayScreen() {
    val mapState = rememberMapViewportState {
        setCameraOptions {
            center(Point.fromLngLat(10.7522, 59.9139))
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

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun ArraySettingsMenu(mapState: MapViewportState) {
    val screenSizeDp = LocalConfiguration.current.screenHeightDp.dp + 20.dp
    val screenSizePx = with(LocalDensity.current) { screenSizeDp.toPx() }
    val anchors = DraggableAnchors {
        ArraySettingsMenuAnchors.Bottom at screenSizePx - 500f
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

    val roofs = remember { mutableStateListOf<Roof>() }

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
                    verticalArrangement = Arrangement.spacedBy(5.dp),
                    horizontalAlignment = Alignment.CenterHorizontally,
                ) {
                    Box(
                        modifier = Modifier
                            .padding(bottom = 8.dp)
                            .width(50.dp)
                            .height(5.dp)
                            .clip(shape = RoundedCornerShape(100.dp))
                            .background(Color.Gray)
                    )
                    SearchField(mapState, draggableState)
                    Spacer(
                        modifier = Modifier.size(10.dp)
                    )
                    Column(
                        verticalArrangement = Arrangement.spacedBy(5.dp)
                    ) {
                        roofs.forEachIndexed { index, roof ->
                            Box(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .clip(shape = RoundedCornerShape(15.dp))
                                    .background(Color.White)
                                    .padding(10.dp)
                                    .clickable {
                                        roofs.remove(roof)
                                    }
                            ) {
                                Text("Tak #${index + 1}")
                            }
                        }
                    }
                    AddRoofComponent(
                        onAdd = {
                            if (roofs.size >= 4) {
                                return@AddRoofComponent
                            }

                            roofs.add(it)
                        }
                    )
                }
                Button(
                    onClick = {},
                    colors = ButtonDefaults.buttonColors(
                        containerColor = MaterialTheme.colorScheme.secondary
                    ),
                    modifier = Modifier
                        .width(200.dp)
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
fun AddRoofComponent(onAdd: (Roof) -> Unit) {
    var area by remember { mutableStateOf("") }
    var angle by remember { mutableStateOf("") }
    var direction by remember { mutableStateOf("") }
    var solarPanelType by remember { mutableStateOf(SolarPanelType.entries[0]) }

    Column(
        modifier = Modifier
            .clip(shape = RoundedCornerShape(15.dp))
            .background(Color.White)
            .padding(horizontal = 15.dp, vertical = 10.dp)
    ) {
        Text(
            "Takflate",
            fontSize = 20.sp,
            fontWeight = FontWeight.Bold
        )
        Spacer(
            modifier = Modifier.size(10.dp)
        )
        Row(
            horizontalArrangement = Arrangement.spacedBy(10.dp),
            modifier = Modifier
                .fillMaxWidth()
        ) {
            NumberField(
                modifier = Modifier
                    .fillMaxWidth(),
                containerModifier = Modifier
                    .fillMaxWidth()
                    .weight(1f),
                value = area,
                onValueChange = { area = it },
                label = "Areal (m²)",
                placeholderText = "Areal i kvadratmeter"
            )
            NumberField(
                modifier = Modifier
                    .fillMaxWidth(),
                containerModifier = Modifier
                    .fillMaxWidth()
                    .weight(1f),
                value = angle,
                onValueChange = { angle = it },
                label = "Helning (grader°)",
                placeholderText = "Helning i grader"
            )
        }
        Row(
            horizontalArrangement = Arrangement.spacedBy(10.dp),
            modifier = Modifier
                .fillMaxWidth()
        ) {
            NumberField(
                modifier = Modifier
                    .fillMaxWidth(),
                containerModifier = Modifier
                    .fillMaxWidth()
                    .weight(1f),
                value = direction,
                onValueChange = { direction = it },
                label = "Retning (grader°)",
                placeholderText = "Retning i grader"
            )
            SolarPanelTypeDropdown(
                modifier = Modifier
                    .weight(1f),
                selectedType = solarPanelType,
                onSelect = { solarPanelType = it }
            )
        }
        Spacer(
            modifier = Modifier.size(10.dp)
        )
        Button(
            onClick = {
                onAdd(
                    Roof(
                        area.toDouble(),
                        angle.toDouble(),
                        direction.toDouble(),
                        solarPanelType
                    )
                )
            },
            colors = ButtonDefaults.buttonColors(
                containerColor = MaterialTheme.colorScheme.secondary
            ),
            modifier = Modifier
                .width(150.dp)

        ) {
            Text("Legg til")
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun SolarPanelTypeDropdown(
    modifier: Modifier = Modifier,
    selectedType: SolarPanelType,
    onSelect: (SolarPanelType) -> Unit
) {
    var dropdownExpanded by remember { mutableStateOf(false) }

    Column(
        modifier = modifier
            .fillMaxWidth()
    ) {
        Text("Solcellepaneltype")
        ExposedDropdownMenuBox(
            expanded = dropdownExpanded,
            onExpandedChange = { dropdownExpanded = it }
        ) {
            BasicTextField(
                value = selectedType.nameWithWatt(),
                readOnly = true,
                onValueChange = {},
                modifier = Modifier
                    .menuAnchor()
                    .background(
                        MaterialTheme.colorScheme.primary,
                        MaterialTheme.shapes.small,
                    )
                    .fillMaxWidth()
                    .padding(10.dp)
            )
            ExposedDropdownMenu(
                expanded = dropdownExpanded,
                onDismissRequest = { dropdownExpanded = false }
            ) {
                SolarPanelType.entries.forEach { type ->
                    DropdownMenuItem(
                        text = { Text(type.nameWithWatt()) },
                        onClick = {
                            dropdownExpanded = false
                            onSelect(type)
                        }
                    )
                }
            }
        }
    }
}

@Composable
private fun NumberField(
    modifier: Modifier = Modifier,
    containerModifier: Modifier = Modifier,
    value: String,
    onValueChange: (String) -> Unit,
    label: String = "Label",
    leadingIcon: (@Composable () -> Unit)? = null,
    placeholderText: String = "",
    fontSize: TextUnit = MaterialTheme.typography.bodyMedium.fontSize,
) {
    val keyboardController = LocalSoftwareKeyboardController.current

    Column(
        modifier = containerModifier
    ) {
        Text(label)
        BasicTextField(
            modifier = modifier
                .background(
                    MaterialTheme.colorScheme.primary,
                    MaterialTheme.shapes.small,
                )
                .padding(10.dp),
            value = value,
            onValueChange = { if (it.isDigitsOnly()) onValueChange(it) },
            singleLine = true,
            cursorBrush = SolidColor(MaterialTheme.colorScheme.secondary),
            textStyle = LocalTextStyle.current.copy(
                color = MaterialTheme.colorScheme.onSurface,
                fontSize = fontSize
            ),
            keyboardOptions = KeyboardOptions(
                keyboardType = KeyboardType.Number,
                imeAction = ImeAction.Done
            ),
            keyboardActions = KeyboardActions(
                onDone = {
                    keyboardController?.hide()
                }
            ),
            decorationBox = { innerTextField ->
                Row(
                    modifier,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    if (leadingIcon != null) leadingIcon()

                    Box() {
                        if (value.isEmpty()) {
                            Text(
                                text = placeholderText,
                                style = LocalTextStyle.current.copy(
                                    color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.3f),
                                    fontSize = fontSize
                                )
                            )
                        }
                        innerTextField()
                    }
                }
            }
        )
    }
}