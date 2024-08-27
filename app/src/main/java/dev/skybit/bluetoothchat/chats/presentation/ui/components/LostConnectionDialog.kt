package dev.skybit.bluetoothchat.chats.presentation.ui.components

import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import dev.skybit.bluetoothchat.R

@Composable
fun LostConnectionDialog(
    onDismissRequest: () -> Unit,
    navigateToHomePage: () -> Unit
) {
    AlertDialog(
        onDismissRequest = { onDismissRequest() },

        title = { Text(text = stringResource(id = R.string.error_dialog_disconnected_error_title)) },
        text = { Text(text = stringResource(id = R.string.error_dialog_disconnected_error_message)) },
        dismissButton = {
            Button(
                colors = ButtonDefaults.buttonColors(
                    containerColor = MaterialTheme.colorScheme.primaryContainer
                ),
                onClick = {
                    navigateToHomePage()
                }
            ) {
                Text(
                    text = stringResource(id = R.string.error_dialog_back_to_home_button_title),
                    color = MaterialTheme.colorScheme.primary
                )
            }
        },

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
