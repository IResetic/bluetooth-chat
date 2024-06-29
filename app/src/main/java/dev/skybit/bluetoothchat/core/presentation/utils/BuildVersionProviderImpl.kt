package dev.skybit.bluetoothchat.core.presentation.utils

import android.os.Build
import javax.inject.Inject

class BuildVersionProviderImpl @Inject constructor() : BuildVersionProvider {
    override fun isTiramisuAndAbove(): Boolean {
        return Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU
    }

    override fun isSAndAbove(): Boolean {
        return Build.VERSION.SDK_INT >= Build.VERSION_CODES.S
    }
}
