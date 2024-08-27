package dev.skybit.bluetoothchat.home.domain.repository

import dev.skybit.bluetoothchat.home.domain.model.BluetoothMessage
import kotlinx.coroutines.flow.Flow

interface ChatRepository {
    fun getChatMessagesPaged(chatId: String): Flow<BluetoothMessage>

    suspend fun sendMessage(chatMessage: BluetoothMessage)
}
