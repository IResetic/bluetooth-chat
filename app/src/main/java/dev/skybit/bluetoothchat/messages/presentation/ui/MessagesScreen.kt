@file:OptIn(ExperimentalMaterial3Api::class)

package dev.skybit.bluetoothchat.messages.presentation.ui

import android.util.Log
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
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
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Modifier
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.paging.compose.LazyPagingItems
import androidx.paging.compose.collectAsLazyPagingItems
import dev.skybit.bluetoothchat.chats.domain.model.BluetoothMessage
import dev.skybit.bluetoothchat.chats.presentation.ui.components.WaitingForConnection
import dev.skybit.bluetoothchat.messages.presentation.ui.components.MessagesList

@Composable
fun MessagesScreen(
    senderName: String,
    navigateBack: () -> Unit
) {
    val message = rememberSaveable { mutableStateOf("") }
    val isConnectionAlive = rememberSaveable { mutableStateOf(true) }
    val interactionSource = remember { MutableInteractionSource() }
    val listState = rememberLazyListState()
    val scope = rememberCoroutineScope()

    val viewModel = hiltViewModel<MessagesScreenViewModel>()
    val uiState by viewModel.state.collectAsState()
    val chatMessages = viewModel.chatMessagesPagingSource.collectAsLazyPagingItems()

    var chatMessages2: LazyPagingItems<BluetoothMessage>

    if (uiState.chatId != " ") {
        Log.d("TEST_LE_UNIT", "Chat id is not empty")
    }

    LaunchedEffect(chatMessages.itemCount) {
        if (listState.firstVisibleItemIndex != 0) {
            listState.scrollToItem(0)
        }
    }

    Scaffold(
        topBar = {
            if (uiState.chatId != " ") {
                TopAppBar(
                    title = { Text(senderName) },
                    navigationIcon = {
                        IconButton(onClick = { navigateBack() }) {
                            Icon(
                                imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                                contentDescription = "Navigate back"
                            )
                        }
                    },
                    colors = TopAppBarDefaults.topAppBarColors().copy(
                        containerColor = MaterialTheme.colorScheme.inversePrimary
                    )
                )
            } else {
                TopAppBar(title = { })
            }
        }
    ) { paddingValues ->

        if (uiState.chatId == " ") {
            WaitingForConnection(navigateBack = navigateBack)
        } else {
            Box(modifier = Modifier.padding(paddingValues)) {
                MessagesList(messageFlow = uiState.chatMessagesFlow)
            }
/*            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(paddingValues)
                    .consumeWindowInsets(paddingValues)
                    .imePadding()
            ) {
                LazyColumn(
                    state = listState,
                    modifier = Modifier
                        .fillMaxWidth()
                        .weight(1f),
                    reverseLayout = true,
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
                        }
                    }

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
                                    viewModel.onEvent(MessagesEvent.SendMessage(message.value))
                                    message.value = ""
                                    // listState.animateScrollToItem(if (chatMessages.itemCount == 0) 0 else chatMessages.itemCount - 1)
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
                                contentDescription = "Send message",
                                tint = MaterialTheme.colorScheme.onPrimary
                            )
                        }
                    }
                }
            }*/
        }
    }
}
