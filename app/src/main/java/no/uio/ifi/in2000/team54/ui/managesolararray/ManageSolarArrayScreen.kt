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
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExposedDropdownMenuBox
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
import androidx.compose.runtime.snapshots.SnapshotStateList
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.focus.onFocusChanged
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.IntOffset
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
import no.uio.ifi.in2000.team54.ui.composables.CustomTextField
import no.uio.ifi.in2000.team54.ui.state.RoofState
import kotlin.math.roundToInt

private val placeAutocomplete = PlaceAutocomplete.create()

enum class ArraySettingsMenuAnchors { Bottom, Top }

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
private fun Map(mapState: MapViewportState) {
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
private fun ArraySettingsMenu(mapState: MapViewportState) {
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

    val roofs = remember { mutableStateListOf<RoofState>() }

    Column {
        DraggableBox(
            screenSizeDp = screenSizeDp,
            draggableState = draggableState
        ) {
            ArraySettingsContent(
                mapState,
                draggableState,
                roofs
            )
        }
    }
}

@OptIn(ExperimentalFoundationApi::class)
@Composable
private fun DraggableBox(
    screenSizeDp: Dp,
    draggableState: AnchoredDraggableState<ArraySettingsMenuAnchors>,
    content: @Composable () -> Unit
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
        content()
    }
}

@OptIn(ExperimentalFoundationApi::class)
@Composable
private fun ArraySettingsContent(
    mapState: MapViewportState,
    draggableState: AnchoredDraggableState<ArraySettingsMenuAnchors>,
    roofs: SnapshotStateList<RoofState>
) {
    Column(
        verticalArrangement = Arrangement.SpaceBetween,
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = Modifier
            .fillMaxSize()
            .padding(10.dp)
    ) {
        ArraySettingsMainSection(
            mapState = mapState,
            draggableState = draggableState,
            roofs = roofs
        )
        SaveButton()
    }
}

@OptIn(ExperimentalFoundationApi::class)
@Composable
private fun ArraySettingsMainSection(
    mapState: MapViewportState,
    draggableState: AnchoredDraggableState<ArraySettingsMenuAnchors>,
    roofs: SnapshotStateList<RoofState>
) {
    Column(
        verticalArrangement = Arrangement.spacedBy(5.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        DragHandle()
        SearchField(mapState, draggableState)
        Spacer(modifier = Modifier.size(10.dp))
        RoofList(roofs)
        AddRoofComponent(
            onAdd = {
                roofs.add(it)
            }
        )
    }
}

@Composable
private fun DragHandle() {
    Box(
        modifier = Modifier
            .padding(bottom = 8.dp)
            .width(50.dp)
            .height(5.dp)
            .clip(shape = RoundedCornerShape(100.dp))
            .background(Color.Gray)
    )
}

@Composable
private fun RoofList(roofs: SnapshotStateList<RoofState>) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .horizontalScroll(rememberScrollState())
    ) {
        Row(
            horizontalArrangement = Arrangement.spacedBy(5.dp)
        ) {
            roofs.forEachIndexed { index, roof ->
                RoofItem(index = index, onRemove = { roofs.remove(roof) })
            }
        }
    }
}

@Composable
private fun RoofItem(index: Int, onRemove: () -> Unit) {
    Box(
        modifier = Modifier
            .width(100.dp)
            .height(100.dp)
            .clip(shape = RoundedCornerShape(15.dp))
            .background(Color.White)
            .padding(10.dp)
            .clickable { onRemove() }
    ) {
        Text("Tak #${index + 1}")
    }
}

@Composable
private fun SaveButton() {
    Button(
        onClick = { },
        colors = ButtonDefaults.buttonColors(
            containerColor = MaterialTheme.colorScheme.secondary
        ),
        modifier = Modifier
            .width(200.dp)
            .padding(bottom = 100.dp, top = 20.dp)
            .fillMaxWidth()
    ) {
        Text("Lagre")
    }
}

@OptIn(ExperimentalFoundationApi::class)
@Composable
private fun SearchField(
    mapState: MapViewportState,
    draggableState: AnchoredDraggableState<ArraySettingsMenuAnchors>
) {
    val focusManager = LocalFocusManager.current
    val keyboardController = LocalSoftwareKeyboardController.current
    val scope = rememberCoroutineScope()

    var address by remember { mutableStateOf("") }
    val suggestions = remember { mutableStateOf<List<PlaceAutocompleteSuggestion>>(listOf()) }
    var showSuggestions by remember { mutableStateOf(false) }

    val selectSuggestion: (PlaceAutocompleteSuggestion) -> Unit = remember {
        { suggestion ->
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
    }

    Column {
        SearchTextField(
            address = address,
            onAddressChange = { query ->
                address = query
                scope.launch {
                    val response = placeAutocomplete.suggestions(query)
                    if (response.isValue) {
                        suggestions.value = requireNotNull(response.value)
                    }
                }
            },
            onDone = {
                keyboardController?.hide()
                focusManager.clearFocus()
                suggestions.value.firstOrNull()?.let { selectSuggestion(it) }
            },
            onFocusChanged = { isFocused ->
                showSuggestions = isFocused
                if (isFocused) {
                    scope.launch {
                        draggableState.animateTo(ArraySettingsMenuAnchors.Top, 0F)
                    }
                }
            }
        )

        if (showSuggestions) {
            SuggestionsPopup(
                suggestions = suggestions.value,
                onSuggestionClick = { suggestion ->
                    keyboardController?.hide()
                    focusManager.clearFocus()
                    selectSuggestion(suggestion)
                },
                onDismissRequest = { showSuggestions = false }
            )
        }
    }
}

@Composable
private fun SearchTextField(
    address: String,
    onAddressChange: (String) -> Unit,
    onDone: () -> Unit,
    onFocusChanged: (Boolean) -> Unit
) {
    TextField(
        value = address,
        onValueChange = onAddressChange,
        label = { Text("Søk adresse") },
        keyboardOptions = KeyboardOptions(imeAction = ImeAction.Done),
        keyboardActions = KeyboardActions(onDone = { onDone() }),
        colors = TextFieldDefaults.colors(
            unfocusedContainerColor = MaterialTheme.colorScheme.secondary,
            focusedContainerColor = MaterialTheme.colorScheme.secondary
        ),
        modifier = Modifier
            .fillMaxWidth()
            .height(50.dp)
            .clip(shape = RoundedCornerShape(100.dp))
            .onFocusChanged { onFocusChanged(it.isFocused) }
    )
}

@Composable
private fun SuggestionsPopup(
    suggestions: List<PlaceAutocompleteSuggestion>,
    onSuggestionClick: (PlaceAutocompleteSuggestion) -> Unit,
    onDismissRequest: () -> Unit
) {
    Popup(
        alignment = Alignment.TopStart,
        onDismissRequest = onDismissRequest
    ) {
        Column(
            modifier = Modifier.padding(top = 60.dp)
        ) {
            suggestions.forEach { suggestion ->
                if (suggestion.formattedAddress != null) {
                    SuggestionItem(
                        suggestion = suggestion,
                        onClick = { onSuggestionClick(suggestion) }
                    )
                }
            }
        }
    }
}

@Composable
private fun SuggestionItem(suggestion: PlaceAutocompleteSuggestion, onClick: () -> Unit) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .background(Color.White)
            .padding(10.dp)
            .clickable { onClick() }
    ) {
        Text(text = suggestion.formattedAddress ?: "")
    }
}

@Composable
private fun AddRoofComponent(onAdd: (RoofState) -> Unit) {
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
        HeaderText("Takflate")

        Spacer(modifier = Modifier.size(10.dp))

        Row(
            horizontalArrangement = Arrangement.spacedBy(10.dp),
            modifier = Modifier.fillMaxWidth()
        ) {
            NumberInputField(
                value = area,
                onValueChange = { area = it },
                label = "Areal (m²)",
                placeholder = "Areal i kvadratmeter",
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1f)
            )
            NumberInputField(
                value = angle,
                onValueChange = { angle = it },
                label = "Helning (grader°)",
                placeholder = "Helning i grader",
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1f)
            )
        }

        Row(
            horizontalArrangement = Arrangement.spacedBy(10.dp),
            modifier = Modifier.fillMaxWidth()
        ) {
            NumberInputField(
                value = direction,
                onValueChange = { direction = it },
                label = "Retning (grader°)",
                placeholder = "Retning i grader",
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1f)
            )
            SolarPanelTypeDropdown(
                selectedType = solarPanelType,
                onSelect = { solarPanelType = it },
                modifier = Modifier
                    .weight(1f),
            )
        }

        Spacer(modifier = Modifier.size(10.dp))

        AddRoofButton {
            onAdd(
                RoofState(
                    area.toDouble(),
                    angle.toDouble(),
                    direction.toDouble(),
                    solarPanelType
                )
            )
        }
    }
}

@Composable
private fun HeaderText(text: String) {
    Text(
        text = text,
        fontSize = 20.sp,
        fontWeight = FontWeight.Bold
    )
}

@Composable
private fun AddRoofButton(onClick: () -> Unit) {
    val keyboardController = LocalSoftwareKeyboardController.current

    Button(
        onClick = {
            onClick()
            keyboardController?.hide()
        },
        colors = ButtonDefaults.buttonColors(
            containerColor = MaterialTheme.colorScheme.secondary
        ),
        modifier = Modifier.width(150.dp)
    ) {
        Text("Legg til")
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
private fun NumberInputField(
    value: String,
    onValueChange: (String) -> Unit,
    label: String,
    placeholder: String,
    modifier: Modifier = Modifier
) {
    CustomTextField(
        modifier = Modifier.fillMaxWidth(),
        containerModifier = modifier,
        value = value,
        onValueChange = { if (it.isDigitsOnly()) onValueChange(it) },
        label = label,
        placeholder = placeholder,
        keyboardOptions = KeyboardOptions(
            keyboardType = KeyboardType.Number,
            imeAction = ImeAction.Done
        )
    )
}