package dev.skybit.bluetoothchat.availableconnection.presentation.navigation

import androidx.navigation.NavGraphBuilder
import androidx.navigation.compose.composable
import dev.skybit.bluetoothchat.availableconnection.presentation.ui.AvailableConnectionsScreen
import kotlinx.serialization.Serializable

@Serializable
object AvailableConnectionsDestination

fun NavGraphBuilder.availableConnectionsGraph() {
    composable<AvailableConnectionsDestination> {
        AvailableConnectionsScreen()
    }
}
