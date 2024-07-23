package dev.skybit.bluetoothchat.chat.domain.model

data class BluetoothMessage(
    val id: String,
    val message: String,
    val senderName: String,
    val sendTimeAndDate: String,
    val isFromLocalUser: Boolean
)