package dev.skybit.bluetoothchat.home.presentation.ui.components

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import dev.skybit.bluetoothchat.core.presentation.constants.spacing__4x
import dev.skybit.bluetoothchat.core.presentation.constants.spacing__6x

@Composable
fun MessageListItem(
    name: String,
    message: String,
    onClick: () -> Unit
) {
    Column(
        modifier = Modifier
            .clickable { onClick() }
            .fillMaxWidth()
            .padding(top = spacing__6x, bottom = spacing__6x, start = spacing__4x)
    ) {
        Text(text = name, style = MaterialTheme.typography.titleMedium)
        Text(text = message, style = MaterialTheme.typography.labelMedium)
    }
}
