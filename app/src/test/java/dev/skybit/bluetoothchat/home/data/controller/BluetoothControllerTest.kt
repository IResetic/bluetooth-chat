package dev.skybit.bluetoothchat.home.data.controller

import android.bluetooth.BluetoothAdapter
import android.bluetooth.BluetoothDevice
import android.bluetooth.BluetoothManager
import android.bluetooth.BluetoothServerSocket
import android.bluetooth.BluetoothSocket
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.content.pm.PackageManager
import app.cash.turbine.test
import dev.skybit.bluetoothchat.core.presentation.utils.BuildVersionProvider
import dev.skybit.bluetoothchat.home.data.controller.BluetoothControllerImpl.Companion.SERVICE_UUID
import dev.skybit.bluetoothchat.home.data.mappers.toBluetoothDeviceInfo
import dev.skybit.bluetoothchat.home.data.recevers.FoundDeviceReceiver
import dev.skybit.bluetoothchat.home.data.service.BluetoothDataTransferServiceFactory
import dev.skybit.bluetoothchat.home.domain.controller.BluetoothController
import dev.skybit.bluetoothchat.home.domain.model.BluetoothDeviceInfo
import dev.skybit.bluetoothchat.home.domain.model.BluetoothError
import dev.skybit.bluetoothchat.home.domain.model.ChatInfo
import dev.skybit.bluetoothchat.home.domain.model.ConnectionResult
import dev.skybit.bluetoothchat.home.domain.model.ConnectionResult.ConnectionEstablished
import io.mockk.CapturingSlot
import io.mockk.every
import io.mockk.mockk
import io.mockk.mockkConstructor
import io.mockk.slot
import io.mockk.verify
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.cancel
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.test.UnconfinedTestDispatcher
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.setMain
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test
import java.io.IOException
import java.util.UUID

@OptIn(ExperimentalCoroutinesApi::class)
class BluetoothControllerTest {
    private val testDispatcher = UnconfinedTestDispatcher()

    private lateinit var sut: BluetoothController

    @After
    fun tearDown() {
        Dispatchers.resetMain()
        testDispatcher.cancel()
    }

    @Before
    fun setUp() {
        Dispatchers.setMain(testDispatcher)
    }

    @Test
    fun `should successfully paired device when permission is granted and controller is initializing`() = runBlocking {
        // define test data
        val mockDevice: BluetoothDevice = createBluetoothDevice()
        val context = mockContext(
            bondedDevices = setOf(mockDevice)
        )

        // init sut
        initSut(context)

        // check assertion
        sut.pairedDevices.test {
            val result = awaitItem()
            val expected = listOf(mockDevice.toBluetoothDeviceInfo())
            assertEquals(expected, result)
        }
    }

    @Test
    fun `should not paired device when controller is initializing and permission is not granted`() = runBlocking {
        // define test data
        val mockDevice: BluetoothDevice = createBluetoothDevice()
        val context = mockContext(
            permission = PackageManager.PERMISSION_DENIED,
            bondedDevices = setOf(mockDevice)
        )

        // init sut
        initSut(context)

        // check assertion
        sut.pairedDevices.test {
            assertTrue(awaitItem().isEmpty())
        }
    }

    @Test
    fun `should successfully register FoundDeviceReceiver`() {
        // define test data
        val slot = slot<FoundDeviceReceiver>()
        val adapter: BluetoothAdapter = mockk()

        // init sut
        val context = mockContext(
            bluetoothAdapter = adapter,
            slot = slot
        )
        initSut(context)

        // trigger action
        sut.startDiscovery()

        // check assertion
        verify { context.registerReceiver(capture(slot), any<IntentFilter>()) }
    }

    @Test
    fun `should successfully stop searching for a bluetooth devices`() {
        // define test data
        val adapter: BluetoothAdapter = setBluetoothAdapter()
        val slot = slot<FoundDeviceReceiver>()

        // init sut
        val context = mockContext(
            bluetoothAdapter = adapter,
            slot = slot
        )
        initSut(context)

        // trigger action
        sut.stopDiscovery()

        // check assertion
        verify { adapter.cancelDiscovery() }
    }

    @Test
    fun `should not be able to stop discovery of the bluetooth devices if the scan permission is not granted`() {
        // define test data
        val adapter: BluetoothAdapter = mockk()
        val slot = slot<FoundDeviceReceiver>()

        // init sut
        val context = mockContext(
            bluetoothAdapter = adapter,
            permission = PackageManager.PERMISSION_DENIED,
            slot = slot
        )
        initSut(context)

        // trigger action
        sut.stopDiscovery()

        // check assertion
        verify(exactly = 0) { adapter.cancelDiscovery() }
    }

    @Test
    fun `should unregister found bluetooth devices`() {
        // define test data
        val slot = slot<FoundDeviceReceiver>()
        val device: BluetoothDevice = createBluetoothDevice(name = "Name_Two")

        // init sut
        val context = mockContext(
            bondedDevices = setOf(device),
            slot = slot
        )
        initSut(context)

        // trigger action
        sut.startDiscovery()
        sut.release()

        val mockIntent = setMockFoundDeviceIntent(device)
        slot.captured.onReceive(context, mockIntent)

        verify(exactly = 2) { context.unregisterReceiver(any()) }
    }

    @Test
    fun `should close connection`() = runBlocking {
        // Define test data
        val mockClientSocket: BluetoothSocket = mockk(relaxed = true)
        val mockServerSocket: BluetoothServerSocket = mockk(relaxed = true)

        val bluetoothAdapter = setBluetoothAdapter(mockClientSocket, mockServerSocket)

        val context = mockContext(bluetoothAdapter = bluetoothAdapter)

        // init sut
        initSut(context)

        // trigger action
        sut.startBluetoothServer().first()
        sut.connectToDevice(BluetoothDeviceInfo("Name", "Address")).first()

        sut.closeConnection()

        // check assertion
        verify { mockClientSocket.close() }
        verify { mockServerSocket.close() }
    }

    @Test
    fun `should successfully update scanned devices without pairing if permission is granted`() = runBlocking {
        // define test data
        val slot = slot<FoundDeviceReceiver>()
        val deviceOne: BluetoothDevice = createBluetoothDevice(name = "Name_Two", "Address_Two")
        val adapter: BluetoothAdapter = mockk()

        // init sut
        val context = mockContext(
            bluetoothAdapter = adapter,
            bondedDevices = emptySet(),
            slot = slot
        )
        initSut(context)

        // trigger action
        sut.startDiscovery()
        val mockIntent = setMockFoundDeviceIntent(deviceOne)
        slot.captured.onReceive(context, mockIntent)

        // assertion
        verify { adapter.startDiscovery() }
        sut.scannedDevices.test {
            val result = awaitItem()
            val expected = listOf(deviceOne.toBluetoothDeviceInfo())

            assertEquals(expected, result)
        }
    }

    @Test
    fun `should successfully update scanned device and pair with it if permission is granted`() = runBlocking {
        // define test data
        val slot = slot<FoundDeviceReceiver>()
        val testDevice: BluetoothDevice = createBluetoothDevice(name = "Name_Two", "Address_Two")
        val adapter: BluetoothAdapter = mockk()

        // init sut
        val context = mockContext(
            bluetoothAdapter = adapter,
            bondedDevices = setOf(testDevice),
            slot = slot
        )
        initSut(context)

        // trigger action
        sut.startDiscovery()
        val mockIntent = setMockFoundDeviceIntent(testDevice)
        slot.captured.onReceive(context, mockIntent)

        // check assertion
        verify { adapter.startDiscovery() }
        sut.scannedDevices.test {
            val result = awaitItem()
            val expected = listOf(testDevice.toBluetoothDeviceInfo())

            assertEquals(expected, result)
        }
        sut.pairedDevices.test {
            val result = awaitItem()
            val expected = listOf(testDevice.toBluetoothDeviceInfo())

            assertEquals(expected, result)
        }
    }

    @Test
    fun `should start Bluetooth server and emit ConnectionEstablished`() = runBlocking {
        // define test data
        val clientSocket = setClientSocket()
        val bluetoothAdapter: BluetoothAdapter = setBluetoothAdapter(mockClientSocket = clientSocket)

        // Init sut
        val context = mockContext(bluetoothAdapter = bluetoothAdapter)
        initSut(context = context)

        // trigger action
        val flow = sut.startBluetoothServer()

        // check assertion
        flow.test {
            val expected = ChatInfo(
                chatId = ADDRESS_ONE,
                senderName = NAME_ONE,
                lastMessage = ""
            )
            assertEquals(ConnectionEstablished(expected), awaitItem())
            cancelAndIgnoreRemainingEvents()
        }
    }

    @Test
    fun `should connect to device and emit ConnectionEstablished`() = runBlocking {
        // define test data
        val device = BluetoothDeviceInfo(NAME_ONE, ADDRESS_ONE)
        val chatInfo = ChatInfo(chatId = device.address, senderName = device.name!!, lastMessage = "")
        val mockClientSocket: BluetoothSocket = mockk(relaxed = true)

        val bluetoothAdapter = setBluetoothAdapter(mockClientSocket = mockClientSocket, device = device)

        // init sut
        val context = mockContext(bluetoothAdapter = bluetoothAdapter)
        initSut(context)

        // trigger action
        val flow = sut.connectToDevice(device)

        // check assertion
        flow.test {
            assertEquals(ConnectionEstablished(chatInfo), awaitItem())
            cancelAndIgnoreRemainingEvents()
        }
        verify { mockClientSocket.connect() }
    }

    @Test
    fun `should emit Error when connect to device throws IOException`() = runBlocking {
        // define test data
        val device = BluetoothDeviceInfo(NAME_ONE, ADDRESS_ONE)
        val mockClientSocket: BluetoothSocket = mockk(relaxed = true) {
            every { connect() } throws IOException()
        }

        val bluetoothAdapter = setBluetoothAdapter(
            mockClientSocket = mockClientSocket,
            device = device
        )

        // init sut
        val context = mockContext(bluetoothAdapter = bluetoothAdapter)
        initSut(context)

        // trigger action
        val flow = sut.connectToDevice(device)

        // check assertion
        flow.test {
            assertEquals(ConnectionResult.Error(BluetoothError.CONNECTION_INTERRUPTED), awaitItem())
            cancelAndIgnoreRemainingEvents()
        }
        verify { mockClientSocket.close() }
    }

    private fun initSut(context: Context = mockContext()) {
        val buildVersionProvider = createBuildVersionProvider()
        val bluetoothDataTransferServiceFactory = createBluetoothDataTransferServiceFactory()

        sut = BluetoothControllerImpl(
            context,
            bluetoothDataTransferServiceFactory,
            buildVersionProvider,
            testDispatcher
        )
    }

    private fun createBluetoothDataTransferServiceFactory(): BluetoothDataTransferServiceFactory {
        return mockk<BluetoothDataTransferServiceFactory>(relaxed = true)
    }

    private fun createBuildVersionProvider(): BuildVersionProvider {
        return mockk<BuildVersionProvider>() {
            every { this@mockk.isTiramisuAndAbove() } returns true
        }
    }

    private fun createBluetoothDevice(name: String = "Name", address: String = "Address"): BluetoothDevice {
        return mockk<BluetoothDevice> {
            every { this@mockk.name } returns name
            every { this@mockk.address } returns address
        }
    }

    private fun setClientSocket(name: String = "name", address: String = "address"): BluetoothSocket {
        return mockk<BluetoothSocket>(relaxed = true) {
            every { remoteDevice.name } returns name
            every { remoteDevice.address } returns address
        }
    }

    private fun setBluetoothAdapter(
        mockClientSocket: BluetoothSocket = mockk(relaxed = true),
        mockServerSocket: BluetoothServerSocket = mockk(relaxed = true),
        device: BluetoothDeviceInfo = testBluetoothDeviceOne
    ): BluetoothAdapter {
        val bluetoothDevice: BluetoothDevice = mockk(relaxed = true) {
            every {
                createRfcommSocketToServiceRecord(UUID.fromString(SERVICE_UUID))
            } returns mockClientSocket
        }

        every { mockClientSocket.remoteDevice.name } returns device.name
        every { mockClientSocket.remoteDevice.address } returns device.address
        every { mockServerSocket.accept() } returns mockClientSocket

        return mockk(relaxed = true) {
            every { getRemoteDevice(device.address) } returns bluetoothDevice
            every {
                listenUsingRfcommWithServiceRecord(
                    "bluetooth_chat_service",
                    UUID.fromString(SERVICE_UUID)
                )
            } returns mockServerSocket
            every { getRemoteDevice(device.address) } returns bluetoothDevice
        }
    }

    private fun mockContext(
        bluetoothManager: BluetoothManager = mockk(),
        bluetoothAdapter: BluetoothAdapter = mockk(),
        permission: Int = PackageManager.PERMISSION_GRANTED,
        bondedDevices: Set<BluetoothDevice> = setOf(createBluetoothDevice()),
        isStartDiscoverySuccessful: Boolean = true,
        slot: CapturingSlot<FoundDeviceReceiver> = slot<FoundDeviceReceiver>()
    ): Context {
        mockkConstructor(IntentFilter::class)
        every {
            anyConstructed<IntentFilter>().addAction(any())
        } returns Unit

        return mockk<Context>(relaxed = true) {
            every { getSystemService(BluetoothManager::class.java) } returns bluetoothManager
            every { bluetoothManager.adapter } returns bluetoothAdapter
            every { bluetoothAdapter.bondedDevices } returns bondedDevices
            every { bluetoothAdapter.startDiscovery() } returns isStartDiscoverySuccessful
            every { registerReceiver(capture(slot), any<IntentFilter>()) } returns mockk(relaxed = true)
            every { checkSelfPermission(any()) } returns permission
            every { unregisterReceiver(any()) } returns Unit
        }
    }

    private fun setMockFoundDeviceIntent(device: BluetoothDevice): Intent {
        return mockk<Intent> {
            every { this@mockk.toString() } returns BluetoothDevice.ACTION_FOUND
            every { this@mockk.action } returns BluetoothDevice.ACTION_FOUND
            every { this@mockk.getParcelableExtra<BluetoothDevice>(BluetoothDevice.EXTRA_DEVICE) } returns device
            every {
                this@mockk.getParcelableExtra(BluetoothDevice.EXTRA_DEVICE, BluetoothDevice::class.java)
            } returns device
        }
    }

    companion object {
        private const val NAME_ONE = "NameOne"
        private const val ADDRESS_ONE = "AddressOne"
        private val testBluetoothDeviceOne = BluetoothDeviceInfo(NAME_ONE, ADDRESS_ONE)
    }
}

/*
    @Test
    fun should successfully discover bluetooth device and pair it if permission is granted() = runBlocking {
        // define test data
        val slot = slot<FoundDeviceReceiver>()
        val device: BluetoothDevice = createBluetoothDevice(name = NAME_ONE, ADDRESS_ONE)
        val adapter: BluetoothAdapter = setBluetoothAdapter()

        // init sut
        val context = mockContext(
            bluetoothAdapter = adapter,
            bondedDevices = setOf(device),
            slot = slot
        )
        initSut(context)

        // trigger action
        sut.startDiscovery()
        val mockIntent = setMockFoundDeviceIntent(device)
        slot.captured.onReceive(context, mockIntent)

        // assertion
        verify { adapter.startDiscovery() }
        sut.pairedDevices.test {
            val result = awaitItem()
            val expected = listOf(device.toBluetoothDeviceInfo())
            assertEquals(expected, result)
        }
    }

    @Test
    fun should not be able discover and pair bluetooth devices when scan permission is not granted() {
        // define test data
        val adapter: BluetoothAdapter = mockk(relaxed = true)
        val context = mockContext(
            bluetoothAdapter = adapter,
            permission = PackageManager.PERMISSION_DENIED
        )

        // init sut
        initSut(context)

        // trigger action
        sut.startDiscovery()

        // check assertion
        verify(exactly = 0) {
            context.registerReceiver(any(), IntentFilter(BluetoothDevice.ACTION_FOUND))
        }
        verify(exactly = 0) { adapter.startDiscovery() }
    }
 */
