package dev.skybit.bluetoothchat.home.presentation.ui

import dev.skybit.bluetoothchat.home.domain.model.BluetoothDeviceInfo

sealed interface HomeScreenEvent {
    data class SetConnectionAvailability(val isAvailable: Boolean) : HomeScreenEvent
    data object NavigateToDevicesScreen : HomeScreenEvent
    data class ShowChat(val senderName: String, val chatId: String) : HomeScreenEvent
    data object NavigateBackToHomeScreen : HomeScreenEvent
    data class SendMessage(val message: String) : HomeScreenEvent
    data class ConnectToBluetoothDevice(val device: BluetoothDeviceInfo) : HomeScreenEvent
    data class ScanForDevices(val isScanning: Boolean) : HomeScreenEvent
    data object ErrorConnectingToDevice : HomeScreenEvent
    data object ChatError : HomeScreenEvent
}
