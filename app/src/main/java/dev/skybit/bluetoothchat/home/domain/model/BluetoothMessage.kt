package dev.skybit.bluetoothchat.home.domain.model

data class BluetoothMessage(
    val id: String,
    val deviceAddress: String,
    val message: String,
    val senderName: String,
    val sendTimeAndDate: String,
    val isFromLocalUser: Boolean
)