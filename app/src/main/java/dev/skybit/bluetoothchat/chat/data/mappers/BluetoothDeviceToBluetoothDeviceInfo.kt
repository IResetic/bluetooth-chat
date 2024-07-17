package dev.skybit.bluetoothchat.chat.data.mappers

import android.annotation.SuppressLint
import android.bluetooth.BluetoothDevice
import dev.skybit.bluetoothchat.chat.domain.model.BluetoothDeviceInfo

@SuppressLint("MissingPermission")
fun BluetoothDevice.toBluetoothDeviceInfo(): BluetoothDeviceInfo {
    return BluetoothDeviceInfo(
        name = name,
        address = address
    )
}
