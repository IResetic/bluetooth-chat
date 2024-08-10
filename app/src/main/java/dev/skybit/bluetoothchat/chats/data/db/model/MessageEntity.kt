package dev.skybit.bluetoothchat.chats.data.db.model

import androidx.room.Entity
import androidx.room.PrimaryKey
import dev.skybit.bluetoothchat.chats.domain.model.BluetoothMessage

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
        deviceAddress = chatId,
        senderName = senderName,
        message = text,
        isFromLocalUser = isFromMe,
        sendTimeAndDate = timestamp
    )

    companion object {
        fun fromDomain(bluetoothMessage: BluetoothMessage, chatId: String) = MessageEntity(
            id = bluetoothMessage.id,
            //chatId = bluetoothMessage.deviceAddress,
            chatId = chatId,
            senderName = bluetoothMessage.senderName,
            text = bluetoothMessage.message,
            isFromMe = bluetoothMessage.isFromLocalUser,
            timestamp = System.currentTimeMillis().toString()
        )
    }
}
