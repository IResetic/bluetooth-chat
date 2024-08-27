package dev.skybit.bluetoothchat.chats.presentation.ui.model

sealed interface ScreenType {
    data object ChatsListScreenType : ScreenType
    data object DevicesScreenType : ScreenType
    data class ChatScreenType(val deviceName: String) : ScreenType
}
