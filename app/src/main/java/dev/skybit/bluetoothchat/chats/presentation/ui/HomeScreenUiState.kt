package dev.skybit.bluetoothchat.chats.presentation.ui

import androidx.paging.PagingData
import dev.skybit.bluetoothchat.chats.domain.model.BluetoothDeviceInfo
import dev.skybit.bluetoothchat.chats.domain.model.BluetoothMessage
import dev.skybit.bluetoothchat.chats.presentation.ui.model.ChatsListUiItem
import dev.skybit.bluetoothchat.chats.presentation.ui.model.ScreenType
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.emptyFlow

data class HomeScreenUiState(
    val isAvailableForConnection: Boolean = false,
    val chatsMap: Map<String, ChatsListUiItem> = emptyMap(),
    val isConnecting: Boolean = false,
    val currentScreen: ScreenType = ScreenType.HomeScreenType,
    val scannedDevices: List<BluetoothDeviceInfo> = emptyList(),
    val pairedDevices: List<BluetoothDeviceInfo> = emptyList(),
    val messages: List<BluetoothMessage> = emptyList(),
    val errorMessage: String? = null,
    val isConnectionChannelClosed: Boolean = false, // TODO Rename to something more appropriate
    val chatMessageListener: Flow<PagingData<BluetoothMessage>> = emptyFlow(),
    val chatId: String = "",
    val senderName: String = "",
)
