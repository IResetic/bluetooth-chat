package dev.skybit.bluetoothchat.messages.presentation

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.paging.PagingData
import androidx.paging.cachedIn
import dagger.hilt.android.lifecycle.HiltViewModel
import dev.skybit.bluetoothchat.chats.domain.controller.BluetoothController
import dev.skybit.bluetoothchat.chats.domain.model.BluetoothMessage
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class MessagesScreenViewModel @Inject constructor(
    private val bluetoothController: BluetoothController,
    savedStateHandle: SavedStateHandle,
) : ViewModel() {

    private var chatId: String = savedStateHandle.get<String>("chatId")!!

    val chatMessagesPagingSource: Flow<PagingData<BluetoothMessage>> =
        bluetoothController.getChatMessagesPaged(chatId).cachedIn(viewModelScope).distinctUntilChanged()

    fun onEvent(event: MessagesEvent) {
        when (event) {
            is MessagesEvent.SendMessage -> sendMessage(event.message)
        }
    }

    private fun sendMessage(message: String) {
        viewModelScope.launch {
            bluetoothController.trySendMessage(message)
        }
    }

    override fun onCleared() {
        super.onCleared()
        bluetoothController.closeConnection()
    }
}
