package dev.skybit.bluetoothchat.chat.presentation.ui.model

import kotlinx.serialization.Serializable

@Serializable
enum class ScreenType {
    DEVICES,
    MESSAGES;

    companion object {
        fun getScreenType(code: Int): ScreenType {
            return if (code == DEVICES.ordinal) DEVICES else MESSAGES
        }
    }
}