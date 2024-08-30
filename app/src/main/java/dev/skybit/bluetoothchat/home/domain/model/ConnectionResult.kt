package dev.skybit.bluetoothchat.home.domain.model

sealed interface ConnectionResult {
    data class ConnectionEstablished(val chatInfo: ChatInfo) : ConnectionResult

    data class TransferSucceeded(val message: BluetoothMessage, val chatId: String) : ConnectionResult

    data class Error(val message: String) : ConnectionResult
}
