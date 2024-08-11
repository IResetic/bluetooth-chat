package dev.skybit.bluetoothchat.messages.presentation.ui

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.paging.PagingData
import androidx.paging.cachedIn
import androidx.paging.compose.LazyPagingItems
import androidx.paging.compose.collectAsLazyPagingItems
import dagger.hilt.android.lifecycle.HiltViewModel
import dev.skybit.bluetoothchat.chats.domain.controller.BluetoothController
import dev.skybit.bluetoothchat.chats.domain.model.BluetoothMessage
import dev.skybit.bluetoothchat.chats.domain.model.ConnectionResult
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class MessagesScreenViewModel @Inject constructor(
    private val bluetoothController: BluetoothController,
    savedStateHandle: SavedStateHandle,
) : ViewModel() {
    private var deviceConnectionJob: Job? = null

    private var chatId: String = savedStateHandle.get<String>("chatId") ?: ""
    private val senderName: String = savedStateHandle.get<String>("senderName") ?: ""

    private val _state = MutableStateFlow(MessagesScreenUiState())
    val state: StateFlow<MessagesScreenUiState> = _state

    init {
        _state.update {
            it.copy(senderName = senderName, chatId = chatId)
        }

        if(chatId == " ") {
            startIncomingConnectionListener()
        }
    }

    val chatMessagesPagingSource: Flow<PagingData<BluetoothMessage>> =
        bluetoothController.getChatMessagesPaged(chatId).cachedIn(viewModelScope).distinctUntilChanged()

    fun onEvent(event: MessagesEvent) {
        when (event) {
            is MessagesEvent.SendMessage -> sendMessage(event.message)
        }
    }


    private fun setChatMessagesPagingSource(chatId: String): Flow<PagingData<BluetoothMessage>> {
        return  bluetoothController.getChatMessagesPaged(chatId).cachedIn(viewModelScope).distinctUntilChanged()
    }

    private fun sendMessage(message: String) {
        viewModelScope.launch {
            bluetoothController.trySendMessage(message)
        }
    }

    private fun startIncomingConnectionListener() {
        deviceConnectionJob = bluetoothController
            .startBluetoothServer()
            .listen()
    }

    override fun onCleared() {
        super.onCleared()
        bluetoothController.closeConnection()
    }

    private fun Flow<ConnectionResult>.listen(): Job {
        return onEach { result ->
            when (result) {
                is ConnectionResult.ConnectionEstablished -> {
                    _state.update {
                        it.copy(
                            senderName = result.senderName,
                            chatId = result.chatId,
                            chatMessagesFlow = setChatMessagesPagingSource(result.chatId)
                        )
                    }
                }
                is ConnectionResult.Error -> {
                    _state.update {
                        it.copy(
                            // chatId = " ",
                        )
                    }
                }

                is ConnectionResult.TransferSucceeded -> {
                    _state.update {
                        it.copy(
                        )
                    }
                }
            }
        }.catch { _ ->
            bluetoothController.closeConnection()
            _state.update {
                it.copy(
                    // chatId = " ",
                )
            }
        }.launchIn(viewModelScope)
    }
}
