package no.uio.ifi.in2000.team54.ui.composables

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
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
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.TextUnit
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import no.uio.ifi.in2000.team54.ui.theme.Caramel
import no.uio.ifi.in2000.team54.ui.theme.Rajah
import no.uio.ifi.in2000.team54.ui.theme.Tamarillo

@Composable
fun CustomTextField(
    modifier: Modifier = Modifier,
    containerModifier: Modifier = Modifier,
    value: String,
    onValueChange: (String) -> Unit,
    label: String = "Label",
    placeholder: String = "",
    validate: Boolean,
    fontSize: TextUnit = 12.sp,
    keyboardOptions: KeyboardOptions = KeyboardOptions.Default
) {
    val keyboardController = LocalSoftwareKeyboardController.current

    val isInvalid = validate && value.isEmpty()

    Column(
        modifier = containerModifier
    ) {
        Row(
            horizontalArrangement = Arrangement.SpaceBetween,
            modifier = Modifier
                .fillMaxWidth()
        ) {
            Text(
                label,
                fontWeight = FontWeight.Bold,
                fontSize = 13.sp
            )
            if (isInvalid) {
                Text(
                    "Må fylles ut",
                    fontWeight = FontWeight.Bold,
                    fontSize = 13.sp,
                    color = Tamarillo
                )
            }
        }
        BasicTextField(
            modifier = modifier
                .clip(RoundedCornerShape(15))
                .background(Caramel)
                .border(1.dp, if (isInvalid) Tamarillo else Rajah, RoundedCornerShape(15))
                .padding(vertical = 5.dp, horizontal = 10.dp),
            value = value,
            onValueChange = onValueChange,
            singleLine = true,
            cursorBrush = SolidColor(Color.Black),
            textStyle = LocalTextStyle.current.copy(
                color = Color.Black,
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
                                    color = Color.Black.copy(alpha = 0.6f),
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