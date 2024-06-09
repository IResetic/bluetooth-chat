package dev.skybit.bluetoothchat.availableconnections.data.controller

import dev.skybit.bluetoothchat.availableconnections.data.model.BluetoothDeviceInfo
import kotlinx.coroutines.flow.StateFlow

interface BluetoothController {
    val scannedDevices: StateFlow<List<BluetoothDeviceInfo>>
    val pairedDevices: StateFlow<List<BluetoothDeviceInfo>>

    fun startDiscovery()

    fun stopDiscovery()

    fun release()
}
