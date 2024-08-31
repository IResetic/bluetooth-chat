package dev.skybit.bluetoothchat.home.domain.model

sealed interface ConnectionResult {
    data class ConnectionEstablished(val chatInfo: ChatInfo) : ConnectionResult

    data class TransferSucceeded(val message: BluetoothMessage) : ConnectionResult

    data class Error(val error: BluetoothError) : ConnectionResult
}
