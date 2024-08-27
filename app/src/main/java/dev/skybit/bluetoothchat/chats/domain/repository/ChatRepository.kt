package dev.skybit.bluetoothchat.chats.domain.repository

import dev.skybit.bluetoothchat.chats.domain.model.BluetoothMessage
import kotlinx.coroutines.flow.Flow

interface ChatRepository {
    fun getChatMessagesPaged(chatId: String): Flow<BluetoothMessage>

    suspend fun sendMessage(chatMessage: BluetoothMessage)
}
