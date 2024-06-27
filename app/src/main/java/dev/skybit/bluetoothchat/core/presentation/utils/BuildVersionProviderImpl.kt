package dev.skybit.bluetoothchat.core.presentation.utils

import android.os.Build

object BuildVersionProviderImpl : BuildVersionProvider {
    override fun isTiramisuAndAbove(): Boolean {
        return Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU
    }
}
