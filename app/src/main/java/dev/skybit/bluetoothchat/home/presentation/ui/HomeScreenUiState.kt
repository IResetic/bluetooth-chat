package dev.skybit.bluetoothchat.home.presentation.ui

import dev.skybit.bluetoothchat.home.domain.model.BluetoothDeviceInfo
import dev.skybit.bluetoothchat.home.domain.model.BluetoothMessage
import dev.skybit.bluetoothchat.home.presentation.ui.model.ChatsListUiItem
import dev.skybit.bluetoothchat.home.presentation.ui.model.ScreenType

data class HomeScreenUiState(
    val isAvailableForConnection: Boolean = false,
    val chatsMap: Map<String, ChatsListUiItem> = emptyMap(),
    val isConnecting: Boolean = false,
    val currentScreen: ScreenType = ScreenType.HomeScreenType,
    val scannedDevices: List<BluetoothDeviceInfo> = emptyList(),
    val pairedDevices: List<BluetoothDeviceInfo> = emptyList(),
    val messages: List<BluetoothMessage> = emptyList(),
    val errorMessage: String? = null,
    val isConnectionChannelClosed: Boolean = false // TODO Rename to something more appropriate
)
