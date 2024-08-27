package dev.skybit.bluetoothchat.chats.presentation.ui

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.paging.PagingData
import androidx.paging.cachedIn
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
import dev.skybit.bluetoothchat.chats.presentation.ui.HomeScreenEvent.ShowChat
import dev.skybit.bluetoothchat.chats.presentation.ui.model.ChatsListUiItem
import dev.skybit.bluetoothchat.chats.presentation.ui.model.ScreenType
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.flow.launchIn
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

    init {
        getAllChats()
    }

    override fun onCleared() {
        super.onCleared()
        bluetoothController.release()
    }

    fun onEvent(event: HomeScreenEvent) {
        when (event) {
            is SetConnectionAvailability -> setConnectionAvailability(event.isAvailable)

            is NavigateToDevicesScreen -> {
                stopConnectingToDevice()
                startScanAndPairDevicesListener()
                setCurrentScreenType(ScreenType.DevicesScreenType)
            }

            is ShowChat -> {
                _state.update {
                    it.copy(
                        currentScreen = ScreenType.ChatScreenType(deviceName = event.senderName),
                        chatMessageListener = setChatMessagesPagingSource(event.chatId)
                    )
                }
            }

            is NavigateBackToHomeScreen -> {
                getAllChats()
                stopConnectingToDevice()

                if (_state.value.currentScreen is ScreenType.DevicesScreenType) {
                    stopScanAndPairDevicesListener()
                }

                if (_state.value.currentScreen is ScreenType.ChatScreenType) {
                    bluetoothController.closeConnection()
                }
                setCurrentScreenType(ScreenType.ChatsListScreenType)
            }

            is SendMessage -> sendMessage(event.message)

            is ConnectToBluetoothDevice -> connectToDevice(event.device)

            is ScanForDevices -> if (event.isScanning) stopScanning() else startScanning()

            is ErrorConnectingToDevice -> stopConnectingToDevice()

            is ChatError -> stopConnectingToDevice() /*_state.update {
                it.copy(isConnected = false)
            }*/
        }
    }

    private fun getAllChats() {
        viewModelScope.launch {
            val chats = bluetoothController.getAllChats().map {
                ChatsListUiItem(it.chatId, it.senderName, it.lastMessage)
            }
            _state.update { currentState ->
                currentState.copy(
                    chatsMap = chats.associateBy { it.name }
                )
            }
        }
    }

    private fun setChatMessagesPagingSource(chatId: String): Flow<PagingData<BluetoothMessage>> =
        bluetoothController.getChatMessagesPaged(chatId)
            .cachedIn(viewModelScope)

    private fun setCurrentScreenType(screenType: ScreenType) {
        _state.update {
            it.copy(
                currentScreen = screenType,
                chatMessageListener = flowOf(PagingData.empty())
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
        Log.d("CURRENT_ERROR_MESSAGE", "Connecting to device: ${state.value.errorMessage}")
        _state.update {
            it.copy(
                isConnecting = true,
                errorMessage = null
            )
        }
        deviceConnectionJob = bluetoothController
            .connectToDevice(device)
            .listen()
    }

    private fun stopConnectingToDevice() {
        _state.update {
            it.copy(
                isConnecting = false,
                isConnected = false,
                errorMessage = null
            )
        }
        deviceConnectionJob?.cancel()
        deviceConnectionJob = null
    }

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

    private fun sendMessage(message: String) {
        viewModelScope.launch {
            if (message.trim().isNotEmpty()) {
                bluetoothController.trySendMessage(message)
            }
        }
    }

    private fun Flow<ConnectionResult>.listen(): Job {
        return onEach { result ->
            when (result) {
                is ConnectionResult.ConnectionEstablished -> {
                    _state.update {
                        it.copy(
                            currentScreen = ScreenType.ChatScreenType(deviceName = result.senderName),
                            isAvailableForConnection = false,
                            isConnected = true,
                            isConnecting = false,
                            chatMessageListener = setChatMessagesPagingSource(result.chatId)
                        )
                    }
                }
                is ConnectionResult.Error -> {
                    _state.update {
                        it.copy(
                            isAvailableForConnection = false,
                            isConnecting = false,
                            errorMessage = result.message,
                            isConnected = false
                        )
                    }
                }

                is ConnectionResult.TransferSucceeded -> { }
            }
        }.catch { _ ->
            bluetoothController.closeConnection()
            _state.update {
                it.copy(
                    isAvailableForConnection = false,
                    errorMessage = "Unknown error",
                    isConnecting = false,
                    isConnected = false
                )
            }
        }.launchIn(viewModelScope)
    }
}
