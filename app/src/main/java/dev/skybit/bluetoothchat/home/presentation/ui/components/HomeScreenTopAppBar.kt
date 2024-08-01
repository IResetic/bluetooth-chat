@file:OptIn(ExperimentalMaterial3Api::class)

package dev.skybit.bluetoothchat.home.presentation.ui.components

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import dev.skybit.bluetoothchat.home.presentation.ui.model.ScreenType

@Composable
fun HomeScreenTopAppBar(
    screenType: ScreenType,
    navigateBack: () -> Unit
) {
    val title = when (screenType) {
        is ScreenType.HomeScreenType -> "Chats"
        is ScreenType.DevicesScreenType -> "Bluetooth Connections"
        is ScreenType.ChatScreenType -> screenType.deviceName
    }

    TopAppBar(
        title = { Text(title) },
        navigationIcon = {
            if (screenType !is ScreenType.HomeScreenType) {
                IconButton(onClick = { navigateBack() }) {
                    Icon(
                        imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                        contentDescription = "Navigate back"
                    )
                }
            }
        },
        colors = TopAppBarDefaults.topAppBarColors().copy(
            containerColor = MaterialTheme.colorScheme.inversePrimary
        )
    )
}
