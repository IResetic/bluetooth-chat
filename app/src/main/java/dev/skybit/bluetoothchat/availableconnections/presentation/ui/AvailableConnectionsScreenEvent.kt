package dev.skybit.bluetoothchat.availableconnections.presentation.ui

sealed interface AvailableConnectionsScreenEvent {
    data object StartScanning : AvailableConnectionsScreenEvent
    data object StopScanning : AvailableConnectionsScreenEvent
}
