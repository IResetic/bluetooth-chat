package dev.skybit.bluetoothchat.availableconnections.presentation.navigation

import androidx.navigation.NavGraphBuilder
import androidx.navigation.compose.composable
import dev.skybit.bluetoothchat.availableconnections.presentation.ui.AvailableConnectionsScreen
import kotlinx.serialization.Serializable

@Serializable
object AvailableConnectionsDestination

fun NavGraphBuilder.availableConnectionsGraph() {
    composable<AvailableConnectionsDestination> {
        AvailableConnectionsScreen()
    }
}
