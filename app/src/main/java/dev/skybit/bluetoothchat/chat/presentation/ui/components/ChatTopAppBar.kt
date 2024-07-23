@file:OptIn(ExperimentalMaterial3Api::class)

package dev.skybit.bluetoothchat.chat.presentation.ui.components

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import dev.skybit.bluetoothchat.chat.presentation.ui.model.ScreenType

@Composable
fun ChatTopAppBar(
    screenType: ScreenType,
    name: String,
    navigateBack: () -> Unit,
    startConnection: () -> Unit
) {

    val title = if(screenType == ScreenType.DEVICES) "Bluetooth Connections" else name

    TopAppBar(
        title = { Text(title) },
        navigationIcon = {
            IconButton(onClick = { navigateBack() }) {
                Icon(
                    imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                    contentDescription = "Navigat back"
                )
            }
        },
        colors = TopAppBarDefaults.topAppBarColors().copy(
            containerColor = MaterialTheme.colorScheme.inversePrimary
        ),
        actions = {
            IconButton(onClick = { startConnection() }) {
                Icon(Icons.Default.Refresh, "Connect")
            }
        }
    )
}