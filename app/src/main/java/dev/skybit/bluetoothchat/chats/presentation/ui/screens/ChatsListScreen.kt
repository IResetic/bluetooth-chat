@file:OptIn(ExperimentalFoundationApi::class)

package dev.skybit.bluetoothchat.chats.presentation.ui.screens

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Button
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import dev.skybit.bluetoothchat.R
import dev.skybit.bluetoothchat.chats.presentation.ui.components.MessageListItem
import dev.skybit.bluetoothchat.chats.presentation.ui.components.WaitingForConnection
import dev.skybit.bluetoothchat.chats.presentation.ui.model.ChatsListUiItem
import dev.skybit.bluetoothchat.core.presentation.constants.spacing__2x
import dev.skybit.bluetoothchat.core.presentation.constants.spacing__4x
import dev.skybit.bluetoothchat.core.presentation.constants.spacing__8x

@Composable
fun ChatsListScreen(
    isAvailableForConnection: Boolean,
    setConnectionAvailability: (Boolean) -> Unit,
    chatsMap: Map<String, ChatsListUiItem>,
    openChat: (String, String) -> Unit
) {
    if (isAvailableForConnection) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(spacing__4x),
            contentAlignment = Alignment.TopCenter
        ) {
            WaitingForConnection {
                setConnectionAvailability(false)
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
                                setConnectionAvailability(true)
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
            items(chatsMap.keys.toList()) { deviceAddress ->
                chatsMap[deviceAddress]?.let { chat ->
                    MessageListItem(
                        name = chat.name,
                        message = chat.lastMessage,
                        onClick = {
                            openChat(chat.name, chat.chatId)
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
