package dev.skybit.bluetoothchat.home.data.repository

import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.PagingData
import androidx.paging.map
import dev.skybit.bluetoothchat.core.data.di.IoDispatcher
import dev.skybit.bluetoothchat.home.data.db.dao.ChatDao
import dev.skybit.bluetoothchat.home.data.db.dao.MessagesDao
import dev.skybit.bluetoothchat.home.data.db.model.ChatEntity
import dev.skybit.bluetoothchat.home.data.db.model.MessageEntity
import dev.skybit.bluetoothchat.home.domain.model.BluetoothMessage
import dev.skybit.bluetoothchat.home.domain.model.ChatInfo
import dev.skybit.bluetoothchat.home.domain.repository.ChatRepository
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.withContext
import javax.inject.Inject

class ChatRepositoryImpl @Inject constructor(
    private val messagesDao: MessagesDao,
    private val chatDao: ChatDao,
    @IoDispatcher private val ioDispatcher: CoroutineDispatcher
) : ChatRepository {
    override fun getChatMessagesPaged(chatId: String): Flow<PagingData<BluetoothMessage>> {
        return Pager(
            config = PagingConfig(
                pageSize = 20,
                initialLoadSize = 20,
                enablePlaceholders = false
            )
        ) {
            messagesDao.getMessagesByChatId(chatId)
        }.flow.map { pagingData ->
            pagingData.map { message ->
                message.toDomain()
            }
        }
    }

    override suspend fun getAllChats(): List<ChatInfo> {
        return withContext(ioDispatcher) {
            chatDao.getAllChats2().map {
                it.toDomain()
            }
        }
    }

    override suspend fun createNewChat(chatInfo: ChatInfo) {
        withContext(ioDispatcher) {
            val chatEntity = ChatEntity.fromDomain(chatInfo)

            chatDao.insertOrUpdateChat(chatEntity)
        }
    }

    override suspend fun saveMessage(message: BluetoothMessage) {
        withContext(ioDispatcher) {
            val messageEntity = MessageEntity.fromDomain(message)

            messagesDao.insertOrUpdateMessage(messageEntity)
        }
    }
}
