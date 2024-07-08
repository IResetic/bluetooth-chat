package dev.skybit.bluetoothchat.availableconnections.presentation.ui

sealed interface AvailableConnectionsScreenEvent {
    data object StartStopScanning : AvailableConnectionsScreenEvent
}
