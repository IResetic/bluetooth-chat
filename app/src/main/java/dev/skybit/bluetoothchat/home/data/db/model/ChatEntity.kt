package dev.skybit.bluetoothchat.home.data.db.model

import androidx.room.Entity
import androidx.room.PrimaryKey
import dev.skybit.bluetoothchat.home.domain.model.ChatInfo

@Entity(tableName = "chats")
data class ChatEntity(
    @PrimaryKey(autoGenerate = false)
    val chatId: String,
    val senderName: String,
    val lastMessage: String
) {
    fun toDomain() = ChatInfo(
        chatId = chatId,
        senderName = senderName,
        lastMessage = lastMessage
    )

    companion object {
        fun fromDomain(chatInfo: ChatInfo) = ChatEntity(
            chatId = chatInfo.chatId,
            senderName = chatInfo.senderName,
            lastMessage = chatInfo.lastMessage
        )
    }
}
