package dev.skybit.bluetoothchat.home.presentation.ui

import android.util.Log
import androidx.lifecycle.ViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import dev.skybit.bluetoothchat.availableconnections.domain.controller.BluetoothController
import javax.inject.Inject

@HiltViewModel
class HomeScreenViewModel @Inject constructor(
    private val bluetoothController: BluetoothController
) : ViewModel() {

    init {
        Log.d("TEST_CHATS", "init")
    }

    override fun onCleared() {
        super.onCleared()
        Log.d("TEST_CHATS", "onCleard")
    }

    // TODO Create a separated method for checking if the device is paired.
    // TODO Create a method to check if the device is available

    // TODO Show the histor of conversations
}
