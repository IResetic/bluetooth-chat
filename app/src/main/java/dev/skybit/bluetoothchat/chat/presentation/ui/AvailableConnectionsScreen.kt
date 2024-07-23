package dev.skybit.bluetoothchat.chat.presentation.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.Modifier
import androidx.hilt.navigation.compose.hiltViewModel
import dev.skybit.bluetoothchat.chat.presentation.ui.AvailableConnectionsScreenEvent.ConnectToBluetoothDevice
import dev.skybit.bluetoothchat.chat.presentation.ui.AvailableConnectionsScreenEvent.NavigateToDevices
import dev.skybit.bluetoothchat.chat.presentation.ui.AvailableConnectionsScreenEvent.SendMessage
import dev.skybit.bluetoothchat.chat.presentation.ui.AvailableConnectionsScreenEvent.SetInitialScreenType
import dev.skybit.bluetoothchat.chat.presentation.ui.AvailableConnectionsScreenEvent.StartIncomingConnection
import dev.skybit.bluetoothchat.chat.presentation.ui.components.ChatTopAppBar
import dev.skybit.bluetoothchat.chat.presentation.ui.components.ScanFloatingActionButton
import dev.skybit.bluetoothchat.chat.presentation.ui.model.ScreenType
import dev.skybit.bluetoothchat.chat.presentation.ui.screens.DevicesScreen
import dev.skybit.bluetoothchat.chat.presentation.ui.screens.MessagesScreen

@Composable
fun AvailableConnectionsScreen(
    // navigateToNewChat: (BluetoothDeviceInfo) -> Unit,
    screenType: ScreenType,
    navigateBack: () -> Unit
) {
    val viewModel = hiltViewModel<AvailableConnectionsScreenViewModel>()
    val uiState = viewModel.state.collectAsState()

    LaunchedEffect(key1 = Unit) {
        viewModel.onEvent(SetInitialScreenType(screenType))
    }

/*    LaunchedEffect(key1 = uiState.value) {
        uiState.value.startNewChat?.let {
            if (uiState.value.isConnected) {
                navigateToNewChat(it)
            }
        }
    }*/

    Scaffold(
        modifier = Modifier.background(color = MaterialTheme.colorScheme.onPrimaryContainer),
        topBar = {
                 ChatTopAppBar(
                     screenType = uiState.value.screenType,
                     name = "Unknown name", // TODO Update later,
                     navigateBack = {
                         if (uiState.value.screenType == ScreenType.DEVICES) {
                             navigateBack()
                         } else {
                             viewModel.onEvent(NavigateToDevices)
                         }
                     },
                     startConnection = { viewModel.onEvent(StartIncomingConnection) }
                 )
/*            TopAppBar(
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
            )*/
        },
        floatingActionButton = {
            if (uiState.value.screenType == ScreenType.DEVICES) {
                ScanFloatingActionButton(
                    isScanning = uiState.value.isSceningDevices,
                    isEnabled = !uiState.value.isConnecting,
                    onClick = viewModel::onEvent
                )
            }
        }
    ) { paddingValues ->
        Box(modifier = Modifier.padding(paddingValues)) {

            when (uiState.value.screenType) {
                ScreenType.DEVICES -> {
                    DevicesScreen(
                        pairedDevices = uiState.value.pairedDevices,
                        scannedDevices = uiState.value.scannedDevices,
                        isConnecting = uiState.value.isConnecting,
                        connectToDevice = { viewModel.onEvent(ConnectToBluetoothDevice(it)) }
                    )
                }
                ScreenType.MESSAGES -> {
                    MessagesScreen(
                        state = uiState.value,
                        onSendMessage = {
                            viewModel.onEvent(SendMessage(it))
                        }
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
    }*/
}
