package no.uio.ifi.in2000.team54.ui.managesolararray


import androidx.compose.animation.core.tween
import androidx.compose.animation.rememberSplineBasedDecay
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.AnchoredDraggableState
import androidx.compose.foundation.gestures.DraggableAnchors
import androidx.compose.foundation.gestures.Orientation
import androidx.compose.foundation.gestures.anchoredDraggable
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
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.Icon
import androidx.compose.material3.LocalTextStyle
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.MenuAnchorType
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.runtime.snapshots.SnapshotStateList
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.mapbox.geojson.Point
import com.mapbox.maps.extension.compose.MapState
import com.mapbox.maps.extension.compose.animation.viewport.MapViewportState
import com.mapbox.maps.extension.compose.animation.viewport.rememberMapViewportState
import com.mapbox.maps.extension.compose.rememberMapState
import kotlinx.coroutines.launch
import no.uio.ifi.in2000.team54.domain.RoofSection
import no.uio.ifi.in2000.team54.domain.SolarArray
import no.uio.ifi.in2000.team54.enums.SolarPanelType
import no.uio.ifi.in2000.team54.ui.theme.BrightYellow
import no.uio.ifi.in2000.team54.ui.theme.DarkYellow
import no.uio.ifi.in2000.team54.ui.theme.Light
import no.uio.ifi.in2000.team54.ui.theme.LightestYellow
import kotlin.math.roundToInt

private val osloCenter = Point.fromLngLat(10.7522, 59.9139)

enum class ArraySettingsMenuAnchors { Bottom, Top }

@Composable
fun ManageSolarArrayScreen(
    viewModel: ManageSolarArrayViewModel,
    navController: NavController,
    snackbarState: SnackbarHostState,
    updateArray: Long? = -1L,
) {
    val solarEntity by viewModel.currentSolarArray.collectAsState()
    val roofSections = remember { mutableStateListOf<RoofSection>() }
    val solarPanelType = rememberSaveable {
        mutableStateOf(solarEntity?.panelType ?: SolarPanelType.ECONOMY)
    }

    LaunchedEffect(updateArray) {
        if (updateArray != -1L) {
            viewModel.getSolarArray(updateArray!!.toLong())
        }
    }

    LaunchedEffect(solarEntity) { // if we are updating
        if (solarEntity != null) {
            roofSections.clear()
            roofSections.addAll(solarEntity!!.roofSections)
            solarEntity!!.panelType.let {
                solarPanelType.value = it
            }
        }
    }

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
        SolarArrayMap(mapState, mapViewportState, snackbarState, viewModel, solarPanelType.value, roofSections)

        BackButton(viewModel, navController)
        Box(
            modifier = Modifier
                .fillMaxSize()
        ) {
            ArraySettingsMenu(mapState, mapViewportState, snackbarState, viewModel, navController, solarPanelType, roofSections)
        }
    }
}

@Composable
private fun BackButton(viewModel: ManageSolarArrayViewModel, navController: NavController) {
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
                viewModel.setSearchAddress("")
                navController.navigate("home")
                viewModel.resetUpdSolarArray()
            }
    )
}

@OptIn(ExperimentalFoundationApi::class)
@Composable
private fun ArraySettingsMenu(
    mapState: MapState,
    mapViewportState: MapViewportState,
    snackbarState: SnackbarHostState,
    viewModel: ManageSolarArrayViewModel,
    navController: NavController,
    solarPanelType: MutableState<SolarPanelType>,
    roofSections: SnapshotStateList<RoofSection>,
    ) {
    val screenSizeDp = LocalConfiguration.current.screenHeightDp.dp + 20.dp
    val screenSizePx = with(LocalDensity.current) { screenSizeDp.toPx() }

    val anchors = DraggableAnchors {
        ArraySettingsMenuAnchors.Bottom at screenSizePx - 700f
        ArraySettingsMenuAnchors.Top at 150f
    }

    val decayAnimation = rememberSplineBasedDecay<Float>()
    val draggableState = remember {
        AnchoredDraggableState(
            initialValue = ArraySettingsMenuAnchors.Bottom,
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
                snackbarState,
                draggableState,
                viewModel,
                navController,
                solarPanelType,
                roofSections,
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
    snackbarState: SnackbarHostState,
    draggableState: AnchoredDraggableState<ArraySettingsMenuAnchors>,
    viewModel: ManageSolarArrayViewModel,
    navController: NavController,
    solarPanelType: MutableState<SolarPanelType>,
    roofSections: SnapshotStateList<RoofSection>,
) {
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
            snackbarState,
            draggableState,
            navController,
            viewModel,
            solarPanelType.value,
            roofSections,
            { selectedType ->
                solarPanelType.value = selectedType

                // we need to clamp each roof section's amount of panels to the max amount of panels
                // the roof section has space for, this is necessary when changing solar panel type
                // because the different types are different sizes
                roofSections.forEach { roofSection ->
                    val maxPanelAmount = (roofSection.area / solarPanelType.value.area()).toInt()
                    if (roofSection.panels <= maxPanelAmount) {
                        return@forEach
                    }

                    // we need to create a new RoofSection object to trigger a re-render by the state changing
                    roofSections[roofSections.indexOf(roofSection)] = RoofSection(
                        roofSection.id,
                        roofSection.area,
                        roofSection.incline,
                        roofSection.direction,
                        maxPanelAmount,
                        roofSection.mapId
                    )
                }
            },
        )
    }
}

@OptIn(ExperimentalFoundationApi::class)
@Composable
private fun ArraySettingsMainSection(
    mapState: MapState,
    mapViewportState: MapViewportState,
    snackbarState: SnackbarHostState,
    draggableState: AnchoredDraggableState<ArraySettingsMenuAnchors>,
    navController: NavController,
    viewModel: ManageSolarArrayViewModel,
    solarPanelType: SolarPanelType,
    roofSections: SnapshotStateList<RoofSection>,
    onSelectPanelType: (SolarPanelType) -> Unit,
) {
    val addressState by viewModel.mapAddress.collectAsState()
    var editingRoofSection by remember { mutableStateOf<Int?>(null) }
    var openSaveDialog by remember { mutableStateOf(false) }
    val solarEntity by viewModel.currentSolarArray.collectAsState()

    val scope = rememberCoroutineScope()
    val scroll = rememberScrollState()

    Column(
        verticalArrangement = Arrangement.spacedBy(6.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        DragHandle(draggableState)
        SearchField(mapState, mapViewportState, draggableState, viewModel)
        Spacer(modifier = Modifier.size(10.dp))
        Column(
            verticalArrangement = Arrangement.spacedBy(15.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = Modifier
                .then(
                    if (draggableState.currentValue == ArraySettingsMenuAnchors.Top) Modifier.verticalScroll(scroll)
                    else Modifier
                )
        ) {
            RoofSectionsList(roofSections, { editingRoofSection = null }, { editingRoofSection = it })
            ManageRoofSectionCard(roofSections, editingRoofSectionIndex = editingRoofSection, { editingRoofSection = null })
            SolarPanelTypeDropdown(solarPanelType, onSelectPanelType)
            PriceSummaryCard(solarPanelType, roofSections) //Inni denne ligger totalkosten
            SaveButton {
                if (addressState.address == null) {
                    scope.launch {
                        snackbarState.showSnackbar("Du må fylle inn en adresse for å lagre.")
                    }
                    return@SaveButton
                }

                if (roofSections.isEmpty()) {
                    scope.launch {
                        snackbarState.showSnackbar("Du må legge til minst én takflate for å lagre.")
                    }
                    return@SaveButton
                }

                openSaveDialog = true
            }
        }
    }
    SaveDialog(viewModel, openSaveDialog, onClose = { openSaveDialog = false }, onSave = { name, power ->
        val solarObj = SolarArray(
            id = null,
            name,
            solarPanelType,
            roofSections,
            addressState.address!!.pos.toCoordinates(),
            power.toDouble(),
            addressState.address!!.toFormatted()
        )
        if (solarEntity == null) {
            viewModel.addSolarArray(solarObj)
        } else {
            // if solarentity exists then you just want to update the values not create and save a whole new one
            viewModel.updateSolarArray(solarObj.copy(id = solarEntity!!.id))
        }
        viewModel.setSearchAddress("")
        navController.navigate("home")
    })
}

@OptIn(ExperimentalFoundationApi::class)
@Composable
private fun DragHandle(draggableState: AnchoredDraggableState<ArraySettingsMenuAnchors>) {
    val direction = if (draggableState.currentValue == ArraySettingsMenuAnchors.Top) 1 else -1

    Row(
        horizontalArrangement = Arrangement.spacedBy((-5).dp),
        modifier = Modifier
            .padding(top = 5.dp)
    ) {
        Box(
            modifier = Modifier
                .padding(bottom = 8.dp)
                .width(35.dp)
                .height(7.dp)
                .rotate(20f * direction)
                .clip(shape = RoundedCornerShape(100.dp))
                .background(BrightYellow)
        )
        Box(
            modifier = Modifier
                .padding(bottom = 8.dp)
                .width(35.dp)
                .height(7.dp)
                .rotate(20f * direction * -1)
                .clip(shape = RoundedCornerShape(100.dp))
                .background(BrightYellow)
        )
    }
}

@Composable
private fun SaveButton(
    onClick: () -> Unit
) {
    OutlinedButton(
        onClick = onClick,
        colors = ButtonDefaults.buttonColors(
            containerColor = Light
        ),
        border = BorderStroke(1.dp, DarkYellow),
        modifier = Modifier
            .width(250.dp)
            .padding(top = 5.dp)
    ) {
        Text(
            "Lagre",
            color = Color.Black,
            fontSize = 18.sp
        )
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun SolarPanelTypeDropdown(
    solarPanelType: SolarPanelType,
    onSelect: (SolarPanelType) -> Unit,
) {
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