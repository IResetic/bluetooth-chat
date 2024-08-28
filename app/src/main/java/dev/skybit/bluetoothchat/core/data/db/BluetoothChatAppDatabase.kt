package dev.skybit.bluetoothchat.core.data.db

import androidx.room.Database
import androidx.room.RoomDatabase
import dev.skybit.bluetoothchat.core.data.DATABASE_VERSION
import dev.skybit.bluetoothchat.home.data.db.dao.ChatDao
import dev.skybit.bluetoothchat.home.data.db.dao.MessagesDao
import dev.skybit.bluetoothchat.home.data.db.model.ChatEntity
import dev.skybit.bluetoothchat.home.data.db.model.MessageEntity

@Database(
    entities = [ChatEntity::class, MessageEntity::class],
    version = DATABASE_VERSION
)
abstract class BluetoothChatAppDatabase : RoomDatabase() {
    abstract fun messagesDao(): MessagesDao

    abstract fun chatDao(): ChatDao
}
