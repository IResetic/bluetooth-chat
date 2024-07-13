package dev.skybit.bluetoothchat.availableconnections.presentation.ui

import androidx.compose.runtime.Immutable
import dev.skybit.bluetoothchat.availableconnections.domain.model.BluetoothDeviceInfo

@Immutable
data class AvailableConnectionsUiState(
    val scannedDevices: List<BluetoothDeviceInfo> = emptyList(),
    val pairedDevices: List<BluetoothDeviceInfo> = emptyList(),
    val isSceningDevices: Boolean = false,
    val isConnected: Boolean = false,
    val isConnecting: Boolean = false,
    val errorMessage: String? = null
)
