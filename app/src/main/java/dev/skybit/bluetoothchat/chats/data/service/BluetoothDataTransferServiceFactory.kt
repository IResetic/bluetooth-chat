package dev.skybit.bluetoothchat.chats.data.service

import android.bluetooth.BluetoothSocket
import dagger.assisted.AssistedFactory

@AssistedFactory
interface BluetoothDataTransferServiceFactory {
    fun create(bluetoothSocket: BluetoothSocket): BluetoothDataTransferService
}
