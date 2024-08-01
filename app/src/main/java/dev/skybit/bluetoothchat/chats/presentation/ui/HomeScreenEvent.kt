package dev.skybit.bluetoothchat.chats.presentation.ui

import dev.skybit.bluetoothchat.chats.domain.model.BluetoothDeviceInfo

sealed interface HomeScreenEvent {
    data class SetConnectionAvailability(val isAvailable: Boolean) : HomeScreenEvent
    data object NavigateToDevicesScreen : HomeScreenEvent
    data object NavigateBackToHomeScreen : HomeScreenEvent
    data class SendMessage(val message: String) : HomeScreenEvent
    data class ConnectToBluetoothDevice(val device: BluetoothDeviceInfo) : HomeScreenEvent
    data class ScanForDevices(val isScanning: Boolean) : HomeScreenEvent
    data object ErrorConnectingToDevice : HomeScreenEvent
    data object ChatError : HomeScreenEvent
}
