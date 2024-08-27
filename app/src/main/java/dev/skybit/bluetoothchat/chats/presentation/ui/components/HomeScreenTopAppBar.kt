@file:OptIn(ExperimentalMaterial3Api::class)

package dev.skybit.bluetoothchat.chats.presentation.ui.components

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
import androidx.compose.ui.res.stringResource
import dev.skybit.bluetoothchat.R
import dev.skybit.bluetoothchat.chats.presentation.ui.model.ScreenType

@Composable
fun HomeScreenTopAppBar(
    isConnecting: Boolean,
    screenType: ScreenType,
    navigateBack: () -> Unit
) {
    val title = when (screenType) {
        is ScreenType.HomeScreenType -> stringResource(id = R.string.home_screen_title)
        is ScreenType.DevicesScreenType -> stringResource(id = R.string.bluetooth_screen_title)
        is ScreenType.ChatScreenType -> screenType.deviceName
    }

    TopAppBar(
        title = { Text(title) },
        navigationIcon = {
            if (screenType !is ScreenType.HomeScreenType && !isConnecting) {
                IconButton(
                    onClick = {
                        navigateBack()
                    }
                ) {
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
