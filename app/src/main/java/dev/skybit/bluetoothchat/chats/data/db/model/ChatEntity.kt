package dev.skybit.bluetoothchat.chats.data.db.model

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "chats")
data class ChatEntity(
    @PrimaryKey(autoGenerate = false)
    val userAddress: String,
    val senderName: String,
    val lastMessage: String,
)
