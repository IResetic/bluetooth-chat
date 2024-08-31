package dev.skybit.bluetoothchat.home.domain.model

enum class BluetoothError {
    NO_BLUETOOTH_CONNECT_PERMISSION,
    MESSAGE_NOT_SENT,
    CANNOT_CONNECT_TO_NON_PAIRED_DEVICE,
    CONNECTION_INTERRUPTED,
    NO_CONNECTION_ESTABLISHED,
    UNKNOWN_ERROR
}
