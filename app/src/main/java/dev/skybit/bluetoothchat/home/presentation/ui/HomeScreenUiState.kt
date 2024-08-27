package dev.skybit.bluetoothchat.home.presentation.ui

import androidx.paging.PagingData
import dev.skybit.bluetoothchat.home.domain.model.BluetoothDeviceInfo
import dev.skybit.bluetoothchat.home.domain.model.BluetoothMessage
import dev.skybit.bluetoothchat.home.presentation.ui.model.ChatsListUiItem
import dev.skybit.bluetoothchat.home.presentation.ui.model.ScreenType
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flowOf

data class HomeScreenUiState(
    val isAvailableForConnection: Boolean = false,
    val chatsMap: Map<String, ChatsListUiItem> = emptyMap(),
    val isConnecting: Boolean = false,
    val currentScreen: ScreenType = ScreenType.ChatsListScreenType,
    val scannedDevices: List<BluetoothDeviceInfo> = emptyList(),
    val pairedDevices: List<BluetoothDeviceInfo> = emptyList(),
    val errorMessage: String? = null,
    val isConnected: Boolean = false,
    val chatMessageListener: Flow<PagingData<BluetoothMessage>> = flowOf(PagingData.empty())
)
