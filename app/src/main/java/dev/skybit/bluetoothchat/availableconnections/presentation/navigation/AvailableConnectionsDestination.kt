package dev.skybit.bluetoothchat.availableconnections.presentation.navigation

import androidx.compose.animation.EnterTransition
import androidx.navigation.NavGraphBuilder
import androidx.navigation.compose.composable
import dev.skybit.bluetoothchat.availableconnections.domain.model.BluetoothDeviceInfo
import dev.skybit.bluetoothchat.availableconnections.presentation.ui.screens.connections.AvailableConnectionsScreen
import kotlinx.serialization.Serializable

@Serializable
object AvailableConnectionsDestination

fun NavGraphBuilder.availableConnectionsGraph(
    navigateToNewChat: (BluetoothDeviceInfo) -> Unit,
    navigateBack: () -> Unit
) {
    composable<AvailableConnectionsDestination>(
        enterTransition = { EnterTransition.None }
    ) {
        AvailableConnectionsScreen(
            navigateToNewChat = navigateToNewChat,
            navigateBack = navigateBack
        )
    }
}
