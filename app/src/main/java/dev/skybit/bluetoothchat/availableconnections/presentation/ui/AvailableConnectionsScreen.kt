package dev.skybit.bluetoothchat.availableconnections.presentation.ui

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import dev.skybit.bluetoothchat.R
import dev.skybit.bluetoothchat.availableconnections.presentation.ui.AvailableConnectionsScreenEvent.StartScanning
import dev.skybit.bluetoothchat.availableconnections.presentation.ui.AvailableConnectionsScreenEvent.StopScanning

@Composable
fun AvailableConnectionsScreen() {
    val viewModel = hiltViewModel<AvailableConnectionsScreenViewModel>()
    val uiState = viewModel.state.collectAsState()

    Scaffold(
        modifier = Modifier.fillMaxSize()
    ) { innerPadding ->
        Box(
            modifier = Modifier
                .padding(innerPadding)
                .fillMaxSize(),
            contentAlignment = Alignment.Center
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .align(Alignment.BottomCenter)
                    .padding(16.dp),
                horizontalArrangement = Arrangement.SpaceEvenly
            ) {
                Button(
                    onClick = { viewModel.onEvent(StartScanning) },
                    enabled = !uiState.value.isSceningDevices
                ) {
                    Text(
                        text = stringResource(id = R.string.start_scanning_button_title),
                        style = MaterialTheme.typography.bodyMedium.copy(
                            color = MaterialTheme.colorScheme.onPrimary
                        )
                    )
                }

                Button(
                    onClick = { viewModel.onEvent(StopScanning) },
                    enabled = uiState.value.isSceningDevices
                ) {
                    Text(
                        text = stringResource(id = R.string.stop_scanning_button_title),
                        style = MaterialTheme.typography.bodyMedium.copy(
                            color = MaterialTheme.colorScheme.onPrimary
                        )
                    )
                }
            }
        }
    }
}
