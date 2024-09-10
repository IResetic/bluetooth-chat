package dev.skybit.bluetoothchat.home.presentation.ui

import app.cash.turbine.test
import dev.skybit.bluetoothchat.home.domain.controller.BluetoothController
import dev.skybit.bluetoothchat.home.domain.model.BluetoothDeviceInfo
import dev.skybit.bluetoothchat.home.domain.model.BluetoothError
import dev.skybit.bluetoothchat.home.domain.model.BluetoothMessage
import dev.skybit.bluetoothchat.home.domain.model.ChatInfo
import dev.skybit.bluetoothchat.home.domain.model.ConnectionResult
import dev.skybit.bluetoothchat.home.domain.repository.ChatRepository
import dev.skybit.bluetoothchat.home.presentation.ui.model.ChatsListUiItem
import dev.skybit.bluetoothchat.home.presentation.ui.model.ScreenType
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.every
import io.mockk.mockk
import io.mockk.verify
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.test.UnconfinedTestDispatcher
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.setMain
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertNull
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test

@OptIn(ExperimentalCoroutinesApi::class)
class HomeScreenViewModelTest {
    private val testDispatcher = UnconfinedTestDispatcher()

    @Before
    fun setUp() {
        Dispatchers.setMain(testDispatcher)
    }

    @After
    fun tearDown() {
        Dispatchers.resetMain()
    }

    @Test
    fun `should get all chats when view model is initialized`() = runBlocking {
        // define test data
        val chats = listOf(chatInfoOne, chatInfoTwo)
        val chatRepository = setChatRepository(chats)

        // init sut
        val sut = initSut(chatRepository = chatRepository)

        // check assertion
        coVerify { chatRepository.getAllChats() }
        sut.state.test {
            val expected = chats.map { ChatsListUiItem.fromDomain(it) }
            assertEquals(expected, awaitItem().chatsMap.values.toList())
        }
    }

    @Test
    fun `should start incoming connection listener when event is SetConnectionAvailability`() = runBlocking {
        // define test data
        val bluetoothController = setBluetoothController()

        // trigger action
        val sut = initSut(bluetoothController = bluetoothController)
        sut.onEvent(HomeScreenEvent.SetConnectionAvailability(true))

        // check assertion
        verify { bluetoothController.startBluetoothServer() }
    }

    @Test
    fun `should stop incoming connection listener when event is SetConnectionAvailability`() = runBlocking {
        // define test data
        val bluetoothController = setBluetoothController()

        // trigger action
        val sut = initSut(bluetoothController = bluetoothController)
        sut.onEvent(HomeScreenEvent.SetConnectionAvailability(false))

        // check assertion
        verify { bluetoothController.closeServerConnection() }
    }

    @Test
    fun `should stop incoming connection listener when event is NavigateToDevicesScreen`() = runBlocking {
        // define test data
        val scanDevices = listOf(bluetoothDeviceOne, bluetoothDeviceTwo)
        val pairDevices = listOf(bluetoothDeviceThree)
        val bluetoothController = setBluetoothController(
            scanDevices = scanDevices,
            pairDevices = pairDevices
        )

        // init suit
        val sut = initSut(bluetoothController = bluetoothController)

        // trigger action
        sut.onEvent(HomeScreenEvent.NavigateToDevicesScreen)

        // check assertion
        sut.state.test {
            val result = awaitItem()
            assertEquals(scanDevices, result.scannedDevices)
            assertEquals(pairDevices, result.pairedDevices)
            assertEquals(ScreenType.DevicesScreenType, result.currentScreen)
        }
    }

    @Test
    fun `should set currentScreen to ChatScreenType and start message listener when even is ShowChat`() = runBlocking {
        // define test data
        val chatRepository = setChatRepository()

        // inti sut
        val sut = initSut(chatRepository = chatRepository)

        // trigger action
        sut.onEvent(HomeScreenEvent.ShowChat(chatInfoOne.senderName, chatInfoOne.chatId))

        // check assertion
        sut.state.test {
            assertEquals(
                ScreenType.ChatScreenType(chatInfoOne.senderName),
                awaitItem().currentScreen
            )
        }
        verify { chatRepository.getChatMessagesPaged(chatInfoOne.chatId) }
    }

    @Test
    fun `should set current screen to HomeScreenType and clear the state when event is NavigateBackToHomeScreen`() = runBlocking {
        // define test data
        val chats = listOf(chatInfoOne, chatInfoTwo)
        val chatRepository = setChatRepository(chats)

        // init sut
        val sut = initSut(chatRepository = chatRepository)

        // trigger action
        sut.onEvent(HomeScreenEvent.NavigateBackToHomeScreen)

        // check assertion
        sut.state.test {
            assertEquals(ScreenType.ChatsListScreenType, awaitItem().currentScreen)
        }
    }

    @Test
    fun `should successfully send a message and save it locally`() = runBlocking {
        // define test data
        val result = ConnectionResult.TransferSucceeded(bluetoothMessageOne)
        val chatRepository = setChatRepository()
        val bluetoothController = setBluetoothController(
            connectionResult = result,
            message = bluetoothMessageOne.message
        )

        // init sut
        val sut = initSut(
            bluetoothController = bluetoothController,
            chatRepository = chatRepository
        )

        // trigger action
        sut.onEvent(HomeScreenEvent.SendMessage(bluetoothMessageOne.message))

        // check assertion
        coVerify { bluetoothController.trySendMessage(bluetoothMessageOne.message) }
        coVerify { chatRepository.saveMessage(bluetoothMessageOne) }
    }

    @Test
    fun `should not save message locally if the message is not sent successfully`() {
        // define test data
        val chatRepository = setChatRepository()
        val bluetoothController = setBluetoothController(
            connectionResult = ConnectionResult.Error(BluetoothError.MESSAGE_NOT_SENT),
            message = messageOne
        )

        // init sut
        val sut = initSut(
            bluetoothController = bluetoothController,
            chatRepository = chatRepository
        )

        // trigger action
        sut.onEvent(HomeScreenEvent.SendMessage(bluetoothMessageOne.message))

        // check assertion
        coVerify { bluetoothController.trySendMessage(bluetoothMessageOne.message) }
        coVerify(exactly = 0) { chatRepository.saveMessage(bluetoothMessageOne) }
    }

    @Test
    fun `should not sent the message if the message is empty string`() {
        // define test data
        val chatRepository = setChatRepository()
        val bluetoothController = setBluetoothController(
            connectionResult = ConnectionResult.Error(BluetoothError.MESSAGE_NOT_SENT),
            message = ""
        )

        // init sut
        val sut = initSut(
            bluetoothController = bluetoothController,
            chatRepository = chatRepository
        )

        // trigger action
        sut.onEvent(HomeScreenEvent.SendMessage(""))

        // check assertion
        coVerify(exactly = 0) { bluetoothController.trySendMessage(bluetoothMessageOne.message) }
        coVerify(exactly = 0) { chatRepository.saveMessage(bluetoothMessageOne) }
    }

    @Test
    fun `should successfully connect to bluetooth device`() = runBlocking {
        // define test data
        val bluetoothController = setBluetoothController(
            connectionResult = ConnectionResult.ConnectionEstablished(chatInfoOne),
            bluetoothDevice = bluetoothDeviceOne
        )
        val repository = setChatRepository(
            chats = listOf(chatInfoOne, chatInfoTwo)
        )

        // init sut
        val sut = initSut(
            bluetoothController = bluetoothController,
            chatRepository = repository
        )

        // trigger action
        sut.onEvent(HomeScreenEvent.ConnectToBluetoothDevice(bluetoothDeviceOne))

        // check assertion
        sut.state.test {
            val result = awaitItem()
            assertEquals(ScreenType.ChatScreenType(chatInfoOne.senderName), result.currentScreen)
            assertFalse(result.isAvailableForConnection)
            assertTrue(result.isConnected)
            assertFalse(result.isConnecting)
            assertNull(result.bluetoothError)
        }
    }

    @Test
    fun `should set error message if user is not able to connect to a device`() = runBlocking {
        // define test data

        val bluetoothController = setBluetoothController(
            connectionResult = ConnectionResult.Error(BluetoothError.NO_CONNECTION_ESTABLISHED),
            bluetoothDevice = bluetoothDeviceOne
        )
        val repository = setChatRepository(
            chats = listOf(chatInfoOne, chatInfoTwo)
        )

        // init sut
        val sut = initSut(
            bluetoothController = bluetoothController,
            chatRepository = repository
        )

        // trigger action
        sut.onEvent(HomeScreenEvent.ConnectToBluetoothDevice(bluetoothDeviceOne))

        // check assertion
        sut.state.test {
            val result = awaitItem()
            assertEquals(BluetoothError.NO_CONNECTION_ESTABLISHED, result.bluetoothError)
            assertFalse(result.isAvailableForConnection)
            assertFalse(result.isConnected)
            assertFalse(result.isConnecting)
            assertEquals(BluetoothError.NO_CONNECTION_ESTABLISHED, result.bluetoothError)
        }
    }

    @Test
    fun `should start scanning for devices`() = runBlocking {
        // define test data
        val bluetoothController = setBluetoothController(
            scanDevices = listOf(bluetoothDeviceOne, bluetoothDeviceTwo)
        )

        // init sut
        val sut = initSut(bluetoothController = bluetoothController)

        // trigger action
        sut.onEvent(HomeScreenEvent.ScanForDevices(false))

        // check assertion
        verify { bluetoothController.startDiscovery() }
    }

    @Test
    fun `should stop scanning for devices`() = runBlocking {
        // define test data
        val bluetoothController = setBluetoothController(
            scanDevices = listOf(bluetoothDeviceOne, bluetoothDeviceTwo)
        )

        // init sut
        val sut = initSut(bluetoothController = bluetoothController)

        // trigger action
        sut.onEvent(HomeScreenEvent.ScanForDevices(true))

        // check assertion
        verify { bluetoothController.stopDiscovery() }
    }

    @Test
    fun `should stop connecting to a device if the other device has not started the server`() = runBlocking {
        // init sut
        val sut = initSut()

        // trigger action
        sut.onEvent(HomeScreenEvent.ErrorConnectingToDevice)

        // check assertion
        sut.state.test {
            val result = awaitItem()
            assertFalse(result.isConnecting)
            assertFalse(result.isConnected)
            assertNull(result.bluetoothError)
        }
    }

    @Test
    fun `should clear state if the other user has leave the chat`() = runBlocking {
        // init sut
        val sut = initSut()

        // trigger action
        sut.onEvent(HomeScreenEvent.ChatError)

        // check assertion
        sut.state.test {
            val result = awaitItem()
            assertFalse(result.isConnecting)
            assertFalse(result.isConnected)
            assertNull(result.bluetoothError)
        }
    }

    private fun initSut(
        bluetoothController: BluetoothController = setBluetoothController(),
        chatRepository: ChatRepository = setChatRepository()
    ): HomeScreenViewModel {
        return HomeScreenViewModel(bluetoothController, chatRepository)
    }

    private fun setBluetoothController(
        connectionResult: ConnectionResult = mockk(),
        scanDevices: List<BluetoothDeviceInfo> = emptyList(),
        pairDevices: List<BluetoothDeviceInfo> = emptyList(),
        bluetoothDevice: BluetoothDeviceInfo = mockk(),
        message: String = ""
    ): BluetoothController {
        val connectionResultFlow = flowOf(connectionResult)
        val scanDevicesFlow = MutableStateFlow(scanDevices)
        val pairDevicesFlow = MutableStateFlow(pairDevices)

        return mockk() {
            every { startBluetoothServer() } returns connectionResultFlow
            every { closeServerConnection() } returns Unit
            every { scannedDevices } returns scanDevicesFlow
            every { pairedDevices } returns pairDevicesFlow
            coEvery { trySendMessage(message) } returns connectionResult
            coEvery { connectToDevice(bluetoothDevice) } returns connectionResultFlow
            every { startDiscovery() } returns Unit
            every { stopDiscovery() } returns Unit
        }
    }

    private fun setChatRepository(chats: List<ChatInfo> = emptyList()): ChatRepository {
        return mockk {
            coEvery { getAllChats() } returns chats
            every { getChatMessagesPaged(any()) } returns mockk()
            coEvery { createNewChat(any()) } returns mockk()
        }
    }

    companion object {
        val bluetoothDeviceOne = BluetoothDeviceInfo(
            name = "Name One",
            address = "Address Two"
        )

        val bluetoothDeviceTwo = BluetoothDeviceInfo(
            name = "Name Two",
            address = "Address Two"
        )

        val bluetoothDeviceThree = BluetoothDeviceInfo(
            name = "Name Three",
            address = "Address Three"
        )

        val chatInfoOne = ChatInfo(
            chatId = "ChatIdOne",
            senderName = "SenderNameOne",
            lastMessage = "LastMessageOne"
        )

        val chatInfoTwo = ChatInfo(
            chatId = "ChatIdTwo",
            senderName = "SenderNameTwo",
            lastMessage = "LastMessageTwo"
        )

        val messageOne = "Hello world!"

        val bluetoothMessageOne = BluetoothMessage(
            id = "IdOne",
            chatId = "ChatIdOne",
            message = messageOne,
            senderName = "SenderNameOne",
            sendTimeAndDate = "TimeAndDateOne",
            isFromLocalUser = true
        )
    }
}
