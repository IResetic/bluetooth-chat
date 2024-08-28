package dev.skybit.bluetoothchat.home.presentation.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import dev.skybit.bluetoothchat.core.presentation.constants.spacing__3_5x
import dev.skybit.bluetoothchat.core.presentation.constants.spacing__4x
import dev.skybit.bluetoothchat.home.domain.model.BluetoothMessage

@Composable
fun ChatMessage(
    message: BluetoothMessage,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier
            .fillMaxWidth(0.65f)
            .clip(
                RoundedCornerShape(
                    topStart = if (message.isFromLocalUser) spacing__3_5x else 0.dp,
                    topEnd = spacing__3_5x,
                    bottomStart = spacing__3_5x,
                    bottomEnd = if (message.isFromLocalUser) 0.dp else spacing__3_5x
                )
            )
            .background(
                if (message.isFromLocalUser) {
                    MaterialTheme.colorScheme.tertiaryContainer
                } else {
                    MaterialTheme.colorScheme.primaryContainer
                }
            )
            .padding(spacing__4x)
    ) {
        Text(
            text = message.senderName,
            style = MaterialTheme.typography.bodySmall
        )
        Text(
            text = message.message,
            color = Color.Black,
            modifier = Modifier.widthIn(max = 600.dp)
        )
    }
}
