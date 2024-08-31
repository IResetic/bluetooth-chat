package dev.skybit.bluetoothchat.home.presentation.ui

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.paging.PagingData
import androidx.paging.cachedIn
import dagger.hilt.android.lifecycle.HiltViewModel
import dev.skybit.bluetoothchat.home.domain.controller.BluetoothController
import dev.skybit.bluetoothchat.home.domain.model.BluetoothDeviceInfo
import dev.skybit.bluetoothchat.home.domain.model.BluetoothError
import dev.skybit.bluetoothchat.home.domain.model.BluetoothMessage
import dev.skybit.bluetoothchat.home.domain.model.ConnectionResult
import dev.skybit.bluetoothchat.home.domain.repository.ChatRepository
import dev.skybit.bluetoothchat.home.presentation.ui.HomeScreenEvent.ChatError
import dev.skybit.bluetoothchat.home.presentation.ui.HomeScreenEvent.ConnectToBluetoothDevice
import dev.skybit.bluetoothchat.home.presentation.ui.HomeScreenEvent.ErrorConnectingToDevice
import dev.skybit.bluetoothchat.home.presentation.ui.HomeScreenEvent.NavigateBackToHomeScreen
import dev.skybit.bluetoothchat.home.presentation.ui.HomeScreenEvent.NavigateToDevicesScreen
import dev.skybit.bluetoothchat.home.presentation.ui.HomeScreenEvent.ScanForDevices
import dev.skybit.bluetoothchat.home.presentation.ui.HomeScreenEvent.SendMessage
import dev.skybit.bluetoothchat.home.presentation.ui.HomeScreenEvent.SetConnectionAvailability
import dev.skybit.bluetoothchat.home.presentation.ui.HomeScreenEvent.ShowChat
import dev.skybit.bluetoothchat.home.presentation.ui.model.ChatsListUiItem
import dev.skybit.bluetoothchat.home.presentation.ui.model.ScreenType
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
    private val bluetoothController: BluetoothController,
    private val chatRepository: ChatRepository
) : ViewModel() {
    private var deviceConnectionJob: Job? = null
    private var devicesScanningJob: Job? = null

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

            is ChatError -> stopConnectingToDevice()
        }
    }

    private fun getAllChats() {
        viewModelScope.launch {
            val chats = chatRepository.getAllChats().map {
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
        chatRepository.getChatMessagesPaged(chatId)
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
        devicesScanningJob = viewModelScope.launch {
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
        devicesScanningJob?.cancel()
        devicesScanningJob = null
    }

    private fun startIncomingConnectionListener() {
        deviceConnectionJob = bluetoothController
            .startBluetoothServer()
            .listen()
    }

    private fun connectToDevice(device: BluetoothDeviceInfo) {
        _state.update {
            it.copy(
                isConnecting = true,
                bluetoothError = null
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
                bluetoothError = null
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
                val result = bluetoothController.trySendMessage(message)

                if (result is ConnectionResult.TransferSucceeded) {
                    chatRepository.saveMessage(result.message)
                } else {
                    // TODO emit state that message is not send
                }
            }
        }
    }

    private fun Flow<ConnectionResult>.listen(): Job {
        return onEach { result ->
            when (result) {
                is ConnectionResult.ConnectionEstablished -> {
                    chatRepository.createNewChat(result.chatInfo)

                    _state.update {
                        it.copy(
                            currentScreen = ScreenType.ChatScreenType(
                                deviceName = result.chatInfo.senderName
                            ),
                            isAvailableForConnection = false,
                            isConnected = true,
                            isConnecting = false,
                            bluetoothError = null,
                            chatMessageListener = setChatMessagesPagingSource(
                                result.chatInfo.chatId
                            )
                        )
                    }
                }
                is ConnectionResult.Error -> {
                    _state.update {
                        it.copy(
                            isAvailableForConnection = false,
                            isConnecting = false,
                            bluetoothError = result.error,
                            isConnected = false
                        )
                    }
                }

                is ConnectionResult.TransferSucceeded -> {
                    chatRepository.saveMessage(result.message)
                }
            }
        }.catch { _ ->
            bluetoothController.closeConnection()
            _state.update {
                it.copy(
                    isAvailableForConnection = false,
                    bluetoothError = BluetoothError.UNKNOWN_ERROR,
                    isConnecting = false,
                    isConnected = false
                )
            }
        }.launchIn(viewModelScope)
    }
}
