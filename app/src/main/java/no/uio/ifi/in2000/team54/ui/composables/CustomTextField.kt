package no.uio.ifi.in2000.team54.ui.composables

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.LocalTextStyle
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.TextUnit
import androidx.compose.ui.unit.dp
import no.uio.ifi.in2000.team54.ui.theme.Beige
import no.uio.ifi.in2000.team54.ui.theme.DarkBeige

@Composable
fun CustomTextField(
    modifier: Modifier = Modifier,
    containerModifier: Modifier = Modifier,
    value: String,
    onValueChange: (String) -> Unit,
    label: String = "Label",
    placeholder: String = "",
    fontSize: TextUnit = MaterialTheme.typography.bodyMedium.fontSize,
    keyboardOptions: KeyboardOptions = KeyboardOptions.Default
) {
    val keyboardController = LocalSoftwareKeyboardController.current

    Column(
        modifier = containerModifier
    ) {
        Text(label, fontWeight = FontWeight.Bold)
        BasicTextField(
            modifier = modifier
                .clip(RoundedCornerShape(15))
                .background(Beige)
                .border(1.dp, DarkBeige, RoundedCornerShape(15))
                .padding(10.dp),
            value = value,
            onValueChange = onValueChange,
            singleLine = true,
            cursorBrush = SolidColor(MaterialTheme.colorScheme.secondary),
            textStyle = LocalTextStyle.current.copy(
                color = MaterialTheme.colorScheme.onSurface,
                fontSize = fontSize
            ),
            keyboardOptions = keyboardOptions,
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
                    Box {
                        if (value.isEmpty()) {
                            Text(
                                text = placeholder,
                                style = LocalTextStyle.current.copy(
                                    color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.5f),
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