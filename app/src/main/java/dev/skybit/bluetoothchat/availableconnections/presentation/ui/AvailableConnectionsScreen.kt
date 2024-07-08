package dev.skybit.bluetoothchat.availableconnections.presentation.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.hilt.navigation.compose.hiltViewModel
import dev.skybit.bluetoothchat.R
import dev.skybit.bluetoothchat.availableconnections.presentation.ui.components.DevicesListItem
import dev.skybit.bluetoothchat.availableconnections.presentation.ui.components.ScanFloatingActionButton
import dev.skybit.bluetoothchat.availableconnections.presentation.ui.components.SectionHeader

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AvailableConnectionsScreen() {
    val viewModel = hiltViewModel<AvailableConnectionsScreenViewModel>()
    val uiState = viewModel.state.collectAsState()

    Scaffold(
        modifier = Modifier
            .fillMaxSize()
            .background(color = MaterialTheme.colorScheme.onPrimaryContainer),
        topBar = {
            TopAppBar(
                title = { Text("Bluetooth Connections") },
                colors = TopAppBarDefaults.topAppBarColors().copy(
                    containerColor = MaterialTheme.colorScheme.inversePrimary
                )
            )
        },
        floatingActionButton = {
            ScanFloatingActionButton(
                isScanning = uiState.value.isSceningDevices,
                onClick = viewModel::onEvent
            )
        }
    ) { innerPadding ->
        Box(
            modifier = Modifier
                .padding(innerPadding)
                .fillMaxSize(),
            contentAlignment = Alignment.TopStart
        ) {
            LazyColumn {
                if (uiState.value.pairedDevices.isNotEmpty()) {
                    item { SectionHeader(sectionHeaderId = R.string.pair_devices_header) }
                }

                items(uiState.value.pairedDevices) { device ->
                    DevicesListItem(device = device)
                }

                if (uiState.value.scannedDevices.isNotEmpty()) {
                    item { SectionHeader(sectionHeaderId = R.string.available_devices_header) }
                }

                items(uiState.value.scannedDevices) { device ->
                    DevicesListItem(device = device)
                }
            }
        }
    }
}
