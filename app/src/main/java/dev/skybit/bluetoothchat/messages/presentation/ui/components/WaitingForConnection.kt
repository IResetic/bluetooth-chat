package dev.skybit.bluetoothchat.messages.presentation.ui.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
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
import dev.skybit.bluetoothchat.core.presentation.constants.mediumPadding

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
            modifier = Modifier.padding(mediumPadding),
            color = MaterialTheme.colorScheme.primary
        )

        Text("Waiting for connection...")

        Button(
            modifier = Modifier.padding(mediumPadding),
            onClick = { navigateBack() }) {
            Text("Cancel")
        }
    }
}