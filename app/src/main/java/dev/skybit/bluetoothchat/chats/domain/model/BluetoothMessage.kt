package dev.skybit.bluetoothchat.chats.domain.model

import androidx.compose.runtime.Immutable
import androidx.compose.runtime.Stable

@Stable
data class BluetoothMessage(
    val id: String,
    val deviceAddress: String,
    val message: String,
    val senderName: String,
    val sendTimeAndDate: String,
    val isFromLocalUser: Boolean
)
