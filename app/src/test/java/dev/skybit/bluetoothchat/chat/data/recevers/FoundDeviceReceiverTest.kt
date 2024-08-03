package dev.skybit.bluetoothchat.chat.data.recevers

import android.bluetooth.BluetoothDevice
import android.content.Context
import android.content.Intent
import dev.skybit.bluetoothchat.chats.data.recevers.FoundDeviceReceiver
import dev.skybit.bluetoothchat.core.presentation.utils.BuildVersionProvider
import io.mockk.every
import io.mockk.mockk
import io.mockk.verify
import org.junit.Test

class FoundDeviceReceiverTest {
    private lateinit var mockContext: Context
    private var mockDevice: BluetoothDevice? = null
    private lateinit var sut: FoundDeviceReceiver
    private lateinit var mockOnDeviceFound: (BluetoothDevice) -> Unit

    @Test
    fun `onReceive should call onDeviceFound with device on Android TIRAMISU and above`() {
        // define test data
        val buildVersionProvider = getBuildVersionProvider(true)

        // init sut
        initSut(buildVersionProvider)
        val intent: Intent = getMockIntent(BluetoothDevice.ACTION_FOUND)

        // trigger action
        sut.onReceive(mockContext, intent)

        // check assertion
        verify { intent.getParcelableExtra(BluetoothDevice.EXTRA_NAME, BluetoothDevice::class.java) }
        verify { mockOnDeviceFound(mockDevice!!) }
    }

    @Test
    fun `onReceive should call onDeviceFound with device on Android versions below TIRAMISU`() {
        // define test data
        val buildVersionProvider = getBuildVersionProvider(false)

        // init sut
        initSut(buildVersionProvider)
        val intent: Intent = getMockIntent(BluetoothDevice.ACTION_FOUND)

        // trigger action
        sut.onReceive(mockContext, intent)

        // check assertion
        verify {
            @Suppress("DEPRECATION")
            intent.getParcelableExtra<BluetoothDevice>(BluetoothDevice.EXTRA_NAME)
        }
        verify { mockOnDeviceFound(mockDevice!!) }
    }

    @Test
    fun `onReceive should not call onDeviceFound if action is not ACTION_FOUND`() {
        // define test data
        val buildVersionProvider = getBuildVersionProvider(true)

        // init sut
        initSut(buildVersionProvider)
        val intent: Intent = getMockIntent("some_other_action")

        // trigger action
        sut.onReceive(mockContext, intent)

        // Assert
        verify(exactly = 0) { mockOnDeviceFound(any()) }
    }

    @Test
    fun `onReceive should not call onDeviceFound if device is null`() {
        // define test data
        val buildVersionProvider = getBuildVersionProvider(true)

        // init sut
        initSut(buildVersionProvider, null)
        val intent: Intent = getMockIntent(BluetoothDevice.ACTION_FOUND)

        // trigger action
        sut.onReceive(mockContext, intent)

        // check assertion
        verify(exactly = 0) { mockOnDeviceFound(any()) }
    }

    private fun initSut(buildVersionProvider: BuildVersionProvider, devices: BluetoothDevice? = mockk()) {
        mockContext = mockk()
        mockDevice = devices
        mockOnDeviceFound = mockk(relaxed = true)

        sut = FoundDeviceReceiver(buildVersionProvider, mockOnDeviceFound)
    }

    private fun getBuildVersionProvider(isTiramisuOrAbove: Boolean): BuildVersionProvider {
        return mockk<BuildVersionProvider> {
            every { this@mockk.isTiramisuAndAbove() } returns isTiramisuOrAbove
        }
    }

    private fun getMockIntent(action: String): Intent {
        return mockk<Intent> {
            every {
                @Suppress("DEPRECATION")
                this@mockk.getParcelableExtra<BluetoothDevice>(BluetoothDevice.EXTRA_NAME)
            } returns mockDevice
            every {
                this@mockk.getParcelableExtra(BluetoothDevice.EXTRA_NAME, BluetoothDevice::class.java)
            } returns mockDevice
            every { this@mockk.action } returns action
        }
    }
}
