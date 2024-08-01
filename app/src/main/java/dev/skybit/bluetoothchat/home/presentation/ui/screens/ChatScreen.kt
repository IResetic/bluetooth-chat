package dev.skybit.bluetoothchat.home.presentation.ui.screens

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.Send
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.unit.dp
import dev.skybit.bluetoothchat.home.domain.model.BluetoothMessage
import dev.skybit.bluetoothchat.chat.presentation.ui.components.ChatMessage
import dev.skybit.bluetoothchat.home.presentation.ui.components.ConnectionErrorDialog

@Composable
fun ChatScreen(
    messages: List<BluetoothMessage>,
    isConnectionChannelClosed: Boolean,
    onErrorHandler: () -> Unit,
    onSendMessage: (String) -> Unit
) {
    val message = rememberSaveable {
        mutableStateOf("")
    }

    val isConnectionAlive = rememberSaveable {
        mutableStateOf(true)
    }

    val keyboardController = LocalSoftwareKeyboardController.current

    Column(
        modifier = Modifier
            .fillMaxSize()
    ) {
        LazyColumn(
            modifier = Modifier
                .fillMaxWidth()
                .weight(1f),
            contentPadding = PaddingValues(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            items(messages) { message ->
                Column(
                    modifier = Modifier.fillMaxWidth()
                ) {
                    ChatMessage(
                        message = message,
                        modifier = Modifier
                            .align(
                                if(message.isFromLocalUser) Alignment.End else Alignment.Start
                            )
                    )
                }
            }
        }
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            TextField(
                enabled = isConnectionAlive.value,
                value = message.value,
                onValueChange = { message.value = it },
                modifier = Modifier.weight(1f),
                placeholder = {
                    Text(text = "Message")
                }
            )
            IconButton(
                onClick = {
                    onSendMessage(message.value)
                    message.value = ""
                    keyboardController?.hide()
                },
                enabled = isConnectionAlive.value,
            ) {
                Icon(
                    imageVector = Icons.AutoMirrored.Filled.Send,
                    contentDescription = "Send message"
                )
            }
        }
    }

    if (isConnectionChannelClosed) {
        ConnectionErrorDialog {
            isConnectionAlive.value = false
            onErrorHandler()
        }
    }
}