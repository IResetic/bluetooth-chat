package dev.skybit.bluetoothchat.chats.presentation.ui

import android.util.Log
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.paging.PagingData
import androidx.paging.cachedIn
import androidx.paging.compose.collectAsLazyPagingItems
import androidx.paging.map
import dagger.hilt.android.lifecycle.HiltViewModel
import dev.skybit.bluetoothchat.chats.domain.controller.BluetoothController
import dev.skybit.bluetoothchat.chats.domain.model.BluetoothDeviceInfo
import dev.skybit.bluetoothchat.chats.domain.model.BluetoothMessage
import dev.skybit.bluetoothchat.chats.domain.model.ConnectionResult
import dev.skybit.bluetoothchat.chats.presentation.ui.HomeScreenEvent.ChatError
import dev.skybit.bluetoothchat.chats.presentation.ui.HomeScreenEvent.ConnectToBluetoothDevice
import dev.skybit.bluetoothchat.chats.presentation.ui.HomeScreenEvent.ErrorConnectingToDevice
import dev.skybit.bluetoothchat.chats.presentation.ui.HomeScreenEvent.NavigateBackToHomeScreen
import dev.skybit.bluetoothchat.chats.presentation.ui.HomeScreenEvent.NavigateToDevicesScreen
import dev.skybit.bluetoothchat.chats.presentation.ui.HomeScreenEvent.ScanForDevices
import dev.skybit.bluetoothchat.chats.presentation.ui.HomeScreenEvent.SendMessage
import dev.skybit.bluetoothchat.chats.presentation.ui.HomeScreenEvent.SetConnectionAvailability
import dev.skybit.bluetoothchat.chats.presentation.ui.HomeScreenEvent.StartChat
import dev.skybit.bluetoothchat.chats.presentation.ui.model.ChatsListUiItem
import dev.skybit.bluetoothchat.chats.presentation.ui.model.ScreenType
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.emptyFlow
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class HomeScreenViewModel @Inject constructor(
    private val bluetoothController: BluetoothController
) : ViewModel() {
    private var deviceConnectionJob: Job? = null
    private var devicesScanneingJob: Job? = null

    private val _state = MutableStateFlow(HomeScreenUiState())
    val state: StateFlow<HomeScreenUiState> = _state.asStateFlow()

    /*
        private val _pagingDataFlow = MutableStateFlow<Flow<PagingData<MyData>>?>(null)
    val pagingDataFlow: StateFlow<Flow<PagingData<MyData>>?> get() = _pagingDataFlow

     */

    //var chatMessagesPagingSource: Flow<PagingData<BluetoothMessage>> = bluetoothController.getChatMessagesPaged("02:00:00:00:00:00").cachedIn(viewModelScope)
    var chatMessagesPagingSource: MutableStateFlow<Flow<PagingData<BluetoothMessage>>?> = MutableStateFlow(null)

    /*
        val chatMessagesPagingSource: Flow<PagingData<ChatMessageUi>> = getChatMessages(CHAT_ID).map { pagingData ->
        pagingData.map { chatMessage ->
            val uiMessage = chatMessageToChatMessageUiMapper(chatMessage)
            updateSections(uiMessage)
            uiMessage
        }
    }.cachedIn(viewModelScope)

     */

    override fun onCleared() {
        super.onCleared()
        bluetoothController.release()
    }

    fun onEvent(event: HomeScreenEvent) {
        when (event) {
            is SetConnectionAvailability -> setConnectionAvailability(event.isAvailable)

            is NavigateToDevicesScreen -> {
                setCurrentScreenType(ScreenType.DevicesScreenType)
                startScanAndPairDevicesListener()
            }

            is NavigateBackToHomeScreen -> {
                bluetoothController.closeConnection()
                stopScanAndPairDevicesListener()
                stopConnectingToDevice()
                setCurrentScreenType(ScreenType.HomeScreenType)
            }

            is SendMessage -> sendMessage(event.message)

            is ConnectToBluetoothDevice -> connectToDevice(event.device)

            is ScanForDevices -> if (event.isScanning) stopScanning() else startScanning()

            is ErrorConnectingToDevice -> stopConnectingToDevice()

            is ChatError -> {
                _state.update {
                    it.copy(
                        isConnectionChannelClosed = false
                    )
                }
            }

            is StartChat -> startChat(event.chatId)
        }
    }

    private fun setCurrentScreenType(screenType: ScreenType) {
        _state.update {
            it.copy(
                currentScreen = screenType
            )
        }
    }

    private fun startScanAndPairDevicesListener() {
        devicesScanneingJob = viewModelScope.launch {
            combine(
                bluetoothController.scannedDevices,
                bluetoothController.pairedDevices,
                _state
            ) { scannedDevices, pairedDevices, state ->
                state.copy(
                    scannedDevices = scannedDevices,
                    pairedDevices = pairedDevices
                    // TODO Check if this is needed. This is here to clear message hisory for a new connection
                    // messages = if (state.isConnectedWithOtherDevice) state. else emptyList()
                )
            }.stateIn(viewModelScope, SharingStarted.Lazily, _state.value).collect { newState ->
                _state.update {
                    it.copy(
                        scannedDevices = newState.scannedDevices,
                        pairedDevices = newState.pairedDevices
                    )
                }
            }
        }
    }

    private fun stopScanAndPairDevicesListener() {
        devicesScanneingJob?.cancel()
        devicesScanneingJob = null
    }

    private fun startIncomingConnectionListener() {
        deviceConnectionJob = bluetoothController
            .startBluetoothServer()
            .listen()
    }

    private fun connectToDevice(device: BluetoothDeviceInfo) {
        _state.update { it.copy(isConnecting = true) }
        deviceConnectionJob = bluetoothController
            .connectToDevice(device)
            .listen()
    }

    private fun stopConnectingToDevice() {
        _state.update {
            it.copy(
                isConnecting = false,
                errorMessage = null
            )
        }
        deviceConnectionJob?.cancel()
        deviceConnectionJob = null
    }

/*    private fun updateChats(bluetoothMessage: BluetoothMessage): Map<String, ChatsListUiItem> {
        val map = _state.value.chatsMap.toMutableMap()
        map[bluetoothMessage.deviceAddress] = ChatsListUiItem(
            name = bluetoothMessage.senderName,
            lastMessage = bluetoothMessage.message
        )

        return map
    }*/

    private fun startScanning() {
        bluetoothController.startDiscovery()
    }

    private fun stopScanning() {
        bluetoothController.stopDiscovery()
    }

    private fun setConnectionAvailability(isAvailable: Boolean) {
        if (isAvailable) {
            startIncomingConnectionListener()
        } else {
            bluetoothController.closeServerConnection()
        }

        _state.update {
            it.copy(
                isAvailableForConnection = isAvailable
            )
        }
    }

    private fun startChat(chatId: String) {
        /*
                _pagingDataFlow.value = myRepository.getPagingData().cachedIn(viewModelScope)

         */

        chatMessagesPagingSource.value = bluetoothController.getChatMessagesPaged(chatId).cachedIn(viewModelScope)


        // chatMessagesPagingSource.value = bluetoothController.getChatMessagesPaged(chatId).stateIn(viewModelScope)
        //chatMessagesPagingSource = bluetoothController.getChatMessagesPaged(chatId).cachedIn(viewModelScope)
/*        _state.update {
            it.copy(
                chatMessageListener = bluetoothController.getChatMessagesPaged(chatId).cachedIn(viewModelScope)
            )
        }*/
    }

    private fun sendMessage(message: String) {
        viewModelScope.launch {
            val bluetoothMessage = bluetoothController.trySendMessage(message)

            if (bluetoothMessage != null) {
                _state.update {
                    it.copy(
                        messages = it.messages + bluetoothMessage
                        // chatsMap = updateChats(bluetoothMessage)
                    )
                }
            }
        }
    }

    private fun Flow<ConnectionResult>.listen(): Job {
        return onEach { result ->
            when (result) {
                is ConnectionResult.ConnectionEstablished -> {
                    // chatMessagesPagingSource = bluetoothController.getChatMessagesPaged(result.chatId).cachedIn(viewModelScope)
                    _state.update {
                        it.copy(
                            // currentScreen = ScreenType.ChatScreenType(deviceName = result.senderName),
                            senderName = result.senderName,
                            chatId = result.chatId,
                            isAvailableForConnection = false,
                            isConnectionChannelClosed = false
                        )
                    }
                }
                is ConnectionResult.Error -> {
                    _state.update {
                        it.copy(
                            // chatMessageListener = emptyFlow(),
                            chatId = "",
                            isAvailableForConnection = false,
                            isConnecting = false,
                            errorMessage = result.message,
                            isConnectionChannelClosed = true
                        )
                    }
                }

                is ConnectionResult.TransferSucceeded -> {
                    _state.update {
                        it.copy(
                            // messages = it.messages + result.bluetoothMessage
                            // chatsMap = updateChats(result.bluetoothMessage)
                        )
                    }
                }
            }
        }.catch { _ ->
            bluetoothController.closeConnection()
            _state.update {
                it.copy(
                    // chatMessageListener = emptyFlow(),
                    chatId = "",
                    isAvailableForConnection = false,
                    isConnecting = false,
                    isConnectionChannelClosed = true
                )
            }
        }.launchIn(viewModelScope)
    }
}
