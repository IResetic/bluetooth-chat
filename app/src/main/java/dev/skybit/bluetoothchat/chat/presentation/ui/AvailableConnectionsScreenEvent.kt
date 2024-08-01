package dev.skybit.bluetoothchat.chat.presentation.ui

import dev.skybit.bluetoothchat.home.domain.model.BluetoothDeviceInfo
import dev.skybit.bluetoothchat.chat.presentation.ui.model.ScreenType

sealed interface AvailableConnectionsScreenEvent {
    data object StartScanning : AvailableConnectionsScreenEvent
    data object StopScanning : AvailableConnectionsScreenEvent
    data class ConnectToBluetoothDevice(val device: BluetoothDeviceInfo) : AvailableConnectionsScreenEvent
    data object DisconnectFromBluetoothDevice : AvailableConnectionsScreenEvent
    data object NavigateToDevices : AvailableConnectionsScreenEvent
    data class SetInitialScreenType(val screenType: ScreenType) : AvailableConnectionsScreenEvent
    data class SendMessage(val message: String) : AvailableConnectionsScreenEvent
    data object StartIncomingConnection : AvailableConnectionsScreenEvent
}
