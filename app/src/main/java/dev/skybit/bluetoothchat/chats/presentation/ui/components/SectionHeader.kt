package dev.skybit.bluetoothchat.chats.presentation.ui.components

import androidx.annotation.StringRes
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import dev.skybit.bluetoothchat.core.presentation.constants.spacing__2x

@Composable
fun SectionHeader(@StringRes sectionHeaderId: Int) {
    Text(
        text = stringResource(id = sectionHeaderId),
        style = MaterialTheme.typography.titleMedium,
        modifier = Modifier.padding(spacing__2x)
    )
}
