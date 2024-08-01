package dev.skybit.bluetoothchat.chats.presentation.ui.components

import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color

@Composable
fun ConnectionErrorDialog(
    onDismissRequest: () -> Unit
) {
    AlertDialog(
        onDismissRequest = {
            onDismissRequest()
        },
        // 5
        title = { Text(text = "Connection error") },
        text = { Text(text = "The Connection error has happened. Please check that everything is setup properly") },
        confirmButton = {
            Button(
                onClick = {
                    onDismissRequest()
                }
            ) {
                Text(
                    text = "Confirm",
                    color = Color.White
                )
            }
        }
    )
}
