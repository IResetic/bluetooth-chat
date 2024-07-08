package dev.skybit.bluetoothchat.availableconnections.presentation.ui

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import dev.skybit.bluetoothchat.availableconnections.domain.controller.BluetoothController
import dev.skybit.bluetoothchat.availableconnections.presentation.ui.AvailableConnectionsScreenEvent.StartStopScanning
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
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

    init {
        startScanAndPairDevicesListener()
    }

    /*
    val state = combine(
        bluetoothController.scannedDevices,
        bluetoothController.pairedDevices,
        _state
    ) { scannedDevices, pairedDevices, state ->
        state.copy(
            scannedDevices = scannedDevices,
            pairedDevices = pairedDevices
        )
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), _state.value)
    */

    fun onEvent(event: AvailableConnectionsScreenEvent) {
        when (event) {
            StartStopScanning -> startOrStopScanningForDevices()
        }
    }

    private fun startOrStopScanningForDevices() {
        if (state.value.isSceningDevices) {
            bluetoothController.stopDiscovery()
            // setScanningStatus(false)
        } else {
            bluetoothController.startDiscovery()
            // setScanningStatus(true)
        }
        setScanningStatus(!state.value.isSceningDevices)
    }

    private fun setScanningStatus(isScanning: Boolean) {
        _state.update { currentState ->
            currentState.copy(
                isSceningDevices = isScanning
            )
        }
    }

    private fun startScanAndPairDevicesListener() {
        viewModelScope.launch {
            combine(
                bluetoothController.scannedDevices,
                bluetoothController.pairedDevices,
                _state
            ) { scannedDevices, pairedDevices, state ->
                state.copy(
                    scannedDevices = scannedDevices,
                    pairedDevices = pairedDevices
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
    }
}
