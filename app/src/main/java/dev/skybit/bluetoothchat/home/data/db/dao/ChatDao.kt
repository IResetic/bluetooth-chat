package dev.skybit.bluetoothchat.home.data.db.dao

import androidx.room.Dao
import androidx.room.Query
import androidx.room.Upsert
import dev.skybit.bluetoothchat.home.data.db.model.ChatEntity

@Dao
interface ChatDao {

    @Upsert
    fun insertOrUpdateChat(chatMessages: ChatEntity)

    @Query(
        """
        SELECT c.chatId, c.senderName, m.text AS lastMessage 
        FROM chats c 
        LEFT JOIN chat_messages m 
        ON c.chatId = m.chatId 
        WHERE m.timestamp = (
            SELECT MAX(m2.timestamp) 
            FROM chat_messages m2 
            WHERE m2.chatId = c.chatId
        )
        """
    )
    suspend fun getAllChats(): List<ChatEntity>
}
