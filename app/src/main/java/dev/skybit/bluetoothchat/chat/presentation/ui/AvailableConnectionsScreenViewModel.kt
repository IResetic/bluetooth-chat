package dev.skybit.bluetoothchat.chat.presentation.ui

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import dev.skybit.bluetoothchat.chat.domain.controller.BluetoothController
import dev.skybit.bluetoothchat.chat.domain.model.BluetoothDeviceInfo
import dev.skybit.bluetoothchat.chat.domain.model.ConnectionResult
import dev.skybit.bluetoothchat.chat.presentation.ui.AvailableConnectionsScreenEvent.ConnectToBluetoothDevice
import dev.skybit.bluetoothchat.chat.presentation.ui.AvailableConnectionsScreenEvent.DisconnectFromBluetoothDevice
import dev.skybit.bluetoothchat.chat.presentation.ui.AvailableConnectionsScreenEvent.NavigateToDevices
import dev.skybit.bluetoothchat.chat.presentation.ui.AvailableConnectionsScreenEvent.SendMessage
import dev.skybit.bluetoothchat.chat.presentation.ui.AvailableConnectionsScreenEvent.SetInitialScreenType
import dev.skybit.bluetoothchat.chat.presentation.ui.AvailableConnectionsScreenEvent.StartIncomingConnection
import dev.skybit.bluetoothchat.chat.presentation.ui.AvailableConnectionsScreenEvent.StartScanning
import dev.skybit.bluetoothchat.chat.presentation.ui.AvailableConnectionsScreenEvent.StopScanning
import dev.skybit.bluetoothchat.chat.presentation.ui.model.ScreenType
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
class AvailableConnectionsScreenViewModel @Inject constructor(
    private val bluetoothController: BluetoothController
) : ViewModel() {

    private val _state = MutableStateFlow(AvailableConnectionsUiState())
    val state: StateFlow<AvailableConnectionsUiState> = _state.asStateFlow()
    private var deviceConnectionJob: Job? = null

    init {
        viewModelScope.launch {
            startScanAndPairDevicesListener()
        }

        initBluetoothConnectionStatus()
        handleBluetoothConnectionErrors()

        // startIncomingConnectionListener()
    }

    fun onEvent(event: AvailableConnectionsScreenEvent) {
        when (event) {
            is StartScanning -> startScanning()
            is StopScanning -> stopScanning()
            is ConnectToBluetoothDevice -> connectToDevice(device = event.device)
            is DisconnectFromBluetoothDevice -> disconnectFromDevice()
            is NavigateToDevices -> disconnectFromDevice()
            is SetInitialScreenType -> setScreenType(event.screenType)
            is SendMessage -> sendMessage(event.message)
            is StartIncomingConnection -> startIncomingConnectionListener()
        }
    }

    private suspend fun startScanAndPairDevicesListener() {
        combine(
            bluetoothController.scannedDevices,
            bluetoothController.pairedDevices,
            _state
        ) { scannedDevices, pairedDevices, state ->
            state.copy(
                scannedDevices = scannedDevices,
                pairedDevices = pairedDevices,
                // TODO Check if this is needed. This is here to clear message hisory for a new connection
                messages = if (state.isConnected) state.messages else emptyList() 
            )
        }.stateIn(viewModelScope, SharingStarted.Eagerly, _state.value).collect { newState ->
            _state.update {
                it.copy(
                    scannedDevices = newState.scannedDevices,
                    pairedDevices = newState.pairedDevices
                )
            }
        }
    }

    private fun initBluetoothConnectionStatus() {
        bluetoothController.isConnected.onEach { isConnected ->
            _state.update { it.copy(isConnected = isConnected) }
        }.launchIn(viewModelScope)
    }

    private fun handleBluetoothConnectionErrors() {
        bluetoothController.errors.onEach { error ->
            _state.update {
                it.copy(errorMessage = error)
            }
        }.launchIn(viewModelScope)
    }

    private fun startScanning() {
        bluetoothController.startDiscovery()
        setScanningStatus(true)
    }

    private fun stopScanning() {
        bluetoothController.stopDiscovery()
        setScanningStatus(false)
    }

    private fun setScanningStatus(isScanning: Boolean) {
        _state.update { currentState ->
            currentState.copy(
                isSceningDevices = isScanning
            )
        }
    }

    private fun connectToDevice(device: BluetoothDeviceInfo) {
        _state.update { it.copy(isConnecting = true) }
        deviceConnectionJob = bluetoothController
            .connectToDevice(device)
            .listen()
    }

    private fun disconnectFromDevice() {
        deviceConnectionJob?.cancel()
        bluetoothController.closeConnection()
        _state.update {
            it.copy(
                isConnecting = false,
                isConnected = false,
                screenType = ScreenType.DEVICES
            )
        }
    }

    private fun setScreenType(type: ScreenType) {
        _state.update {
            it.copy(screenType = type)
        }
    }

    private fun sendMessage(message: String) {
        viewModelScope.launch {
            val bluetoothMessage = bluetoothController.trySendMessage(message)

            if (bluetoothMessage != null) {
                _state.update {
                    it.copy(
                        messages = it.messages + bluetoothMessage
                    )
                }
            }
        }
    }

    private fun startIncomingConnectionListener() {
        // _state.update { it.copy(isConnecting = true) }
        deviceConnectionJob = bluetoothController
            .startBluetoothServer()
            .listen()
    }

    // TODO divide to a two separate listeners one for server and one for client
    private fun Flow<ConnectionResult>.listen(): Job {
        return onEach { result ->
            when (result) {
                is ConnectionResult.ConnectionEstablished -> {
                    _state.update {
                        it.copy(
                            isConnected = true,
                            isConnecting = false,
                            errorMessage = null,
                            screenType = if (it.screenType == ScreenType.DEVICES) {
                                ScreenType.MESSAGES
                            } else {
                                ScreenType.DEVICES
                            }
                        )
                    }
                }
                is ConnectionResult.Error -> {
                    _state.update {
                        it.copy(
                            isConnected = false,
                            isConnecting = false,
                            errorMessage = result.message,
                            startNewChat = null,
                            screenType = ScreenType.DEVICES
                        )
                    }
                }

                is ConnectionResult.TransferSucceeded -> {
                    _state.update {
                        it.copy(
                            messages = it.messages + result.bluetoothMessage
                        )
                    }
                }
            }
        }.catch { _ ->
                bluetoothController.closeConnection()
                _state.update {
                    it.copy(
                        isConnected = false,
                        isConnecting = false,
                        screenType = ScreenType.DEVICES
                    )
                }
                // startIncomingConnectionListener()
            }
            .launchIn(viewModelScope)
    }

    override fun onCleared() {
        super.onCleared()
        bluetoothController.release()
    }
}
