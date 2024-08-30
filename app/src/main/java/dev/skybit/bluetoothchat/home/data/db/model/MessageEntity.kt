package dev.skybit.bluetoothchat.home.data.db.model

import androidx.room.Entity
import androidx.room.PrimaryKey
import dev.skybit.bluetoothchat.home.domain.model.BluetoothMessage

@Entity(tableName = "chat_messages")
data class MessageEntity(
    @PrimaryKey(autoGenerate = false)
    val id: String,
    val chatId: String,
    val senderName: String,
    val text: String,
    val isFromMe: Boolean,
    val timestamp: String
) {
    fun toDomain() = BluetoothMessage(
        id = id,
        chatId = chatId,
        senderName = senderName,
        message = text,
        isFromLocalUser = isFromMe,
        sendTimeAndDate = timestamp
    )

    companion object {
        fun fromDomain(bluetoothMessage: BluetoothMessage) = MessageEntity(
            id = bluetoothMessage.id,
            chatId = bluetoothMessage.chatId,
            senderName = bluetoothMessage.senderName,
            text = bluetoothMessage.message,
            isFromMe = bluetoothMessage.isFromLocalUser,
            timestamp = bluetoothMessage.sendTimeAndDate
        )
    }
}
