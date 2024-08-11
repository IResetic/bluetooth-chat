package dev.skybit.bluetoothchat.messages.presentation.ui

import androidx.paging.PagingData
import dev.skybit.bluetoothchat.chats.domain.model.BluetoothMessage
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flowOf

data class MessagesScreenUiState(
    val senderName: String = "",
    val chatId: String = "",
    val chatMessagesFlow: Flow<PagingData<BluetoothMessage>> = flowOf(PagingData.empty())
)
