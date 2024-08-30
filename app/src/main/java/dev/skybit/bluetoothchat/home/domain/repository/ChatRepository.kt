package dev.skybit.bluetoothchat.home.domain.repository

import androidx.paging.PagingData
import dev.skybit.bluetoothchat.home.domain.model.BluetoothMessage
import dev.skybit.bluetoothchat.home.domain.model.ChatInfo
import kotlinx.coroutines.flow.Flow

interface ChatRepository {
    fun getChatMessagesPaged(chatId: String): Flow<PagingData<BluetoothMessage>>

    suspend fun getAllChats(): List<ChatInfo>

    suspend fun createNewChat(chatInfo: ChatInfo)

    suspend fun saveMessage(message: BluetoothMessage)
}
