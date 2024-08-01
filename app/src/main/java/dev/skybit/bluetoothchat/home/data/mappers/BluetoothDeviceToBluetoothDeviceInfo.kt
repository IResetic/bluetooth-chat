package dev.skybit.bluetoothchat.home.data.mappers

import android.annotation.SuppressLint
import android.bluetooth.BluetoothDevice
import dev.skybit.bluetoothchat.home.domain.model.BluetoothDeviceInfo

@SuppressLint("MissingPermission")
fun BluetoothDevice.toBluetoothDeviceInfo(): BluetoothDeviceInfo {
    return BluetoothDeviceInfo(
        name = name,
        address = address
    )
}
