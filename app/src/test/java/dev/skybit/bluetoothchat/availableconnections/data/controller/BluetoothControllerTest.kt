package dev.skybit.bluetoothchat.availableconnections.data.controller

import android.bluetooth.BluetoothAdapter
import android.bluetooth.BluetoothDevice
import android.bluetooth.BluetoothManager
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.content.pm.PackageManager
import app.cash.turbine.test
import dev.skybit.bluetoothchat.availableconnections.data.mappers.toBluetoothDeviceInfo
import dev.skybit.bluetoothchat.availableconnections.data.recevers.FoundDeviceReceiver
import dev.skybit.bluetoothchat.availableconnections.domain.controller.BluetoothController
import dev.skybit.bluetoothchat.core.presentation.utils.BuildVersionProvider
import io.mockk.CapturingSlot
import io.mockk.every
import io.mockk.mockk
import io.mockk.slot
import io.mockk.verify
import kotlinx.coroutines.runBlocking
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test

@Suppress("DEPRECATION")
class BluetoothControllerTest {

    private lateinit var sut: BluetoothController

    @Test
    fun `should successfully paired device when permission is granted and controller is initializing`() = runBlocking {
        // define test data
        val mockDevice: BluetoothDevice = createBluetoothDevice()
        val context = mockContext(
            bondedDevices = setOf(mockDevice)
        )

        // init sut
        intSut(context)

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
        intSut(context)

        // check assertion
        sut.pairedDevices.test {
            assertTrue(awaitItem().isEmpty())
        }
    }

    @Test
    fun `should successfully discover bluetooth device and pair it if permission is granted`() = runBlocking {
        // define test data
        val slot = slot<FoundDeviceReceiver>()
        val device: BluetoothDevice = createBluetoothDevice(name = "Name_Two", "Address_Two")
        val adapter: BluetoothAdapter = mockk(relaxed = true)

        // init sut
        val context = mockContext(
            bluetoothAdapter = adapter,
            bondedDevices = setOf(device),
            slot = slot
        )
        intSut(context)

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
    fun `should not be able discover and pair bluetooth devices when scan permission is not granted`() {
        // define test data
        val context = mockContext(
            permission = PackageManager.PERMISSION_DENIED
        )

        // init sut
        intSut(context)

        // trigger action
        sut.startDiscovery()

        // assertion
        verify(exactly = 0) {
            context.registerReceiver(any(), IntentFilter(BluetoothDevice.ACTION_FOUND))
        }
    }

    @Test
    fun `should stop searching for a bluetooth devices`() {
        // define test data
        val adapter: BluetoothAdapter = mockk(relaxed = true)
        val slot = slot<FoundDeviceReceiver>()

        // init sut
        val context = mockContext(
            bluetoothAdapter = adapter,
            slot = slot
        )
        intSut(context)

        // trigger action
        sut.stopDiscovery()

        // check assertion
        verify { adapter.cancelDiscovery() }
    }

    @Test
    fun `should not be able to stop discovery of the bluetooth devices if the scan permission is not granted`() {
        // define test data
        val adapter: BluetoothAdapter = mockk(relaxed = true)
        val slot = slot<FoundDeviceReceiver>()

        // init sut
        val context = mockContext(
            bluetoothAdapter = adapter,
            permission = PackageManager.PERMISSION_DENIED,
            slot = slot
        )
        intSut(context)

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
        intSut(context)

        // trigger action
        sut.startDiscovery()
        sut.release()

        val mockIntent = setMockFoundDeviceIntent(device)
        slot.captured.onReceive(context, mockIntent)

        verify(exactly = 1) { context.unregisterReceiver(any()) }
    }

    @Test
    fun `should successfully register FoundDeviceReceiver`() {
        // define test data
        val slot = slot<FoundDeviceReceiver>()
        val adapter: BluetoothAdapter = mockk(relaxed = true)

        // init sut
        val context = mockContext(
            bluetoothAdapter = adapter,
            slot = slot
        )
        intSut(context)
    }

    @Test
    fun `should successfully update scanned devices without pairing if permission is granted`() = runBlocking {
        // define test data
        val slot = slot<FoundDeviceReceiver>()
        val deviceOne: BluetoothDevice = createBluetoothDevice(name = "Name_Two", "Address_Two")
        val adapter: BluetoothAdapter = mockk(relaxed = true)

        // init sut
        val context = mockContext(
            bluetoothAdapter = adapter,
            bondedDevices = emptySet(),
            slot = slot
        )
        intSut(context)

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
        val adapter: BluetoothAdapter = mockk(relaxed = true)

        // init sut
        val context = mockContext(
            bluetoothAdapter = adapter,
            bondedDevices = setOf(testDevice),
            slot = slot
        )
        intSut(context)

        // trigger action
        sut.startDiscovery()
        val mockIntent = setMockFoundDeviceIntent(testDevice)
        slot.captured.onReceive(context, mockIntent)

        // assertion
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

    private fun intSut(context: Context = mockContext()) {
        val buildVersionProvider = createBuildVersionProvider()
        sut = BluetoothControllerImpl(context, buildVersionProvider)
    }

    private fun createBuildVersionProvider(): BuildVersionProvider {
        return mockk<BuildVersionProvider> {
            every { this@mockk.isTiramisuAndAbove() } returns true
        }
    }

    private fun createBluetoothDevice(name: String = "Name", address: String = "Address"): BluetoothDevice {
        return mockk<BluetoothDevice> {
            every { this@mockk.name } returns name
            every { this@mockk.address } returns address
        }
    }

    private fun setMockFoundDeviceIntent(device: BluetoothDevice): Intent {
        return mockk<Intent> {
            every { this@mockk.toString() } returns BluetoothDevice.ACTION_FOUND
            every { this@mockk.action } returns BluetoothDevice.ACTION_FOUND
            every { this@mockk.getParcelableExtra<BluetoothDevice>(BluetoothDevice.EXTRA_NAME) } returns device
            every {
                this@mockk.getParcelableExtra(BluetoothDevice.EXTRA_NAME, BluetoothDevice::class.java)
            } returns device
        }
    }

    private fun mockContext(
        bluetoothManager: BluetoothManager = mockk(relaxed = true),
        bluetoothAdapter: BluetoothAdapter = mockk(relaxed = true),
        permission: Int = PackageManager.PERMISSION_GRANTED,
        bondedDevices: Set<BluetoothDevice> = setOf(createBluetoothDevice()),
        isStartDiscoverySuccessful: Boolean = true,
        slot: CapturingSlot<FoundDeviceReceiver> = slot<FoundDeviceReceiver>()
    ): Context {
        return mockk<Context> {
            every { getSystemService(BluetoothManager::class.java) } returns bluetoothManager
            every { bluetoothManager.adapter } returns bluetoothAdapter
            every { bluetoothAdapter.bondedDevices } returns bondedDevices
            every { bluetoothAdapter.startDiscovery() } returns isStartDiscoverySuccessful
            every { registerReceiver(capture(slot), any<IntentFilter>()) } returns mockk(relaxed = true)
            every { checkSelfPermission(any()) } returns permission
            every { unregisterReceiver(any()) } returns Unit
        }
    }
}
