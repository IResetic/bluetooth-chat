package dev.skybit.bluetoothchat.core.presentation.utils

interface BuildVersionProvider {
    fun isTiramisuAndAbove(): Boolean

    fun isSAndAbove(): Boolean
}
