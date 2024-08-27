package dev.skybit.bluetoothchat.home.domain.controller

import androidx.paging.PagingData
import dev.skybit.bluetoothchat.home.domain.model.BluetoothDeviceInfo
import dev.skybit.bluetoothchat.home.domain.model.BluetoothMessage
import dev.skybit.bluetoothchat.home.domain.model.ChatInfo
import dev.skybit.bluetoothchat.home.domain.model.ConnectionResult
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.StateFlow

interface BluetoothController {
    val isConnected: StateFlow<Boolean>

    val scannedDevices: StateFlow<List<BluetoothDeviceInfo>>

    val pairedDevices: StateFlow<List<BluetoothDeviceInfo>>

    val errors: SharedFlow<String>

    fun startDiscovery()

    fun stopDiscovery()

    fun release()

    fun startBluetoothServer(): Flow<ConnectionResult>

    fun connectToDevice(device: BluetoothDeviceInfo): Flow<ConnectionResult>

    suspend fun trySendMessage(message: String): BluetoothMessage?

    fun getChatMessagesPaged(chatId: String): Flow<PagingData<BluetoothMessage>>

    suspend fun getAllChats(): List<ChatInfo>

    fun closeServerConnection()

    fun closeConnection()
}
