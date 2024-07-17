package dev.skybit.bluetoothchat.chat.presentation.ui.components

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
import dev.skybit.bluetoothchat.chat.domain.model.BluetoothDeviceInfo
import dev.skybit.bluetoothchat.core.presentation.constants.extraSmallPadding
import dev.skybit.bluetoothchat.core.presentation.constants.mediumPadding
import dev.skybit.bluetoothchat.core.presentation.constants.mediumRadius
import dev.skybit.bluetoothchat.core.presentation.constants.smallPadding

@Composable
fun DevicesListItem(
    device: BluetoothDeviceInfo,
    isConnectingEnabled: Boolean,
    connectToDevice: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = smallPadding, vertical = extraSmallPadding)
            .clip(RoundedCornerShape(mediumRadius))
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
            modifier = Modifier.padding(start = mediumPadding, top = smallPadding)
        )
        Text(
            device.address,
            modifier = Modifier.padding(start = mediumPadding, bottom = smallPadding)
        )
    }
}
