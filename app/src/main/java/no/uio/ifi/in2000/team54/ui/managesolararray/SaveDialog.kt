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
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import no.uio.ifi.in2000.team54.ui.composables.CustomTextField
import no.uio.ifi.in2000.team54.ui.composables.NumberInputField
import no.uio.ifi.in2000.team54.ui.theme.DarkYellow
import no.uio.ifi.in2000.team54.ui.theme.Light
import no.uio.ifi.in2000.team54.ui.theme.Red

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

    val addressState by viewModel.mapAddress.collectAsState()

    var validate by remember { mutableStateOf(false) }
    var name by remember { mutableStateOf("") }
    var power by remember { mutableStateOf("1574.5") }

    val isValid = !validate || (name.isNotEmpty() && power.isNotEmpty() && addressState.address != null)

    Dialog({
        validate = false
        onClose()
    }) {
        Column(
            modifier = Modifier
                .clip(RoundedCornerShape(15.dp))
                .background(Light)
                .border(1.dp, DarkYellow, RoundedCornerShape(15.dp))
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
            if (validate && addressState.address == null) {
                Text(
                    text = "Du må fylle inn en adresse for å lagre.",
                    textAlign = TextAlign.Center,
                    fontSize = 14.sp,
                    color = Red,
                    modifier = Modifier
                        .padding(top = 5.dp)
                        .fillMaxWidth()
                )
            }
            OutlinedButton(
                onClick = {
                    if (name.isEmpty() || power.isEmpty() || addressState.address == null) {
                        validate = true
                        return@OutlinedButton
                    }

                    onClose()
                    onSave(name, power)
                },
                colors = ButtonDefaults.buttonColors(
                    containerColor = Light
                ),
                contentPadding = PaddingValues(2.dp),
                border = BorderStroke(1.dp, if (isValid) DarkYellow else Red),
                modifier = Modifier
                    .defaultMinSize(100.dp, 30.dp)
            ) {
                Text(
                    "Lagre",
                    color = if (isValid) Color.Black else Red,
                    fontSize = 14.sp
                )
            }
        }
    }
}