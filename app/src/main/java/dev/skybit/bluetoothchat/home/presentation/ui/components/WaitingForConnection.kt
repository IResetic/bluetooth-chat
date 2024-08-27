package dev.skybit.bluetoothchat.home.presentation.ui.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import dev.skybit.bluetoothchat.R
import dev.skybit.bluetoothchat.core.presentation.constants.spacing__4x

@Composable
fun WaitingForConnection(
    navigateBack: () -> Unit
) {
    Column(
        modifier = Modifier.fillMaxSize(),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        CircularProgressIndicator(
            modifier = Modifier.padding(spacing__4x),
            color = MaterialTheme.colorScheme.primary
        )

        Text(stringResource(id = R.string.waiting_for_connection_message))

        Button(
            modifier = Modifier.padding(spacing__4x),
            onClick = { navigateBack() }
        ) {
            Text(stringResource(id = R.string.cancel_button_title))
        }
    }
}
