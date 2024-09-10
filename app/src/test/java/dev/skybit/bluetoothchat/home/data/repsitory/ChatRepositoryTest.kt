package dev.skybit.bluetoothchat.home.data.repsitory

import androidx.paging.PagingSource
import androidx.paging.PagingState
import androidx.paging.testing.asSnapshot
import dev.skybit.bluetoothchat.home.data.db.dao.ChatDao
import dev.skybit.bluetoothchat.home.data.db.dao.MessagesDao
import dev.skybit.bluetoothchat.home.data.db.model.ChatEntity
import dev.skybit.bluetoothchat.home.data.db.model.MessageEntity
import dev.skybit.bluetoothchat.home.data.repository.ChatRepositoryImpl
import dev.skybit.bluetoothchat.home.domain.model.ChatInfo
import dev.skybit.bluetoothchat.home.domain.repository.ChatRepository
import io.mockk.coEvery
import io.mockk.every
import io.mockk.mockk
import io.mockk.verify
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
            MessageEntity.fromDomain(it)
        }

        // check assertions
        assertEquals(expected, result)
    }

    @Test
    fun `should throw error when failing to get paged chat messages`() = runBlocking {
        // define test data
        val exception = Exception("Failed to fetch messages")
        every { mockMessagesDao.getMessagesByChatId("1") } throws exception

        // trigger action
        val chatId = "1"
        val result = runCatching {
            sut.getChatMessagesPaged(chatId).asSnapshot()
        }

        // check assertions
        assert(result.isFailure)
        assertEquals(exception.message, result.exceptionOrNull()?.message)
    }

    @Test
    fun `should successfully get all chats`() = runBlocking {
        // define test data
        val mockChatEntities = listOf(chatEntityOne, chatEntityTwo)
        coEvery { mockChatDao.getAllChats() } returns mockChatEntities

        // trigger action
        val result = sut.getAllChats()

        // check assertions
        val expected = mockChatEntities.map { it.toDomain() }
        assertEquals(expected, result)
    }

    @Test
    fun `should throw error when failing to fetch all chats`() = runBlocking {
        // define test data
        val exception = Exception("Failed to fetch chats")
        coEvery { mockChatDao.getAllChats() } throws exception

        // trigger action
        val result = runCatching { sut.getAllChats() }

        // check assertions
        assert(result.isFailure)
        assertEquals(exception, result.exceptionOrNull())
    }

    @Test
    fun `should successfully create new chat`() = runBlocking {
        // define test data
        coEvery { mockChatDao.insertOrUpdateChat(chatEntityOne) } returns Unit

        // trigger action
        sut.createNewChat(chatInfoOne)

        // check assertions
        val expectedEntry = ChatEntity.fromDomain(chatInfoOne)
        verify { mockChatDao.insertOrUpdateChat(expectedEntry) }
    }

    @Test
    fun `should throw error when failing to create new chat`() = runBlocking {
        // define test data
        val exception = Exception("Failed to create chat")
        coEvery { mockChatDao.insertOrUpdateChat(chatEntityOne) } throws exception

        // trigger action
        val result = runCatching {
            sut.createNewChat(chatInfoOne)
        }

        // check assertions
        assert(result.isFailure)
        assertEquals(exception, result.exceptionOrNull())
    }

    @Test
    fun `should successfully save message`() = runBlocking {
        // define test data
        coEvery { mockMessagesDao.insertOrUpdateMessage(messageEntityOne) } returns Unit

        // trigger action
        sut.saveMessage(messageEntityOne.toDomain())

        // check assertions
        verify { mockMessagesDao.insertOrUpdateMessage(messageEntityOne) }
    }

    @Test
    fun `should throw error when failing to save message`() = runBlocking {
        // define test data
        val exception = Exception("Failed to save message")
        coEvery { mockMessagesDao.insertOrUpdateMessage(messageEntityOne) } throws exception

        // trigger action
        val result = runCatching {
            sut.saveMessage(messageEntityOne.toDomain())
        }

        // check assertions
        assert(result.isFailure)
        assertEquals(exception, result.exceptionOrNull())
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

        private val chatInfoOne = ChatInfo(
            chatId = "1",
            senderName = "Sender Name One",
            lastMessage = "Last Message One"
        )
    }
}
