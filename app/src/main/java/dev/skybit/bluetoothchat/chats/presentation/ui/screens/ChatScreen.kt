@file:OptIn(ExperimentalMaterial3Api::class)

package dev.skybit.bluetoothchat.chats.presentation.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.Send
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.unit.sp
import androidx.paging.PagingData
import androidx.paging.compose.collectAsLazyPagingItems
import dev.skybit.bluetoothchat.R
import dev.skybit.bluetoothchat.chats.domain.model.BluetoothMessage
import dev.skybit.bluetoothchat.chats.presentation.ui.components.ChatMessage
import dev.skybit.bluetoothchat.chats.presentation.ui.components.LostConnectionDialog
import dev.skybit.bluetoothchat.core.presentation.constants.elevation__2x
import dev.skybit.bluetoothchat.core.presentation.constants.spacing__2_5x
import dev.skybit.bluetoothchat.core.presentation.constants.spacing__2x
import dev.skybit.bluetoothchat.core.presentation.constants.spacing__4x
import kotlinx.coroutines.flow.Flow

@Composable
fun ChatScreen(
    chatMessagesFlow: Flow<PagingData<BluetoothMessage>>,
    isConnected: Boolean,
    errorMessage: String?,
    onErrorHandler: () -> Unit,
    onSendMessage: (String) -> Unit,
    navigateBack: () -> Unit
) {
    val message = rememberSaveable { mutableStateOf("") }
    val interactionSource = remember { MutableInteractionSource() }
    val listState = rememberLazyListState()

    val chatMessages = chatMessagesFlow.collectAsLazyPagingItems()

    LaunchedEffect(chatMessages.itemCount) {
        if (listState.firstVisibleItemIndex != 0) {
            listState.scrollToItem(0)
        }
    }

    Column(
        modifier = Modifier.fillMaxSize()
    ) {
        LazyColumn(
            state = listState,
            modifier = Modifier
                .fillMaxWidth()
                .weight(1f),
            reverseLayout = true,
            contentPadding = PaddingValues(spacing__4x),
            verticalArrangement = Arrangement.spacedBy(spacing__4x)
        ) {
            items(chatMessages.itemCount) { index ->
                chatMessages[index]?.let {
                    Column(
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        ChatMessage(
                            message = it,
                            modifier = Modifier
                                .align(
                                    if (it.isFromLocalUser) Alignment.End else Alignment.Start
                                )
                        )
                    }
                }
            }
        }

        Surface(
            shadowElevation = elevation__2x
        ) {
            Row(
                modifier = Modifier
                    .background(MaterialTheme.colorScheme.onPrimary)
                    .fillMaxWidth()
                    .padding(spacing__2x),
                verticalAlignment = Alignment.CenterVertically
            ) {
                BasicTextField(
                    modifier = Modifier
                        .weight(5f)
                        .fillMaxWidth()
                        .background(MaterialTheme.colorScheme.onPrimary),

                    value = message.value,
                    onValueChange = { message.value = it },
                    // enabled = isConnectionAlive.value,
                    enabled = isConnected,
                    textStyle = TextStyle(
                        color = MaterialTheme.colorScheme.primary,
                        fontSize = 16.sp,
                        textDecoration = TextDecoration.None
                    )
                ) {
                    OutlinedTextFieldDefaults.DecorationBox(
                        value = message.value,
                        innerTextField = it,
                        singleLine = false,
                        enabled = true,
                        contentPadding = PaddingValues(spacing__2_5x),
                        placeholder = {
                            Text(
                                stringResource(id = R.string.chat_message_placeholder),
                                style = MaterialTheme.typography.bodyLarge
                            )
                        },
                        visualTransformation = VisualTransformation.None,
                        interactionSource = interactionSource,
                        colors = OutlinedTextFieldDefaults.colors(
                            unfocusedContainerColor = MaterialTheme.colorScheme.primary.copy(alpha = 0.1f),
                            focusedContainerColor = MaterialTheme.colorScheme.primary.copy(alpha = 0.1f),
                            disabledBorderColor = Color.Transparent,
                            focusedBorderColor = Color.Transparent,
                            unfocusedBorderColor = Color.Transparent
                        )
                    )
                }

                IconButton(
                    modifier = Modifier.weight(1f),
                    // enabled = isConnectionAlive.value,
                    enabled = isConnected,
                    onClick = {
                        onSendMessage(message.value)
                        message.value = ""
                    }
                ) {
                    Icon(
                        modifier = Modifier
                            .background(
                                color = MaterialTheme.colorScheme.primary.copy(alpha = if (isConnected) 1f else 0.5f),
                                shape = CircleShape
                            )
                            .padding(spacing__2x),
                        imageVector = Icons.AutoMirrored.Filled.Send,
                        contentDescription = "Send message",
                        tint = MaterialTheme.colorScheme.onPrimary
                    )
                }
            }
        }
    }

    if (errorMessage != null) {
        LostConnectionDialog(
            onDismissRequest = { onErrorHandler() },
            navigateToHomePage = { navigateBack() }
        )
    }
}
