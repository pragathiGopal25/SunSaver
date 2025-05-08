package no.uio.ifi.in2000.team54.ui.managesolararray

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.defaultMinSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import no.uio.ifi.in2000.team54.ui.composables.CustomTextField
import no.uio.ifi.in2000.team54.ui.composables.NumberInputField
import no.uio.ifi.in2000.team54.ui.theme.RipeLemon
import no.uio.ifi.in2000.team54.ui.theme.VistaWhite
import no.uio.ifi.in2000.team54.ui.theme.Tamarillo

@Composable
fun SaveDialog(
    viewModel: ManageSolarArrayViewModel,
    open: Boolean,
    onClose: () -> Unit,
    onSave: (name: String, power: String) -> Unit,
) {
    if (!open) {
        return
    }
    val solarEntity = viewModel.currentSolarArray.collectAsStateWithLifecycle()
    var validate by rememberSaveable { mutableStateOf(false) }

    // shows the saved name and power values. If they dont exist, shows the default values.
    var name by rememberSaveable { mutableStateOf(solarEntity.value?.name ?: "")}
    var power by rememberSaveable { mutableStateOf(solarEntity.value?.powerConsumption?.toString() ?: "1574.5") }
    val isValid = !validate || (name.isNotEmpty() && power.isNotEmpty())

    Dialog({
        validate = false
        onClose()
    }) {
        Column(
            modifier = Modifier
                .clip(RoundedCornerShape(15.dp))
                .background(VistaWhite)
                .border(1.dp, RipeLemon, RoundedCornerShape(15.dp))
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
                onValueChange = { name = it },
                label = "Navn",
                placeholder = "Navn på anlegget",
                validate = validate,
            )
            NumberInputField(
                value = power,
                onValueChange = { power = it },
                label = "Strømforbruk (kWh per måned)",
                placeholder = "Månedlig strømforbruk",
                validate = validate
            )
            OutlinedButton(
                onClick = {
                    if (name.isEmpty() || power.isEmpty()) {
                        validate = true
                        return@OutlinedButton
                    }

                    onClose()
                    onSave(name, power)
                    viewModel.resetUpdSolarArray()
                },
                colors = ButtonDefaults.buttonColors(
                    containerColor = VistaWhite
                ),
                contentPadding = PaddingValues(2.dp),
                border = BorderStroke(1.dp, if (isValid) RipeLemon else Tamarillo),
                modifier = Modifier
                    .defaultMinSize(100.dp, 30.dp)
            ) {
                Text(
                    "Lagre",
                    color = if (isValid) Color.Black else Tamarillo,
                    fontSize = 14.sp
                )
            }
        }
    }
}