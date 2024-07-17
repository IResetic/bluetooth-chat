package dev.skybit.bluetoothchat.availableconnections.presentation.ui.screens.connections

import dev.skybit.bluetoothchat.availableconnections.domain.model.BluetoothDeviceInfo

sealed interface AvailableConnectionsScreenEvent {
    data object StartScanning : AvailableConnectionsScreenEvent
    data object StopScanning : AvailableConnectionsScreenEvent
    data class ConnectToBluetoothDevice(val device: BluetoothDeviceInfo) : AvailableConnectionsScreenEvent
    data object DisconnectFromBluetoothDevice : AvailableConnectionsScreenEvent
}
