package dev.skybit.bluetoothchat.chat.presentation.ui.screens.availableconnections

import dev.skybit.bluetoothchat.chat.domain.model.BluetoothDeviceInfo

sealed interface AvailableConnectionsScreenEvent {
    data object StartScanning : AvailableConnectionsScreenEvent
    data object StopScanning : AvailableConnectionsScreenEvent
    data class ConnectToBluetoothDevice(val device: BluetoothDeviceInfo) : AvailableConnectionsScreenEvent
    data object DisconnectFromBluetoothDevice : AvailableConnectionsScreenEvent
}
