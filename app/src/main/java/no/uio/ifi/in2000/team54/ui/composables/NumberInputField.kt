package no.uio.ifi.in2000.team54.ui.composables

import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import no.uio.ifi.in2000.team54.util.isNumber

@Composable
fun NumberInputField(
    value: String,
    onValueChange: (String) -> Unit,
    label: String,
    placeholder: String,
    validate: Boolean,
    modifier: Modifier = Modifier
) {
    CustomTextField(
        modifier = Modifier.fillMaxWidth(),
        containerModifier = modifier,
        value = value,
        onValueChange = { if (it.isNumber()) onValueChange(it) },
        label = label,
        placeholder = placeholder,
        validate = validate,
        keyboardOptions = KeyboardOptions(
            keyboardType = KeyboardType.Number,
            imeAction = ImeAction.Done
        )
    )
}