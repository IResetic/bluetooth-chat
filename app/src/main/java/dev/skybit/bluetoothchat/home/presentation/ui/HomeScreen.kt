package dev.skybit.bluetoothchat.home.presentation.ui

import android.annotation.SuppressLint
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.consumeWindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.hilt.navigation.compose.hiltViewModel
import dev.skybit.bluetoothchat.home.presentation.ui.HomeScreenEvent.ChatError
import dev.skybit.bluetoothchat.home.presentation.ui.HomeScreenEvent.ConnectToBluetoothDevice
import dev.skybit.bluetoothchat.home.presentation.ui.HomeScreenEvent.ErrorConnectingToDevice
import dev.skybit.bluetoothchat.home.presentation.ui.HomeScreenEvent.NavigateBackToHomeScreen
import dev.skybit.bluetoothchat.home.presentation.ui.HomeScreenEvent.NavigateToDevicesScreen
import dev.skybit.bluetoothchat.home.presentation.ui.HomeScreenEvent.ScanForDevices
import dev.skybit.bluetoothchat.home.presentation.ui.HomeScreenEvent.SendMessage
import dev.skybit.bluetoothchat.home.presentation.ui.HomeScreenEvent.SetConnectionAvailability
import dev.skybit.bluetoothchat.home.presentation.ui.HomeScreenEvent.ShowChat
import dev.skybit.bluetoothchat.home.presentation.ui.components.HomeScreenTopAppBar
import dev.skybit.bluetoothchat.home.presentation.ui.components.ScanDevicesFloatingActionButton
import dev.skybit.bluetoothchat.home.presentation.ui.model.ScreenType
import dev.skybit.bluetoothchat.home.presentation.ui.model.ScreenType.ChatScreenType
import dev.skybit.bluetoothchat.home.presentation.ui.model.ScreenType.DevicesScreenType
import dev.skybit.bluetoothchat.home.presentation.ui.screens.ChatScreen
import dev.skybit.bluetoothchat.home.presentation.ui.screens.ChatsListScreen
import dev.skybit.bluetoothchat.home.presentation.ui.screens.DevicesScreen

@SuppressLint("UnusedMaterial3ScaffoldPaddingParameter")
@Composable
fun HomeScreen() {
    val viewModel = hiltViewModel<HomeScreenViewModel>()
    val uiState by viewModel.state.collectAsState()

    Scaffold(
        topBar = {
            HomeScreenTopAppBar(
                isConnecting = uiState.isConnecting,
                screenType = uiState.currentScreen,
                navigateBack = {
                    viewModel.onEvent(NavigateBackToHomeScreen)
                }
            )
        },
        floatingActionButton = {
            if (uiState.currentScreen == ScreenType.ChatsListScreenType && !uiState.isAvailableForConnection) {
                FloatingActionButton(
                    onClick = { viewModel.onEvent(NavigateToDevicesScreen) }
                ) {
                    Icon(imageVector = Icons.Filled.Add, contentDescription = "Add new chat")
                }
            }

            if (uiState.currentScreen == DevicesScreenType && !uiState.isConnecting) {
                ScanDevicesFloatingActionButton(
                    onClick = { viewModel.onEvent(ScanForDevices(!it)) }
                )
            }
        }
    ) { paddingValues ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .consumeWindowInsets(paddingValues)
                .imePadding()
        ) {
            when (uiState.currentScreen) {
                is ScreenType.ChatsListScreenType -> {
                    ChatsListScreen(
                        isAvailableForConnection = uiState.isAvailableForConnection,
                        setConnectionAvailability = { viewModel.onEvent(SetConnectionAvailability(it)) },
                        chatsMap = uiState.chatsMap,
                        openChat = { senderName, chatId ->
                            viewModel.onEvent(ShowChat(senderName, chatId))
                        }
                    )
                }

                is ChatScreenType -> {
                    ChatScreen(
                        chatMessagesFlow = uiState.chatMessageListener,
                        isConnected = uiState.isConnected,
                        errorMessage = uiState.errorMessage,
                        onErrorHandler = { viewModel.onEvent(ChatError) },
                        onSendMessage = { viewModel.onEvent(SendMessage(it)) },
                        navigateBack = { viewModel.onEvent(NavigateBackToHomeScreen) }
                    )
                }

                is DevicesScreenType -> {
                    DevicesScreen(
                        pairedDevices = uiState.pairedDevices,
                        scannedDevices = uiState.scannedDevices,
                        isConnecting = uiState.isConnecting,
                        errorMessage = uiState.errorMessage,
                        onErrorHandler = { viewModel.onEvent(ErrorConnectingToDevice) },
                        connectToDevice = { viewModel.onEvent(ConnectToBluetoothDevice(it)) }
                    )
                }
            }
        }
    }
}
