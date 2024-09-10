package dev.skybit.bluetoothchat.home.presentation.ui.model

import dev.skybit.bluetoothchat.home.domain.model.ChatInfo

// TODO replace usage of this mode with ChatInfo
data class ChatsListUiItem(
    val chatId: String,
    val name: String,
    val lastMessage: String
) {
    companion object {
        fun fromDomain(chatInfo: ChatInfo): ChatsListUiItem {
            return ChatsListUiItem(
                chatId = chatInfo.chatId,
                name = chatInfo.senderName,
                lastMessage = chatInfo.lastMessage
            )
        }
    }
}
