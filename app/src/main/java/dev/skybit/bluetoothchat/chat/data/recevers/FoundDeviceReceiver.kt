package dev.skybit.bluetoothchat.chat.data.recevers

import android.annotation.SuppressLint
import android.bluetooth.BluetoothDevice
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import dev.skybit.bluetoothchat.core.presentation.utils.BuildVersionProvider

class FoundDeviceReceiver(
    private val buildVersionProvider: BuildVersionProvider,
    private val onDeviceFound: (BluetoothDevice) -> Unit
) : BroadcastReceiver() {

    @SuppressLint("NewApi")
    override fun onReceive(context: Context?, intent: Intent?) {
        when (intent?.action) {
            BluetoothDevice.ACTION_FOUND -> {
                val device = if (buildVersionProvider.isTiramisuAndAbove()) {
                    intent.getParcelableExtra(BluetoothDevice.EXTRA_DEVICE, BluetoothDevice::class.java)
                } else {
                    intent.getParcelableExtra(BluetoothDevice.EXTRA_DEVICE)
                }

                device?.let { onDeviceFound(it) }
            }
        }
    }
}
