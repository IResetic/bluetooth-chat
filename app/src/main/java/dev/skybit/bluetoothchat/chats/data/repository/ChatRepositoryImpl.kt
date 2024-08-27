package dev.skybit.bluetoothchat.chats.data.repository

import dev.skybit.bluetoothchat.chats.domain.controller.BluetoothController
import dev.skybit.bluetoothchat.chats.domain.model.BluetoothMessage
import dev.skybit.bluetoothchat.chats.domain.repository.ChatRepository
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class ChatRepositoryImpl @Inject constructor(
    private val bluetoothController: BluetoothController
) : ChatRepository {
    override fun getChatMessagesPaged(chatId: String): Flow<BluetoothMessage> {
        TODO("Not yet implemented")
    }

    override suspend fun sendMessage(chatMessage: BluetoothMessage) {
        TODO("Not yet implemented")
    }
}
