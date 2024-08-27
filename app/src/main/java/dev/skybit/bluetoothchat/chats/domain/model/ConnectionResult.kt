package dev.skybit.bluetoothchat.chats.domain.model

sealed interface ConnectionResult {
    data class ConnectionEstablished(val senderName: String, val chatId: String) : ConnectionResult

    data object TransferSucceeded : ConnectionResult

    data class Error(val message: String) : ConnectionResult
}
