package dev.skybit.bluetoothchat.chats.data.db.model

import androidx.room.Entity
import androidx.room.PrimaryKey
import dev.skybit.bluetoothchat.chats.domain.model.ChatInfo

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
}
