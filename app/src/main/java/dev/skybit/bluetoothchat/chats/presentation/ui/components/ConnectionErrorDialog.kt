package dev.skybit.bluetoothchat.chats.presentation.ui.components

import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import dev.skybit.bluetoothchat.R

@Composable
fun ConnectionErrorDialog(
    onDismissRequest: () -> Unit
) {
    AlertDialog(
        onDismissRequest = { onDismissRequest() },

        title = { Text(text = stringResource(id = R.string.error_dialog_connection_error_title)) },
        text = { Text(text = stringResource(id = R.string.error_dialog_connection_error_message)) },
        confirmButton = {
            Button(
                onClick = {
                    onDismissRequest()
                }
            ) {
                Text(
                    text = stringResource(id = R.string.error_dialog_confirm_button_title),
                    color = Color.White
                )
            }
        }
    )
}
