package dev.skybit.bluetoothchat.chat.domain.model

sealed interface ConnectionResult {
    data object ConnectionEstablished : ConnectionResult

    data class TransferSucceeded(val bluetoothMessage: BluetoothMessage) : ConnectionResult

    data class Error(val message: String) : ConnectionResult
}
