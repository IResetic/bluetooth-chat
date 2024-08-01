package dev.skybit.bluetoothchat.chats.domain.controller

import dev.skybit.bluetoothchat.chats.domain.model.BluetoothDeviceInfo
import dev.skybit.bluetoothchat.chats.domain.model.BluetoothMessage
import dev.skybit.bluetoothchat.chats.domain.model.ConnectionResult
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

    fun closeServerConnection()

    fun closeConnection()
}
