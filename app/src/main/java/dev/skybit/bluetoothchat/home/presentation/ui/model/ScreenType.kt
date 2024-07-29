package dev.skybit.bluetoothchat.home.presentation.ui.model

sealed interface ScreenType {
    data object HomeScreenType : ScreenType
    data object DevicesScreenType : ScreenType
    data class ChatScreenType(val deviceName: String) : ScreenType
}