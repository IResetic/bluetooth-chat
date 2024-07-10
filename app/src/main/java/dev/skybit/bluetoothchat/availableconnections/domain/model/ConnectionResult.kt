package dev.skybit.bluetoothchat.availableconnections.domain.model

sealed interface ConnectionResult {
    data object ConnectionEstablished : ConnectionResult
    data class Error(val message: String) : ConnectionResult
}
