package dev.skybit.bluetoothchat.chats.data.db.dao

import androidx.paging.PagingSource
import androidx.room.Dao
import androidx.room.Query
import androidx.room.Upsert
import dev.skybit.bluetoothchat.chats.data.db.model.MessageEntity

@Dao
interface MessagesDao {

    @Upsert
    fun insertOrUpdateMessage(message: MessageEntity)

    @Query("SELECT * FROM chat_messages WHERE chatId = :chatId ORDER BY timestamp DESC")
    fun getMessagesByChatId(chatId: String): PagingSource<Int, MessageEntity>

    @Query("SELECT * FROM chat_messages WHERE chatId = :chatId ORDER BY timestamp DESC LIMIT 1")
    fun getLastMessageByChatId(chatId: String): MessageEntity
}
