package dev.skybit.bluetoothchat.home.data.service

import android.bluetooth.BluetoothSocket
import dagger.assisted.AssistedFactory

@AssistedFactory
interface BluetoothDataTransferServiceFactory {
    fun create(bluetoothSocket: BluetoothSocket): BluetoothDataTransferService
}
