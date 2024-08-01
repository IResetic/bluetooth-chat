package dev.skybit.bluetoothchat.chats.presentation.ui.components

import androidx.compose.foundation.layout.Column
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable

@Composable
fun MessageListItem(
    name: String,
    message: String
) {
    Column {
        Text(text = name)
        Text(text = message)
    }
}
