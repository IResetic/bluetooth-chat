@file:OptIn(ExperimentalFoundationApi::class)

package dev.skybit.bluetoothchat.chats.presentation.ui

import android.annotation.SuppressLint
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.consumeWindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.Button
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import dev.skybit.bluetoothchat.R
import dev.skybit.bluetoothchat.chats.presentation.ui.HomeScreenEvent.ChatError
import dev.skybit.bluetoothchat.chats.presentation.ui.HomeScreenEvent.ConnectToBluetoothDevice
import dev.skybit.bluetoothchat.chats.presentation.ui.HomeScreenEvent.ErrorConnectingToDevice
import dev.skybit.bluetoothchat.chats.presentation.ui.HomeScreenEvent.NavigateBackToHomeScreen
import dev.skybit.bluetoothchat.chats.presentation.ui.HomeScreenEvent.NavigateToDevicesScreen
import dev.skybit.bluetoothchat.chats.presentation.ui.HomeScreenEvent.ScanForDevices
import dev.skybit.bluetoothchat.chats.presentation.ui.HomeScreenEvent.SendMessage
import dev.skybit.bluetoothchat.chats.presentation.ui.HomeScreenEvent.SetConnectionAvailability
import dev.skybit.bluetoothchat.chats.presentation.ui.HomeScreenEvent.ShowChat
import dev.skybit.bluetoothchat.chats.presentation.ui.components.HomeScreenTopAppBar
import dev.skybit.bluetoothchat.chats.presentation.ui.components.MessageListItem
import dev.skybit.bluetoothchat.chats.presentation.ui.components.ScanDevicesFloatingActionButton
import dev.skybit.bluetoothchat.chats.presentation.ui.components.WaitingForConnection
import dev.skybit.bluetoothchat.chats.presentation.ui.model.ScreenType
import dev.skybit.bluetoothchat.chats.presentation.ui.model.ScreenType.ChatScreenType
import dev.skybit.bluetoothchat.chats.presentation.ui.model.ScreenType.DevicesScreenType
import dev.skybit.bluetoothchat.chats.presentation.ui.screens.ChatScreen
import dev.skybit.bluetoothchat.chats.presentation.ui.screens.DevicesScreen
import dev.skybit.bluetoothchat.core.presentation.constants.spacing__2x
import dev.skybit.bluetoothchat.core.presentation.constants.spacing__4x
import dev.skybit.bluetoothchat.core.presentation.constants.spacing__8x

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
            if (uiState.currentScreen == ScreenType.HomeScreenType && !uiState.isAvailableForConnection) {
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
                is ScreenType.HomeScreenType -> {
                    if (uiState.isAvailableForConnection) {
                        Box(
                            modifier = Modifier
                                .fillMaxSize()
                                .padding(spacing__4x),
                            contentAlignment = Alignment.TopCenter
                        ) {
                            WaitingForConnection {
                                viewModel.onEvent(SetConnectionAvailability(false))
                            }
                        }
                    } else {
                        LazyColumn(
                            modifier = Modifier.fillMaxSize()
                        ) {
                            stickyHeader {
                                Surface(
                                    shadowElevation = spacing__2x
                                ) {
                                    Box(
                                        modifier = Modifier
                                            .fillMaxWidth()
                                            .padding(top = spacing__4x, bottom = spacing__8x),
                                        contentAlignment = Alignment.Center
                                    ) {
                                        Button(
                                            modifier = Modifier.align(Alignment.Center),
                                            onClick = {
                                                viewModel.onEvent(SetConnectionAvailability(true))
                                            }
                                        ) {
                                            Text(
                                                text = stringResource(id = R.string.available_to_connect_button_title),
                                                modifier = Modifier.padding(spacing__2x),
                                                style = MaterialTheme.typography.titleMedium
                                            )
                                        }
                                    }
                                }
                            }
                            items(uiState.chatsMap.keys.toList()) { deviceAddress ->
                                uiState.chatsMap[deviceAddress]?.let { chat ->
                                    MessageListItem(
                                        name = chat.name,
                                        message = chat.lastMessage,
                                        onClick = {
                                            viewModel.onEvent(
                                                ShowChat(
                                                    senderName = chat.name,
                                                    chatId = chat.chatId
                                                )
                                            )
                                        }
                                    )

                                    HorizontalDivider(
                                        color = Color.Black.copy(alpha = 0.5f),
                                        thickness = 0.3.dp
                                    )
                                }
                            }
                        }
                    }
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
