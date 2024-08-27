package dev.skybit.bluetoothchat.messages.presentation.ui

sealed class MessagesEvent {
    data class SendMessage(val message: String) : MessagesEvent()
}
