package dev.skybit.bluetoothchat.messages.presentation.ui.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.paging.PagingData
import androidx.paging.compose.collectAsLazyPagingItems
import dev.skybit.bluetoothchat.chats.domain.model.BluetoothMessage
import dev.skybit.bluetoothchat.chats.presentation.ui.components.ChatMessage
import kotlinx.coroutines.flow.Flow

@Composable
fun MessagesList(
    messageFlow: Flow<PagingData<BluetoothMessage>>
) {
    val listState = rememberLazyListState()
    val chatMessages = messageFlow.collectAsLazyPagingItems()

    LazyColumn(
        state = listState,
        modifier = Modifier
            .fillMaxWidth(),
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
}