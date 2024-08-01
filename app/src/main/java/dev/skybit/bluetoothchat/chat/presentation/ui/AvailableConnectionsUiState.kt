package dev.skybit.bluetoothchat.chat.presentation.ui

import androidx.compose.runtime.Immutable
import dev.skybit.bluetoothchat.home.domain.model.BluetoothDeviceInfo
import dev.skybit.bluetoothchat.home.domain.model.BluetoothMessage
import dev.skybit.bluetoothchat.chat.presentation.ui.model.ScreenType

@Immutable
data class AvailableConnectionsUiState(
    val scannedDevices: List<BluetoothDeviceInfo> = emptyList(),
    val pairedDevices: List<BluetoothDeviceInfo> = emptyList(),
    val isSceningDevices: Boolean = false,
    val isConnected: Boolean = false,
    val isConnecting: Boolean = false,
    val errorMessage: String? = null,
    val startNewChat: BluetoothDeviceInfo? = null,
    val screenType: ScreenType = ScreenType.DEVICES,
    val messages: List<BluetoothMessage> = emptyList()
)
