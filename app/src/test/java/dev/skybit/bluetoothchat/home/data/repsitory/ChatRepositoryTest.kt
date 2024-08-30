package dev.skybit.bluetoothchat.home.data.repsitory

import androidx.paging.PagingSource
import androidx.paging.PagingState
import androidx.paging.testing.asSnapshot
import dev.skybit.bluetoothchat.home.data.db.dao.ChatDao
import dev.skybit.bluetoothchat.home.data.db.dao.MessagesDao
import dev.skybit.bluetoothchat.home.data.db.model.ChatEntity
import dev.skybit.bluetoothchat.home.data.db.model.MessageEntity
import dev.skybit.bluetoothchat.home.data.repository.ChatRepositoryImpl
import dev.skybit.bluetoothchat.home.domain.repository.ChatRepository
import io.mockk.coEvery
import io.mockk.every
import io.mockk.mockk
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.test.UnconfinedTestDispatcher
import kotlinx.coroutines.test.resetMain
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Before
import org.junit.Test

@OptIn(ExperimentalCoroutinesApi::class)
class ChatRepositoryTest {
    private lateinit var sut: ChatRepository
    private lateinit var mockMessagesDao: MessagesDao
    private lateinit var mockChatDao: ChatDao

    private val testDispatcher = UnconfinedTestDispatcher()

    @Before
    fun setUp() {
        mockChatDao = mockk()
        mockMessagesDao = mockk()

        sut = ChatRepositoryImpl(
            messagesDao = mockMessagesDao,
            chatDao = mockChatDao,
            testDispatcher
        )
    }

    @After
    fun tearDown() {
        Dispatchers.resetMain()
    }

    @Test
    fun `should successfully get paged chat messages using chatId`() = runBlocking {
        // define test data
        val pagingSource = object : PagingSource<Int, MessageEntity>() {
            override suspend fun load(params: LoadParams<Int>): LoadResult<Int, MessageEntity> {
                return LoadResult.Page(
                    data = listOf(messageEntityOne),
                    prevKey = null,
                    nextKey = null
                )
            }

            override fun getRefreshKey(state: PagingState<Int, MessageEntity>): Int? {
                return null
            }
        }

        every { mockMessagesDao.getMessagesByChatId("1") } returns pagingSource

        val chatId = "1"
        val expected = listOf(messageEntityOne)

        // trigger action
        val result = sut.getChatMessagesPaged(chatId).asSnapshot().map {
            MessageEntity.fromDomain(it, chatId)
        }

        // check assertions
        assertEquals(expected, result)
    }

    @Test
    fun `should successfully get all chats`() = runBlocking {
        // define test data
        val mockChatEntities = listOf(chatEntityOne, chatEntityTwo)
        coEvery { mockChatDao.getAllChats2() } returns mockChatEntities

        // trigger action
        val result = sut.getAllChats()

        // check assertions
        val expected = mockChatEntities.map { it.toDomain() }
        assertEquals(expected, result)
    }

    companion object {
        private val messageEntityOne = MessageEntity(
            id = "1",
            chatId = "1",
            senderName = "Sender Name One",
            text = "Message One",
            isFromMe = false,
            timestamp = "123456789"
        )

        private val chatEntityOne = ChatEntity(
            chatId = "1",
            senderName = "Sender Name One",
            lastMessage = "Last Message One"
        )

        private val chatEntityTwo = ChatEntity(
            chatId = "2",
            senderName = "Sender Name Two",
            lastMessage = "Last Message TWo"
        )
    }
}
