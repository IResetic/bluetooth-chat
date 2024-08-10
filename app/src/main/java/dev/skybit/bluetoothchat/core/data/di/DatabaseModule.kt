package dev.skybit.bluetoothchat.core.data.di

import android.content.Context
import androidx.room.Room
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import dev.skybit.bluetoothchat.core.data.DATABASE_NAME
import dev.skybit.bluetoothchat.core.data.db.BluetoothChatAppDatabase
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object DatabaseModule {
    @Provides
    @Singleton
    fun provideDatabase(
        @ApplicationContext context: Context
    ): BluetoothChatAppDatabase {
        return Room.databaseBuilder(context, BluetoothChatAppDatabase::class.java, DATABASE_NAME)
            .build()
    }

    @Provides
    fun provideMessagesDao(database: BluetoothChatAppDatabase) = database.messagesDao()

    @Provides
    fun provideChatDao(database: BluetoothChatAppDatabase) = database.chatDao()
}