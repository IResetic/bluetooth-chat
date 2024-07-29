package dev.skybit.bluetoothchat.home.presentation.ui.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import dev.skybit.bluetoothchat.core.presentation.constants.mediumPadding

@Composable
fun AvailabilitySwitch(
    isConnectionAvailable: Boolean = false,
    changeAvailability: (Boolean) -> Unit
) {
    Row(
        modifier = Modifier.fillMaxWidth().padding(mediumPadding),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(text = "Make me available for connection:", style = MaterialTheme.typography.bodyLarge)

        Switch(
            checked = isConnectionAvailable,
            onCheckedChange = { changeAvailability(it) }
        )
    }
}
