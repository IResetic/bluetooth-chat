package dev.skybit.bluetoothchat.chat.presentation.ui.screens.availableconnections

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.hilt.navigation.compose.hiltViewModel
import dev.skybit.bluetoothchat.R
import dev.skybit.bluetoothchat.chat.domain.model.BluetoothDeviceInfo
import dev.skybit.bluetoothchat.chat.presentation.ui.components.DevicesListItem
import dev.skybit.bluetoothchat.chat.presentation.ui.components.ScanFloatingActionButton
import dev.skybit.bluetoothchat.chat.presentation.ui.components.SectionHeader
import dev.skybit.bluetoothchat.chat.presentation.ui.screens.availableconnections.AvailableConnectionsScreenEvent.ConnectToBluetoothDevice

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AvailableConnectionsScreen(
    navigateToNewChat: (BluetoothDeviceInfo) -> Unit,
    navigateBack: () -> Unit
) {
    val viewModel = hiltViewModel<AvailableConnectionsScreenViewModel>()
    val uiState = viewModel.state.collectAsState()

    LaunchedEffect(key1 = uiState.value) {
        uiState.value.startNewChat?.let {
            if (uiState.value.isConnected) {
                navigateToNewChat(it)
            }
        }
    }

    Scaffold(
        modifier = Modifier.background(color = MaterialTheme.colorScheme.onPrimaryContainer),
        topBar = {
            TopAppBar(
                title = { Text("Bluetooth Connections") },
                navigationIcon = {
                    IconButton(onClick = { navigateBack() }) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = "Navigat back"
                        )
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors().copy(
                    containerColor = MaterialTheme.colorScheme.inversePrimary
                )
            )
        },
        floatingActionButton = {
            ScanFloatingActionButton(
                isScanning = uiState.value.isSceningDevices,
                isEnabled = !uiState.value.isConnecting,
                onClick = viewModel::onEvent
            )
        }
    ) { paddingValues ->
        LazyColumn(modifier = Modifier.padding(paddingValues)) {
            if (uiState.value.pairedDevices.isNotEmpty()) {
                item { SectionHeader(sectionHeaderId = R.string.pair_devices_header) }
            }

            items(uiState.value.pairedDevices) { device ->
                DevicesListItem(device = device, !uiState.value.isConnecting) {
                    viewModel.onEvent(ConnectToBluetoothDevice(device))
                }
            }

            if (uiState.value.scannedDevices.isNotEmpty()) {
                item { SectionHeader(sectionHeaderId = R.string.available_devices_header) }
            }

            items(uiState.value.scannedDevices) { device ->
                DevicesListItem(device = device, !uiState.value.isConnecting) {
                    viewModel.onEvent(ConnectToBluetoothDevice(device))
                }
            }
        }

        Box(
            modifier = Modifier.fillMaxSize(),
            // .padding(paddingValues),
            contentAlignment = Alignment.TopStart
        ) {
            if (uiState.value.isConnecting) {
                Box(
                    contentAlignment = Alignment.Center,
                    modifier = Modifier
                        .fillMaxSize()
                        .background(Color.Black.copy(alpha = 0.5f))

                ) {
                    CircularProgressIndicator(
                        color = MaterialTheme.colorScheme.primary
                    )
                }
            }
        }
    }

/*        Box(
            modifier = Modifier.padding(paddingValues)
        ) {
            DevicesScreen(
                pairedDevices = uiState.value.pairedDevices,
                scannedDevices = uiState.value.scannedDevices,
                isConnecting = !uiState.value.isConnecting
            ) { device ->
                viewModel.onEvent(AvailableConnectionsScreenEvent.ConnectToBluetoothDevice(device))
            }
        }
    }*/
}
