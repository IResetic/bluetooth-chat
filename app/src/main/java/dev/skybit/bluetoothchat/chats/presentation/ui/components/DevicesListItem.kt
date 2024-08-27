package dev.skybit.bluetoothchat.chats.presentation.ui.components

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.res.stringResource
import dev.skybit.bluetoothchat.R
import dev.skybit.bluetoothchat.chats.domain.model.BluetoothDeviceInfo
import dev.skybit.bluetoothchat.core.presentation.constants.radius__2x
import dev.skybit.bluetoothchat.core.presentation.constants.spacing__1x
import dev.skybit.bluetoothchat.core.presentation.constants.spacing__2x
import dev.skybit.bluetoothchat.core.presentation.constants.spacing__4x

@Composable
fun DevicesListItem(
    device: BluetoothDeviceInfo,
    isConnectingEnabled: Boolean,
    connectToDevice: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = spacing__2x, vertical = spacing__1x)
            .clip(RoundedCornerShape(radius__2x))
            .then(
                if (isConnectingEnabled) {
                    Modifier.clickable(onClick = connectToDevice)
                } else {
                    Modifier
                }
            ),
        colors = CardDefaults.cardColors().copy(
            containerColor = MaterialTheme.colorScheme.onPrimary
        ),
        shape = MaterialTheme.shapes.medium
    ) {
        Text(
            device.name ?: stringResource(id = R.string.unknown_connection_name),
            modifier = Modifier.padding(start = spacing__4x, top = spacing__2x)
        )
        Text(
            device.address,
            modifier = Modifier.padding(start = spacing__4x, bottom = spacing__2x)
        )
    }
}
