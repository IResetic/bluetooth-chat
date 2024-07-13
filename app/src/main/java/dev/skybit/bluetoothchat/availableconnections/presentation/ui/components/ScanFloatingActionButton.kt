package dev.skybit.bluetoothchat.availableconnections.presentation.ui.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.ExtendedFloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import dev.skybit.bluetoothchat.R
import dev.skybit.bluetoothchat.availableconnections.presentation.ui.AvailableConnectionsScreenEvent
import dev.skybit.bluetoothchat.core.presentation.constants.smallPadding

@Composable
fun ScanFloatingActionButton(
    isScanning: Boolean,
    isEnabled: Boolean,
    onClick: (AvailableConnectionsScreenEvent) -> Unit
) {
    if (isScanning) {
        StopFab(
            isEnabled = isEnabled,
            onClick = onClick
        )
    } else {
        ScanFab(
            isEnabled = isEnabled,
            onClick = onClick
        )
    }
}

@Composable
fun ScanFab(
    isEnabled: Boolean,
    onClick: (AvailableConnectionsScreenEvent) -> Unit
) {
    ExtendedFloatingActionButton(
        onClick = {
            if (isEnabled) {
                onClick(AvailableConnectionsScreenEvent.StartScanning)
            }
        },
        containerColor = MaterialTheme.colorScheme.inversePrimary,
        contentColor = MaterialTheme.colorScheme.onPrimary
    ) {
        Row(
            horizontalArrangement = Arrangement.Center,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(
                Icons.Filled.Search,
                "Scan for devices FAB.",
                modifier = Modifier.padding(end = smallPadding),
                tint = MaterialTheme.colorScheme.onPrimaryContainer

            )
            Text(
                text = stringResource(id = R.string.start_scanning_button_title),
                color = MaterialTheme.colorScheme.onPrimaryContainer
            )
        }
    }
}

@Composable
fun StopFab(
    isEnabled: Boolean,
    onClick: (AvailableConnectionsScreenEvent) -> Unit
) {
    ExtendedFloatingActionButton(
        onClick = {
            if (isEnabled) {
                onClick(AvailableConnectionsScreenEvent.StopScanning)
            }
        },
        containerColor = MaterialTheme.colorScheme.primary,
        contentColor = MaterialTheme.colorScheme.onPrimary
    ) {
        Row(
            horizontalArrangement = Arrangement.Center,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(
                Icons.Filled.Close,
                "Scan for devices FAB.",
                modifier = Modifier.padding(end = smallPadding),
                tint = MaterialTheme.colorScheme.onPrimary

            )
            Text(
                text = stringResource(id = R.string.stop_scanning_button_title),
                color = MaterialTheme.colorScheme.onPrimary
            )
        }
    }
}
