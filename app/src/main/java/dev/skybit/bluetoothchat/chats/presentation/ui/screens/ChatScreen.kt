@file:OptIn(ExperimentalMaterial3Api::class)

package dev.skybit.bluetoothchat.chats.presentation.ui.screens

import android.widget.Toast
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
import androidx.compose.foundation.lazy.items
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
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.paging.LoadState
import androidx.paging.compose.LazyPagingItems
import androidx.paging.compose.collectAsLazyPagingItems
import dev.skybit.bluetoothchat.R
import dev.skybit.bluetoothchat.chats.domain.model.BluetoothMessage
import dev.skybit.bluetoothchat.chats.presentation.ui.HomeScreenViewModel
import dev.skybit.bluetoothchat.chats.presentation.ui.components.ChatMessage
import dev.skybit.bluetoothchat.chats.presentation.ui.components.ConnectionErrorDialog
import dev.skybit.bluetoothchat.core.presentation.constants.smallElevation
import dev.skybit.bluetoothchat.core.presentation.constants.smallPadding
import kotlinx.coroutines.launch

@Composable
fun ChatScreen(
    // chatMessages: LazyPagingItems<BluetoothMessage>,
    messages: List<BluetoothMessage>,
    isConnectionChannelClosed: Boolean,
    onErrorHandler: () -> Unit,
    onSendMessage: (String) -> Unit
) {

    val message = rememberSaveable { mutableStateOf("") }
    val isConnectionAlive = rememberSaveable { mutableStateOf(true) }
    val interactionSource = remember { MutableInteractionSource() }
    val listState = rememberLazyListState()
    val scope = rememberCoroutineScope()
    val context = LocalContext.current

    val viewModel = hiltViewModel<HomeScreenViewModel>()

    val currentPagingDataFlow by viewModel.chatMessagesPagingSource.collectAsState()

    currentPagingDataFlow?.let {
        val chatMessages = it.collectAsLazyPagingItems()

        if(chatMessages.loadState.append is LoadState.Loading) {
            Text("Append")
        }

        if (chatMessages.loadState.refresh is LoadState.Loading) {
            Text("Loading")
        } else {
            Column(
                modifier = Modifier.fillMaxSize()
            ) {
                LazyColumn(
                    state = listState,
                    modifier = Modifier
                        .fillMaxWidth()
                        .weight(1f),
                    contentPadding = PaddingValues(16.dp),
                    verticalArrangement = Arrangement.spacedBy(16.dp)
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

                            /*                        ChatMessage(
                                                        message = it,
                                                        modifier = Modifier
                                                            .align(
                                                                if (it.isFromLocalUser) Alignment.End else Alignment.Start
                                                            )
                                                    )*/
                            /*
                                                    if (it.messageType == MessageType.SENT) {
                                                        SentMessageItem(
                                                            messageId = it.id,
                                                            messageText = it.message,
                                                            timeStamp = it.formattedTimestamp,
                                                            chatMessageSection = sections[it.sectionId]
                                                        )
                                                    } else {
                                                        ReceivedMessageItem(
                                                            messageId = it.id,
                                                            messageText = it.message,
                                                            timeStamp = it.formattedTimestamp,
                                                            chatMessageSection = sections[it.sectionId]
                                                        )
                                                    }*/
                        }
                    }


                    /*                items(messages) { message ->
                                        Column(
                                            modifier = Modifier.fillMaxWidth()
                                        ) {
                                            ChatMessage(
                                                message = message,
                                                modifier = Modifier
                                                    .align(
                                                        if (message.isFromLocalUser) Alignment.End else Alignment.Start
                                                    )
                                            )
                                        }
                                    }*/
                }

                Surface(
                    shadowElevation = smallElevation
                ) {
                    Row(
                        modifier = Modifier
                            .background(MaterialTheme.colorScheme.onPrimary)
                            .fillMaxWidth()
                            .padding(smallPadding),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        BasicTextField(
                            modifier = Modifier
                                .weight(5f)
                                .fillMaxWidth()
                                .background(MaterialTheme.colorScheme.onPrimary),

                            value = message.value,
                            onValueChange = { message.value = it },
                            enabled = true,
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
                                contentPadding = PaddingValues(10.dp),
                                placeholder = {
                                    Text(
                                        stringResource(id = R.string.chat_message_placeholder),
                                        fontSize = 16.sp
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
                            onClick = {
                                scope.launch {
                                    onSendMessage(message.value)
                                    message.value = ""
                                    listState.animateScrollToItem(if (messages.isEmpty()) 0 else messages.size - 1)
                                }
                            }
                        ) {
                            Icon(
                                modifier = Modifier
                                    .background(
                                        color = MaterialTheme.colorScheme.primary,
                                        shape = CircleShape
                                    )
                                    .padding(smallPadding),
                                imageVector = Icons.AutoMirrored.Filled.Send,
                                // contentDescription = stringResource(id = R.string.send_message_button_content_description),
                                contentDescription = "Send message",
                                tint = MaterialTheme.colorScheme.onPrimary
                            )
                        }
                    }
                }
            }
        }

    }


/*    LaunchedEffect(key1 = messages.size) {
        val lastVisible = listState.layoutInfo.visibleItemsInfo.lastOrNull()?.index ?: -1
        val isLocalUser = messages.lastOrNull()?.isFromLocalUser

        if (isLocalUser == false) {
            if (messages.size - 1 - lastVisible > 1) {
                Toast.makeText(context, "New message received", Toast.LENGTH_SHORT).show()
            } else {
                listState.animateScrollToItem(messages.size - 1)
            }
        }
    }*/
/*
    if (chatMessages.loadState.refresh is LoadState.Loading) {
        Text("Loading")
    } else {
        Column(
            modifier = Modifier.fillMaxSize()
        ) {
            LazyColumn(
                state = listState,
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1f),
                contentPadding = PaddingValues(16.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp)
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

*//*                        ChatMessage(
                            message = it,
                            modifier = Modifier
                                .align(
                                    if (it.isFromLocalUser) Alignment.End else Alignment.Start
                                )
                        )*//*
*//*
                        if (it.messageType == MessageType.SENT) {
                            SentMessageItem(
                                messageId = it.id,
                                messageText = it.message,
                                timeStamp = it.formattedTimestamp,
                                chatMessageSection = sections[it.sectionId]
                            )
                        } else {
                            ReceivedMessageItem(
                                messageId = it.id,
                                messageText = it.message,
                                timeStamp = it.formattedTimestamp,
                                chatMessageSection = sections[it.sectionId]
                            )
                        }*//*
                    }
                }


*//*                items(messages) { message ->
                    Column(
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        ChatMessage(
                            message = message,
                            modifier = Modifier
                                .align(
                                    if (message.isFromLocalUser) Alignment.End else Alignment.Start
                                )
                        )
                    }
                }*//*
            }

            Surface(
                shadowElevation = smallElevation
            ) {
                Row(
                    modifier = Modifier
                        .background(MaterialTheme.colorScheme.onPrimary)
                        .fillMaxWidth()
                        .padding(smallPadding),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    BasicTextField(
                        modifier = Modifier
                            .weight(5f)
                            .fillMaxWidth()
                            .background(MaterialTheme.colorScheme.onPrimary),

                        value = message.value,
                        onValueChange = { message.value = it },
                        enabled = true,
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
                            contentPadding = PaddingValues(10.dp),
                            placeholder = {
                                Text(
                                    stringResource(id = R.string.chat_message_placeholder),
                                    fontSize = 16.sp
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
                        onClick = {
                            scope.launch {
                                onSendMessage(message.value)
                                message.value = ""
                                listState.animateScrollToItem(if (messages.isEmpty()) 0 else messages.size - 1)
                            }
                        }
                    ) {
                        Icon(
                            modifier = Modifier
                                .background(
                                    color = MaterialTheme.colorScheme.primary,
                                    shape = CircleShape
                                )
                                .padding(smallPadding),
                            imageVector = Icons.AutoMirrored.Filled.Send,
                            // contentDescription = stringResource(id = R.string.send_message_button_content_description),
                            contentDescription = "Send message",
                            tint = MaterialTheme.colorScheme.onPrimary
                        )
                    }
                }
            }
        }
    }*/



    if (isConnectionChannelClosed) {
        ConnectionErrorDialog {
            isConnectionAlive.value = false
            onErrorHandler()
        }
    }
}
