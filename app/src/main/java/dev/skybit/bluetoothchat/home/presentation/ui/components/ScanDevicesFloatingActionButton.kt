package dev.skybit.bluetoothchat.home.presentation.ui.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.ExtendedFloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import dev.skybit.bluetoothchat.R
import dev.skybit.bluetoothchat.core.presentation.constants.smallPadding

@Composable
fun ScanDevicesFloatingActionButton(
    onClick: (startScanning: Boolean) -> Unit
) {
    val colorScheme = MaterialTheme.colorScheme
    val isScanning = rememberSaveable {
        mutableStateOf(false)
    }

    fun onButtonClicked() {
        isScanning.value = !isScanning.value
        onClick(isScanning.value)
    }

    ExtendedFloatingActionButton(
        onClick = ::onButtonClicked,
        containerColor = if (isScanning.value) colorScheme.primary else colorScheme.inversePrimary,
        contentColor = MaterialTheme.colorScheme.onPrimary
    ) {
        Row(
            horizontalArrangement = Arrangement.Center,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(
                if (isScanning.value) Icons.Filled.Close else Icons.Filled.Search,
                "Scan for devices FAB.",
                modifier = Modifier.padding(end = smallPadding),
                tint = if (isScanning.value) colorScheme.onPrimary else colorScheme.onPrimaryContainer

            )
            Text(
                text = stringResource(id = R.string.start_scanning_button_title),
                color = if (isScanning.value) colorScheme.onPrimary else colorScheme.onPrimaryContainer
            )
        }
    }
}
