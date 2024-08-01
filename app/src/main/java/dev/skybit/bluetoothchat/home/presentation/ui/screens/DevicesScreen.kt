package dev.skybit.bluetoothchat.home.presentation.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import dev.skybit.bluetoothchat.R
import dev.skybit.bluetoothchat.home.domain.model.BluetoothDeviceInfo
import dev.skybit.bluetoothchat.chat.presentation.ui.components.SectionHeader
import dev.skybit.bluetoothchat.home.presentation.ui.components.ConnectionErrorDialog
import dev.skybit.bluetoothchat.home.presentation.ui.components.DevicesListItem

@Composable
fun DevicesScreen(
    pairedDevices: List<BluetoothDeviceInfo>,
    scannedDevices: List<BluetoothDeviceInfo>,
    isConnecting: Boolean = false,
    errorMessage: String? = null,
    onErrorHandler: () -> Unit,
    connectToDevice: (BluetoothDeviceInfo) -> Unit
) {
    LazyColumn {
        if (pairedDevices.isNotEmpty()) {
            item { SectionHeader(sectionHeaderId = R.string.pair_devices_header) }
        }

        items(pairedDevices) { device ->
            DevicesListItem(device = device, !isConnecting) {
                connectToDevice(device)
            }
        }

        if (scannedDevices.isNotEmpty()) {
            item { SectionHeader(sectionHeaderId = R.string.available_devices_header) }
        }

        items(scannedDevices) { device ->
            DevicesListItem(device = device, !isConnecting) {
                connectToDevice(device)
            }
        }
    }

    Box(
        modifier = Modifier.fillMaxSize(),
        // .padding(paddingValues),
        contentAlignment = Alignment.TopStart
    ) {
        if (isConnecting) {
            Box(
                contentAlignment = Alignment.Center,
                modifier = Modifier
                    .fillMaxSize()
                    .background(Color.Black.copy(alpha = 0.5f))

            ) {
                CircularProgressIndicator(
                    color = MaterialTheme.colorScheme.primary
                )
            }
        }

        if (errorMessage != null) {
            ConnectionErrorDialog(
                onDismissRequest = onErrorHandler
            )
        }
    }
}
