package dev.skybit.bluetoothchat.home.presentation.ui
/*

import app.cash.turbine.test
import dev.skybit.bluetoothchat.home.domain.controller.BluetoothController
import dev.skybit.bluetoothchat.home.domain.model.BluetoothDeviceInfo
import dev.skybit.bluetoothchat.home.domain.model.ConnectionResult
import io.mockk.every
import io.mockk.mockk
import io.mockk.verify
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.test.UnconfinedTestDispatcher
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.setMain
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test

class AvailableConnectionsScreenViewModelTest {

    private val testDispatcher = UnconfinedTestDispatcher()
    private lateinit var mockBluetoothController: BluetoothController
    private lateinit var sut: AvailableConnectionsScreenViewModel

    private val mockScannedDevices = MutableStateFlow<List<BluetoothDeviceInfo>>(emptyList())
    private val mockPairedDevices = MutableStateFlow<List<BluetoothDeviceInfo>>(emptyList())

    @Before
    fun setUp() {
        Dispatchers.setMain(testDispatcher)
        mockBluetoothController = mockk(relaxed = true)
    }

    @After
    fun tearDown() {
        Dispatchers.resetMain()
    }

    @Test
    fun `initialization of view model should start scan and pair devices listener`() = runBlocking {
        // define test data
        val pairDevices = listOf(bluetoothDeviceOne)
        val scanDevices = listOf(bluetoothDeviceTwo, bluetoothDeviceThree)

        every { mockBluetoothController.scannedDevices } returns mockScannedDevices
        every { mockBluetoothController.pairedDevices } returns mockPairedDevices

        mockPairedDevices.emit(pairDevices)
        mockScannedDevices.emit(scanDevices)

        // trigger action
        sut = AvailableConnectionsScreenViewModel(mockBluetoothController)

        testDispatcher.scheduler.advanceUntilIdle()

        // check assertion
        sut.state.test {
            val result = awaitItem()
            assertEquals(pairDevices, result.pairedDevices)
            assertEquals(scanDevices, result.scannedDevices)
        }
    }

    @Test
    fun `should start scanning for bluetooth devices`() = runBlocking {
        // define test data
        sut = AvailableConnectionsScreenViewModel(mockBluetoothController)

        // trigger action
        sut.onEvent(AvailableConnectionsScreenEvent.StartScanning)

        // check assertion
        verify { mockBluetoothController.startDiscovery() }
        sut.state.test {
            val result = awaitItem()
            assertTrue(result.isSceningDevices)
        }
    }

    @Test
    fun `should stop scanning for bluetooth devices`() = runBlocking {
        // define test data
        sut = AvailableConnectionsScreenViewModel(mockBluetoothController)

        // trigger action
        sut.onEvent(AvailableConnectionsScreenEvent.StopScanning)

        // check assertion
        verify { mockBluetoothController.stopDiscovery() }
        sut.state.test {
            val result = awaitItem()
            assertFalse(result.isSceningDevices)
        }
    }

    @Test
    fun `should connect to bluetooth device`() = runBlocking {
        // define test data
        val connectionResultFlow = MutableSharedFlow<ConnectionResult>()
        every { mockBluetoothController.connectToDevice(bluetoothDeviceOne) } returns connectionResultFlow

        // init sut
        sut = AvailableConnectionsScreenViewModel(mockBluetoothController)

        // trigger action
        sut.onEvent(AvailableConnectionsScreenEvent.ConnectToBluetoothDevice(bluetoothDeviceOne))

        // check assertion
        sut.state.test {
            var result = awaitItem()
            assertTrue(result.isConnecting)

            connectionResultFlow.emit(ConnectionResult.ConnectionEstablished)
            result = awaitItem()
            assertTrue(result.isConnected)
        }
    }

    @Test
    fun `should handle connection error when connecting to bluetooth device`() = runBlocking {
        // define test data
        val device = bluetoothDeviceOne
        val connectionResultFlow = MutableSharedFlow<ConnectionResult>()
        every { mockBluetoothController.connectToDevice(device) } returns connectionResultFlow

        // init sut
        sut = AvailableConnectionsScreenViewModel(mockBluetoothController)

        // trigger action
        sut.onEvent(AvailableConnectionsScreenEvent.ConnectToBluetoothDevice(device))

        // check assertion
        sut.state.test {
            var result = awaitItem()
            assertTrue(result.isConnecting)

            val errorMessage = "Connection error"
            connectionResultFlow.emit(ConnectionResult.Error(errorMessage))
            result = awaitItem()
            assertFalse(result.isConnected)
            assertFalse(result.isConnecting)
            assertEquals(errorMessage, result.errorMessage)
        }
    }

    @Test
    fun `should disconnect from bluetooth device`() = runBlocking {
        // init sut
        sut = AvailableConnectionsScreenViewModel(mockBluetoothController)

        // trigger action
        sut.onEvent(AvailableConnectionsScreenEvent.DisconnectFromBluetoothDevice)

        // check assertion
        verify { mockBluetoothController.closeConnection() }
        sut.state.test {
            val result = awaitItem()
            assertFalse(result.isConnecting)
            assertFalse(result.isConnected)
        }
    }

    @Test
    fun `should start incoming connection listener on initialization`() = runBlocking {
        // define test data
        val connectionResultFlow = MutableSharedFlow<ConnectionResult>()
        every { mockBluetoothController.startBluetoothServer() } returns connectionResultFlow

        // init sut
        sut = AvailableConnectionsScreenViewModel(mockBluetoothController)

        // check assertion
        verify { mockBluetoothController.startBluetoothServer() }
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
    }
}
*/
