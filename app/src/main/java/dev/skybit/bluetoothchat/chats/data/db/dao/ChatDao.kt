package dev.skybit.bluetoothchat.chats.data.db.dao

import androidx.room.Dao
import androidx.room.Upsert
import dev.skybit.bluetoothchat.chats.data.db.model.ChatEntity

@Dao
interface ChatDao {

    @Upsert
    fun insertOrUpdateChat(chatMessages: ChatEntity)
}
