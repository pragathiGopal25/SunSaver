package no.uio.ifi.in2000.team54.ui.managesolararray

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.AnchoredDraggableState
import androidx.compose.foundation.gestures.animateTo
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Search
import androidx.compose.material3.Icon
import androidx.compose.material3.LocalTextStyle
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
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
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Popup
import com.mapbox.maps.CameraOptions
import com.mapbox.maps.extension.compose.MapState
import com.mapbox.maps.extension.compose.animation.viewport.MapViewportState
import kotlinx.coroutines.launch
import no.uio.ifi.in2000.team54.model.building.Address
import no.uio.ifi.in2000.team54.ui.theme.BrightYellow
import no.uio.ifi.in2000.team54.ui.theme.DarkYellow
import no.uio.ifi.in2000.team54.ui.theme.Light

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun SearchField(
    mapState: MapState,
    snackbarState: SnackbarHostState,
    mapViewportState: MapViewportState,
    draggableState: AnchoredDraggableState<ArraySettingsMenuAnchors>,
    viewModel: ManageSolarArrayViewModel,
) {
    val focusManager = LocalFocusManager.current
    val keyboardController = LocalSoftwareKeyboardController.current
    val scope = rememberCoroutineScope()
    val addressState = viewModel.mapSearchAddress.collectAsState()
    val addressSuggestions = viewModel.mapSearchAddressSuggestions.collectAsState()
    var showSuggestions by remember { mutableStateOf(false) }
    // Use the address from the selected solar array or the search address
    val solarEntity by viewModel.currentSolarArray.collectAsState()
    val snackbarShown = remember { mutableStateOf(false) } // to prevent showing snackbar everytime the cursor moves on the search field


    // recomposes everytime there is a change in the solar entity
    LaunchedEffect(solarEntity) {
        if (solarEntity != null) {
            viewModel.updateSolarArrayAddress(solarEntity) // Load the solar array to edit
            mapViewportState.easeTo(
                CameraOptions.Builder()
                    .center(solarEntity!!.coordinates.toPoint())
                    .zoom(19.0)
                    .build()
            )
        } else {
            viewModel.setSearchAddress("")
        }
    }
    val selectSuggestion: (Address) -> Unit = remember {
        { suggestion ->
            val selectedAddress = suggestion.toFormatted()
            val currentAddress = addressState.value.query

            //  only show snackbar if suggested address is different than the one in the searchfield
            if (solarEntity != null && selectedAddress != currentAddress) {
                if (!snackbarShown.value) {
                    snackbarShown.value = true
                    scope.launch {
                        snackbarState.showSnackbar("Du kan ikke endre adressen når du redigerer et eksisterende solcelleanlegg.")
                    }
                }
            } else {
                viewModel.setSearchAddress(suggestion.toFormatted())
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
    }
    Column {
        SearchTextField(
            address = addressState.value.query,
            onAddressChange = { address ->
                if (solarEntity != null) {
                    if (!snackbarShown.value) {
                        snackbarShown.value = true
                        scope.launch {
                            snackbarState.showSnackbar("Du kan ikke endre adressen når du redigerer et eksisterende solcelleanlegg.")
                        }
                    }
                } else {
                    viewModel.setSearchAddress(address)
                }
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
                } else {
                     snackbarShown.value = false
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
                }
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
                            text = "Søk adresse",
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
    onSuggestionClick: (Address) -> Unit
) {
    Popup(
        alignment = Alignment.TopStart,
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