package no.uio.ifi.in2000.team54.ui.managesolararray

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.defaultMinSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.runtime.snapshots.SnapshotStateList
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import no.uio.ifi.in2000.team54.domain.RoofSection
import no.uio.ifi.in2000.team54.ui.composables.NumberInputField
import no.uio.ifi.in2000.team54.ui.theme.DarkYellow
import no.uio.ifi.in2000.team54.ui.theme.Light
import no.uio.ifi.in2000.team54.ui.theme.Red

@Composable
fun ManageRoofSectionCard(
    roofSections: SnapshotStateList<RoofSection>,
    editingRoofSectionIndex: Int?,
    onSave: () -> Unit,
) {
    val keyboardController = LocalSoftwareKeyboardController.current

    val isEditing = editingRoofSectionIndex != null
    val editingRoofSection = if (isEditing) roofSections[editingRoofSectionIndex!!] else null
    var isEditingInitialized by rememberSaveable(editingRoofSectionIndex) { mutableStateOf(false) }

    var area by rememberSaveable(isEditing) { mutableStateOf("") }
    var incline by rememberSaveable(isEditing) { mutableStateOf("") }
    var direction by rememberSaveable(isEditing) { mutableStateOf("") }
    var panels by rememberSaveable(isEditing) { mutableStateOf("") }
    var validate by rememberSaveable(isEditing) { mutableStateOf(false) }
    val isValid = !validate || (area.isNotEmpty() && incline.isNotEmpty() && direction.isNotEmpty() && panels.isNotEmpty())

    if (isEditing && !isEditingInitialized) {
        isEditingInitialized = true

        area = "%.2f".format(editingRoofSection!!.area)
        incline = "%.2f".format(editingRoofSection.incline)
        direction = "%.2f".format(editingRoofSection.direction)
        panels = editingRoofSection.panels.toString()
    }

    Column(
        verticalArrangement = Arrangement.spacedBy(5.dp),
        modifier = Modifier
            .clip(shape = RoundedCornerShape(15.dp))
            .background(Light)
            .border(1.dp, DarkYellow, shape = RoundedCornerShape(15.dp))
            .padding(10.dp)
    ) {
        Text(
            text = if (isEditing) "Redigerer takflate ${editingRoofSectionIndex!! + 1}" else "Legg til takflate",
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
                validate = validate,
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1f)
            )
            NumberInputField(
                value = incline,
                onValueChange = { incline = it },
                label = "Helning",
                placeholder = "Helning i grader",
                validate = validate,
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
                validate = validate,
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1f)
            )
            NumberInputField(
                value = panels,
                onValueChange = { panels = it },
                label = "Paneler",
                placeholder = "Antall paneler",
                validate = validate,
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1f)
            )
        }
        Button(
            onClick = {
                keyboardController?.hide()

                if (area.isEmpty() || incline.isEmpty() || direction.isEmpty() || panels.isEmpty()) {
                    validate = true
                    return@Button
                }

                val newSection = RoofSection(
                    id = null,
                    area.toDouble(),
                    incline.toDouble(),
                    direction.toDouble(),
                    panels.toInt(),
                    mapId = editingRoofSection?.mapId,
                )
                if (isEditing) {
                    roofSections[editingRoofSectionIndex!!] = newSection
                } else {
                    roofSections.add(newSection)
                }

                onSave()

                validate = false
                area = ""
                incline = ""
                direction = ""
                panels = ""
            },
            colors = ButtonDefaults.buttonColors(
                containerColor = Light
            ),
            border = BorderStroke(1.dp, if (isValid) DarkYellow else Red),
            modifier = Modifier
                .defaultMinSize(100.dp, 30.dp),
            contentPadding = PaddingValues(0.dp),
        ) {
            Text(
                if (isEditing) "Lagre" else "Legg til",
                color = if (isValid) Color.Black else Red,
                fontSize = 14.sp
            )
        }
    }
}