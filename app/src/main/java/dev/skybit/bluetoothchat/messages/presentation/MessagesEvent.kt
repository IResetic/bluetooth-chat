package dev.skybit.bluetoothchat.messages.presentation

sealed class MessagesEvent {
    data class SendMessage(val message: String): MessagesEvent()
}