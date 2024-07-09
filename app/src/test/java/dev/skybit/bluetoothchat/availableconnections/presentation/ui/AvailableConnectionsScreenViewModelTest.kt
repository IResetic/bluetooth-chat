@file:OptIn(ExperimentalCoroutinesApi::class)

package dev.skybit.bluetoothchat.availableconnections.presentation.ui

import app.cash.turbine.test
import dev.skybit.bluetoothchat.availableconnections.domain.controller.BluetoothController
import dev.skybit.bluetoothchat.availableconnections.domain.model.BluetoothDeviceInfo
import io.mockk.every
import io.mockk.mockk
import io.mockk.verify
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
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
