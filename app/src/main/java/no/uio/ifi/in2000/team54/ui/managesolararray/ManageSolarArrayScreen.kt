package no.uio.ifi.in2000.team54.ui.managesolararray


import androidx.compose.animation.core.tween
import androidx.compose.animation.rememberSplineBasedDecay
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
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
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.defaultMinSize
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.rounded.Delete
import androidx.compose.material.icons.rounded.Search
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.Icon
import androidx.compose.material3.LocalTextStyle
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.MenuAnchorType
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
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
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.Popup
import androidx.core.text.isDigitsOnly
import com.mapbox.geojson.Point
import com.mapbox.geojson.Polygon
import com.mapbox.maps.CameraOptions
import com.mapbox.maps.Style
import com.mapbox.maps.extension.compose.MapState
import com.mapbox.maps.extension.compose.MapboxMap
import com.mapbox.maps.extension.compose.animation.viewport.MapViewportState
import com.mapbox.maps.extension.compose.animation.viewport.rememberMapViewportState
import com.mapbox.maps.extension.compose.annotation.ViewAnnotation
import com.mapbox.maps.extension.compose.annotation.generated.PolygonAnnotation
import com.mapbox.maps.extension.compose.annotation.generated.PolylineAnnotation
import com.mapbox.maps.extension.compose.rememberMapState
import com.mapbox.maps.extension.compose.style.MapStyle
import com.mapbox.maps.viewannotation.geometry
import com.mapbox.maps.viewannotation.viewAnnotationOptions
import kotlinx.coroutines.launch
import no.uio.ifi.in2000.team54.R
import no.uio.ifi.in2000.team54.enums.SolarPanelType
import no.uio.ifi.in2000.team54.model.building.Address
import no.uio.ifi.in2000.team54.ui.composables.CustomTextField
import no.uio.ifi.in2000.team54.ui.state.RoofSection
import no.uio.ifi.in2000.team54.ui.state.SolarArray
import no.uio.ifi.in2000.team54.ui.theme.BrightYellow
import no.uio.ifi.in2000.team54.ui.theme.DarkBeige
import no.uio.ifi.in2000.team54.ui.theme.DarkYellow
import no.uio.ifi.in2000.team54.ui.theme.Light
import no.uio.ifi.in2000.team54.ui.theme.LightYellow
import no.uio.ifi.in2000.team54.ui.theme.LightestYellow
import no.uio.ifi.in2000.team54.ui.theme.RandomBeige
import no.uio.ifi.in2000.team54.ui.theme.Red
import no.uio.ifi.in2000.team54.util.calculateSubsidy
import java.util.Locale
import kotlin.math.roundToInt

private val osloCenter = Point.fromLngLat(10.7522, 59.9139)

enum class ArraySettingsMenuAnchors { Bottom, Top }

@Composable
fun ManageSolarArrayScreen(viewModel: ManageSolarArrayViewModel) {
    val roofSections = remember { mutableStateListOf<RoofSection>() }

    val mapViewportState = rememberMapViewportState {
        setCameraOptions {
            center(osloCenter)
            zoom(10.0)
        }
    }
    val mapState = rememberMapState {}
    Box(
        modifier = Modifier
            .fillMaxSize()
    ) {
        Map(mapState, mapViewportState, viewModel, roofSections)
        BackButton()
        Box(
            modifier = Modifier
                .fillMaxSize()
        ) {
            ArraySettingsMenu(mapState, mapViewportState, viewModel, roofSections)
        }
    }
}

@Composable
private fun Map(
    mapState: MapState,
    mapViewportState: MapViewportState,
    viewModel: ManageSolarArrayViewModel,
    roofSections: SnapshotStateList<RoofSection>
) {
    val mapRoofSectionsState by viewModel.mapRoofSections.collectAsState()

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

            if (targetRoofSection != null) {
                if (!roofSections.removeIf { it.mapId == targetRoofSection.id }) {
                    val area = targetRoofSection.width * targetRoofSection.length

                    roofSections.add(
                        RoofSection(
                            area,
                            targetRoofSection.incline,
                            targetRoofSection.direction,
                            (area / SolarPanelType.AREA).toInt(),
                            targetRoofSection.id
                        )
                    )
                }
            }

            //viewModel.setPos(Pos.fromPoint(point))
            false
        }
    ) {
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
                        //geometry(Point.fromLngLat(roofSection.longitude, roofSection.latitude))
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

@Composable
private fun BackButton() {
    Icon(
        Icons.AutoMirrored.Default.ArrowBack,
        contentDescription = "Gå tilbake",
        tint = DarkYellow,
        modifier = Modifier
            .padding(10.dp)
            .width(35.dp)
            .height(35.dp)
            .clip(RoundedCornerShape(100.dp))
            .background(LightestYellow)
            .padding(7.dp)
            .clickable {
                // TODO navigate home
            }
    )
}

@OptIn(ExperimentalFoundationApi::class)
@Composable
private fun ArraySettingsMenu(
    mapState: MapState,
    mapViewportState: MapViewportState,
    viewModel: ManageSolarArrayViewModel,
    roofSections: SnapshotStateList<RoofSection>
) {
    val screenSizeDp = LocalConfiguration.current.screenHeightDp.dp + 20.dp
    val screenSizePx = with(LocalDensity.current) { screenSizeDp.toPx() }

    val anchors = DraggableAnchors {
        ArraySettingsMenuAnchors.Bottom at screenSizePx - 750f
        ArraySettingsMenuAnchors.Top at 150f
    }

    val decayAnimation = rememberSplineBasedDecay<Float>()
    val draggableState = remember {
        /*AnchoredDraggableState(
            initialValue = ArraySettingsMenuAnchors.Bottom,
            anchors = anchors,
            positionalThreshold = { it },
            velocityThreshold = { 0f },
            animationSpec = tween(durationMillis = 100),
        )*/
        AnchoredDraggableState(
            initialValue = ArraySettingsMenuAnchors.Top,
            anchors = anchors,
            positionalThreshold = { it },
            velocityThreshold = { 0f },
            snapAnimationSpec = tween(durationMillis = 100),
            decayAnimationSpec = decayAnimation,
        )
    }

    Column {
        DraggableBox(
            screenSizeDp = screenSizeDp,
            draggableState = draggableState
        ) {
            ArraySettingsContent(
                mapState,
                mapViewportState,
                draggableState,
                viewModel,
                roofSections
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
            .background(LightestYellow)
            .anchoredDraggable(draggableState, Orientation.Vertical)
    ) {
        content()
    }
}

@OptIn(ExperimentalFoundationApi::class)
@Composable
private fun ArraySettingsContent(
    mapState: MapState,
    mapViewportState: MapViewportState,
    draggableState: AnchoredDraggableState<ArraySettingsMenuAnchors>,
    viewModel: ManageSolarArrayViewModel,
    roofSections: SnapshotStateList<RoofSection>
) {
    var solarPanelType by remember { mutableStateOf(SolarPanelType.PREMIUM) }

    Column(
        verticalArrangement = Arrangement.SpaceBetween,
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = Modifier
            .fillMaxSize()
            .padding(10.dp)
    ) {
        ArraySettingsMainSection(
            mapState,
            mapViewportState,
            draggableState,
            viewModel,
            solarPanelType,
            roofSections,
            { solarPanelType = it }
        )
        SaveButton(solarPanelType, roofSections)
    }
}

@OptIn(ExperimentalFoundationApi::class)
@Composable
private fun ArraySettingsMainSection(
    mapState: MapState,
    mapViewportState: MapViewportState,
    draggableState: AnchoredDraggableState<ArraySettingsMenuAnchors>,
    viewModel: ManageSolarArrayViewModel,
    solarPanelType: SolarPanelType,
    roofSections: SnapshotStateList<RoofSection>,
    onSelectPanelType: (SolarPanelType) -> Unit
) {
    Column(
        verticalArrangement = Arrangement.spacedBy(6.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        DragHandle()
        SearchField(mapState, mapViewportState, draggableState, viewModel)
        Spacer(modifier = Modifier.size(10.dp))
        Column(
            verticalArrangement = Arrangement.spacedBy(15.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
        ) {
            RoofSectionsList(roofSections)
            AddRoofSectionCard(
                onAdd = {
                    roofSections.add(it)
                }
            )
            SolarPanelTypeDropdown(solarPanelType, onSelectPanelType)
            PriceSummaryCard(viewModel, solarPanelType, roofSections)
        }
    }
}

@Composable
private fun DragHandle() {
    Box(
        modifier = Modifier
            .padding(bottom = 8.dp)
            .width(85.dp)
            .height(7.dp)
            .clip(shape = RoundedCornerShape(100.dp))
            .background(BrightYellow)
    )
}

@Composable
private fun RoofSectionsList(roofSections: SnapshotStateList<RoofSection>) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .horizontalScroll(rememberScrollState())
    ) {
        Row(
            horizontalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            roofSections.forEachIndexed { index, section ->
                RoofSectionCard(section, index, { roofSections.remove(section) })
            }
        }
    }
}

@Composable
private fun RoofSectionCard(section: RoofSection, index: Int, onRemove: () -> Unit) {
    Box(
        modifier = Modifier
            .width(180.dp)
            .clip(RoundedCornerShape(15.dp))
            .background(Light)
            .border(1.dp, DarkYellow, RoundedCornerShape(15.dp))
            .padding(10.dp)
    ) {
        Column(
            verticalArrangement = Arrangement.spacedBy(10.dp),
            modifier = Modifier
                .fillMaxWidth()
        ) {
            Row(
                horizontalArrangement = Arrangement.SpaceBetween,
                modifier = Modifier
                    .fillMaxWidth()
            ) {
                Text(
                    text = "Takflate ${index + 1}",
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Bold,
                    color = DarkYellow
                )
                Icon(
                    Icons.Rounded.Delete,
                    contentDescription = "Slett takflate",
                    tint = Red,
                    modifier = Modifier
                        .size(20.dp)
                        .clickable {
                            onRemove()
                        }
                )
            }
            Column(
                verticalArrangement = Arrangement.spacedBy(4.dp),
                modifier = Modifier
                    .fillMaxWidth()
            ) {
                RoofSectionRow("Areal", "%.1fm²".format(section.area))
                RoofSectionRow("Helning", "%.1f°".format(section.incline))
                RoofSectionRow("Paneler", section.panels.toString())
            }
        }
    }
}

@Composable
private fun RoofSectionRow(name: String, value: String) {
    Row(
        horizontalArrangement = Arrangement.SpaceBetween,
        modifier = Modifier
            .fillMaxWidth()
    ) {
        Text(
            text = name,
            fontWeight = FontWeight.Medium,
            fontSize = 16.sp
        )
        Text(
            text = value,
            fontWeight = FontWeight.Medium,
            fontSize = 15.sp
        )
    }
}

@Composable
private fun PriceSummaryCard(
    viewModel: ManageSolarArrayViewModel,
    solarPanelType: SolarPanelType,
    roofSections: SnapshotStateList<RoofSection>
) {
    val totalPanels = roofSections.sumOf { it.panels }
    val grossPrice = solarPanelType.totalPrice(totalPanels)
    val subsidy = calculateSubsidy(solarPanelType, totalPanels)

    Box(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(15.dp))
            .background(Light)
            .border(1.dp, DarkYellow, RoundedCornerShape(15.dp))
            .padding(horizontal = 40.dp, vertical = 15.dp)
    ) {
        Row(
            horizontalArrangement = Arrangement.SpaceBetween,
            modifier = Modifier
                .fillMaxWidth()
        ) {
            Column(
                verticalArrangement = Arrangement.SpaceBetween,
                modifier = Modifier
                    .width(150.dp)
                    .height(130.dp)
            ) {
                Text(
                    text = "Oversikt",
                    color = Color.Black,
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Bold
                )
                Column {
                    Text(
                        text = "Bruttopris",
                        color = Color.Black,
                        fontSize = 14.sp,
                    )
                    PriceText(grossPrice)
                }
                Column {
                    Text(
                        text = "Støtte fra staten",
                        color = Color.Black,
                        fontSize = 14.sp,
                    )
                    PriceText(subsidy)
                }
            }
            Column(
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(
                    text = "Totalpris",
                    color = Color.Black,
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Bold
                )
                Image(
                    painter = painterResource(R.drawable.planet),
                    contentDescription = "Planet med solcellepanel",
                    modifier = Modifier
                        .size(80.dp)
                )
                PriceText(grossPrice - subsidy)
            }
        }
    }
}

@Composable
private fun PriceText(price: Double) {
    Row(
        verticalAlignment = Alignment.Bottom,
        horizontalArrangement = Arrangement.spacedBy(2.dp)
    ) {
        Text(
            text = "%,d".format(price.toInt(), Locale.GERMANY).replace(",", " "),
            color = Color.Black,
            fontSize = 22.sp,
            fontWeight = FontWeight.Bold
        )

        Text(
            text = "NOK",
            color = Color.Black,
            fontSize = 14.sp,
            fontWeight = FontWeight.Medium
        )
    }
}

@Composable
private fun SaveButton(solarPanelType: SolarPanelType, roofSections: List<RoofSection>) {
    var name by remember { mutableStateOf("") }
    var openSaveDialog by remember { mutableStateOf(false) }

    if (openSaveDialog) {
        SaveDialog(
            onDismissRequest = { openSaveDialog = false },
            onSave = {
                openSaveDialog = false

                SolarArray(name, solarPanelType, roofSections)
                // TODO save to some view model
                // TODO navigate home
            },
            name,
            onNameChange = { name = it }
        )
    }

    OutlinedButton(
        onClick = {
            openSaveDialog = true
        },
        colors = ButtonDefaults.buttonColors(
            containerColor = Light
        ),
        border = BorderStroke(1.dp, DarkYellow),
        modifier = Modifier
            .width(250.dp)
            .padding(bottom = 60.dp, top = 20.dp)
    ) {
        Text(
            "Lagre",
            color = Color.Black,
            fontSize = 18.sp
        )
    }
}

@Composable
private fun SaveDialog(
    onDismissRequest: () -> Unit,
    onSave: () -> Unit,
    name: String,
    onNameChange: (String) -> Unit
) {
    Dialog(onDismissRequest) {
        Column(
            modifier = Modifier
                .clip(RoundedCornerShape(15.dp))
                .background(RandomBeige)
                .border(1.dp, BrightYellow, RoundedCornerShape(15.dp))
                .padding(horizontal = 16.dp, vertical = 10.dp)
        ) {
            Text(
                text = "Lagre anlegg",
                textAlign = TextAlign.Center,
                fontSize = 20.sp,
                fontWeight = FontWeight.Bold,
                modifier = Modifier
                    .fillMaxWidth(),
            )
            Text(
                text = "Ved å lagre dette anlegget vil det bli lagt til på hjemskjermen din.",
                textAlign = TextAlign.Center,
                fontSize = 14.sp,
                color = Color.DarkGray,
                modifier = Modifier
                    .fillMaxWidth()
            )
            CustomTextField(
                modifier = Modifier.fillMaxWidth(),
                containerModifier = Modifier.fillMaxWidth(),
                value = name,
                onValueChange = onNameChange,
                label = "Navn",
                placeholder = "Navn på anlegget",
            )
            OutlinedButton(
                onClick = onSave,
                colors = ButtonDefaults.buttonColors(
                    containerColor = Light
                ),
                contentPadding = PaddingValues(2.dp),
                border = BorderStroke(1.dp, DarkYellow),
                modifier = Modifier
                    .defaultMinSize(100.dp, 30.dp)
            ) {
                Text(
                    "Lagre",
                    color = Color.Black,
                    fontSize = 14.sp
                )
            }
        }
    }
}

@OptIn(ExperimentalFoundationApi::class)
@Composable
private fun SearchField(
    mapState: MapState,
    mapViewportState: MapViewportState,
    draggableState: AnchoredDraggableState<ArraySettingsMenuAnchors>,
    viewModel: ManageSolarArrayViewModel
) {
    val focusManager = LocalFocusManager.current
    val keyboardController = LocalSoftwareKeyboardController.current
    val scope = rememberCoroutineScope()

    val addressState = viewModel.mapAddress.collectAsState()
    val addressSuggestions = viewModel.mapSearchAddressSuggestions.collectAsState()
    var showSuggestions by remember { mutableStateOf(false) }

    val selectSuggestion: (Address) -> Unit = remember {
        { suggestion ->
            viewModel.setMapAddress(suggestion.toFormatted())
            viewModel.setMapAddress(suggestion)

            scope.launch {
                draggableState.animateTo(ArraySettingsMenuAnchors.Bottom)

                mapViewportState.easeTo(
                    CameraOptions.Builder()
                        .center(suggestion.pos.toPoint())
                        .zoom(19.0)
                        .build()
                )
            }
        }
    }

    Column {
        SearchTextField(
            address = addressState.value.query,
            onAddressChange = { address ->
                viewModel.setMapAddress(address)
            },
            onDone = {
                keyboardController?.hide()
                focusManager.clearFocus()
                addressSuggestions.value.suggestions.firstOrNull()?.let { selectSuggestion(it) }
            },
            onFocusChanged = { isFocused ->
                showSuggestions = isFocused
                if (isFocused) {
                    scope.launch {
                        draggableState.animateTo(ArraySettingsMenuAnchors.Top)
                    }
                }
            }
        )

        if (showSuggestions) {
            SuggestionsPopup(
                suggestions = addressSuggestions.value.suggestions,
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
    BasicTextField(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(100.dp))
            .background(
                Light,
                MaterialTheme.shapes.small
            )
            .border(1.dp, DarkYellow, shape = RoundedCornerShape(100.dp))
            .padding(start = 30.dp)
            .onFocusChanged { onFocusChanged(it.isFocused) },
        value = address,
        onValueChange = onAddressChange,
        singleLine = true,
        cursorBrush = SolidColor(MaterialTheme.colorScheme.secondary),
        textStyle = LocalTextStyle.current.copy(
            color = MaterialTheme.colorScheme.onSurface,
            fontSize = 15.sp
        ),
        keyboardOptions = KeyboardOptions(imeAction = ImeAction.Done),
        keyboardActions = KeyboardActions(onDone = { onDone() }),
        decorationBox = { innerTextField ->
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Box {
                    if (address.isEmpty()) {
                        Text(
                            text = "Søk addresse",
                            style = LocalTextStyle.current.copy(
                                color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.3f),
                                fontSize = 15.sp
                            )
                        )
                    }
                    innerTextField()
                }
                Icon(
                    Icons.Rounded.Search,
                    contentDescription = "Søk adresser",
                    tint = Color.White,
                    modifier = Modifier
                        .width(40.dp)
                        .height(40.dp)
                        .clip(RoundedCornerShape(100.dp))
                        .background(BrightYellow)
                        .padding(7.dp)
                )
            }
        }
    )
}

@Composable
private fun SuggestionsPopup(
    suggestions: List<Address>,
    onSuggestionClick: (Address) -> Unit,
    onDismissRequest: () -> Unit
) {
    Popup(
        alignment = Alignment.TopStart,
        onDismissRequest = onDismissRequest
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = 60.dp, start = 10.dp, end = 10.dp)
                .clip(RoundedCornerShape(15.dp))
                .background(MaterialTheme.colorScheme.tertiary)
                .border(1.dp, MaterialTheme.colorScheme.secondary, shape = RoundedCornerShape(15.dp))
        ) {
            suggestions.forEach { suggestion ->
                SuggestionItem(
                    suggestion = suggestion,
                    onClick = { onSuggestionClick(suggestion) }
                )
            }
        }
    }
}

@Composable
private fun SuggestionItem(suggestion: Address, onClick: () -> Unit) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .padding(10.dp)
            .clickable { onClick() }
    ) {
        Text(suggestion.toFormatted())
    }
}

@Composable
private fun AddRoofSectionCard(onAdd: (RoofSection) -> Unit) {
    var area by remember { mutableStateOf("") }
    var angle by remember { mutableStateOf("") }
    var direction by remember { mutableStateOf("") }
    var panels by remember { mutableStateOf("") }

    Column(
        verticalArrangement = Arrangement.spacedBy(5.dp),
        modifier = Modifier
            .clip(shape = RoundedCornerShape(15.dp))
            .background(Light)
            .border(1.dp, DarkYellow, shape = RoundedCornerShape(15.dp))
            .padding(10.dp)
    ) {
        Text(
            text = "Legg til takflate",
            fontSize = 18.sp,
            fontWeight = FontWeight.Bold,
            color = DarkYellow
        )
        Row(
            horizontalArrangement = Arrangement.spacedBy(10.dp),
            modifier = Modifier.fillMaxWidth()
        ) {
            NumberInputField(
                value = area,
                onValueChange = { area = it },
                label = "Areal",
                placeholder = "Areal i kvadratmeter",
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1f)
            )
            NumberInputField(
                value = angle,
                onValueChange = { angle = it },
                label = "Helning",
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
                label = "Retning",
                placeholder = "Retning i grader",
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1f)
            )
            NumberInputField(
                value = panels,
                onValueChange = { panels = it },
                label = "Paneler",
                placeholder = "Antall paneler",
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1f)
            )
        }
        AddRoofButton {
            onAdd(
                RoofSection(
                    area.toDouble(),
                    angle.toDouble(),
                    direction.toDouble(),
                    panels.toInt(),
                    mapId = null,
                )
            )
        }
    }
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
            containerColor = Light
        ),
        border = BorderStroke(1.dp, DarkBeige),
        modifier = Modifier
            .defaultMinSize(100.dp, 30.dp),
        contentPadding = PaddingValues(0.dp),
    ) {
        Text(
            "Legg til",
            color = Color.Black,
            fontSize = 14.sp
        )
    }
}


@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun SolarPanelTypeDropdown(solarPanelType: SolarPanelType, onSelect: (SolarPanelType) -> Unit) {
    var dropdownExpanded by remember { mutableStateOf(false) }

    Column(
        modifier = Modifier
            .fillMaxWidth()
    ) {
        Text(
            "Paneltype",
            fontWeight = FontWeight.Bold,
            color = Color.Black
        )
        ExposedDropdownMenuBox(
            expanded = dropdownExpanded,
            onExpandedChange = { dropdownExpanded = it }
        ) {
            BasicTextField(
                value = solarPanelType.nameWithWatt(),
                readOnly = true,
                onValueChange = {},
                textStyle = LocalTextStyle.current.copy(
                    color = MaterialTheme.colorScheme.onSurface,
                    fontSize = MaterialTheme.typography.bodyMedium.fontSize
                ),
                modifier = Modifier
                    .menuAnchor(MenuAnchorType.PrimaryEditable, true)
                    .clip(RoundedCornerShape(15))
                    .background(Light)
                    .border(1.dp, DarkYellow, RoundedCornerShape(15))
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