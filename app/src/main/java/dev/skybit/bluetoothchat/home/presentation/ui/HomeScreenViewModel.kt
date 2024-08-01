package dev.skybit.bluetoothchat.home.presentation.ui

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import dev.skybit.bluetoothchat.home.domain.controller.BluetoothController
import dev.skybit.bluetoothchat.home.domain.model.BluetoothDeviceInfo
import dev.skybit.bluetoothchat.home.domain.model.BluetoothMessage
import dev.skybit.bluetoothchat.home.domain.model.ConnectionResult
import dev.skybit.bluetoothchat.home.presentation.ui.HomeScreenEvent.ConnectToBluetoothDevice
import dev.skybit.bluetoothchat.home.presentation.ui.HomeScreenEvent.ErrorConnectingToDevice
import dev.skybit.bluetoothchat.home.presentation.ui.HomeScreenEvent.NavigateBackToHomeScreen
import dev.skybit.bluetoothchat.home.presentation.ui.HomeScreenEvent.NavigateToDevicesScreen
import dev.skybit.bluetoothchat.home.presentation.ui.HomeScreenEvent.ScanForDevices
import dev.skybit.bluetoothchat.home.presentation.ui.HomeScreenEvent.SendMessage
import dev.skybit.bluetoothchat.home.presentation.ui.HomeScreenEvent.SetConnectionAvailability
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

            is HomeScreenEvent.ChatError -> {
                _state.update {
                    it.copy(
                        isConnectionChannelClosed = false
                    )
                }
            }
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

    private fun updateChats(bluetoothMessage: BluetoothMessage): Map<String, ChatsListUiItem> {
        val map = _state.value.chatsMap.toMutableMap()
        map[bluetoothMessage.deviceAddress] = ChatsListUiItem(
            name = bluetoothMessage.senderName,
            lastMessage = bluetoothMessage.message
        )

        return map
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
                    _state.update {
                        it.copy(
                            currentScreen = ScreenType.ChatScreenType(deviceName = result.senderName),
                            isAvailableForConnection = false,
                            isConnectionChannelClosed = false
                        )
                    }
                }
                is ConnectionResult.Error -> {
                    _state.update {
                        it.copy(
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
                            messages = it.messages + result.bluetoothMessage
                            // chatsMap = updateChats(result.bluetoothMessage)
                        )
                    }
                }
            }
        }.catch { _ ->
            bluetoothController.closeConnection()
            _state.update {
                it.copy(
                    isAvailableForConnection = false,
                    isConnecting = false,
                    isConnectionChannelClosed = true
                )
            }
        }.launchIn(viewModelScope)
    }
}
