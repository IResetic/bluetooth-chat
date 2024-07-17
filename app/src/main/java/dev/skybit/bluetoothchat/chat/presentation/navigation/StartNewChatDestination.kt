package dev.skybit.bluetoothchat.chat.presentation.navigation

import androidx.compose.animation.EnterTransition
import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import androidx.navigation.compose.composable
import androidx.navigation.navigation
import dev.skybit.bluetoothchat.chat.presentation.ui.screens.availableconnections.AvailableConnectionsScreen
import dev.skybit.bluetoothchat.chat.presentation.ui.screens.chat.ChatScreen
import kotlinx.serialization.Serializable

@Serializable
object StartNewChatDestination {
    @Serializable
    object AvailableConnectionsDestination

    @Serializable
    object ChatDestination
}

fun NavGraphBuilder.nestedScreensGraph(
    navController: NavController,
    navigateBack: () -> Unit
) {
    navigation<StartNewChatDestination>(
        startDestination = StartNewChatDestination.AvailableConnectionsDestination
    ) {
        composable<StartNewChatDestination.AvailableConnectionsDestination>(
            enterTransition = { EnterTransition.None }
        ) {
            AvailableConnectionsScreen(
                navigateToNewChat = { navController.navigate(StartNewChatDestination.ChatDestination) },
                navigateBack = navigateBack
            )
        }

        composable<StartNewChatDestination.ChatDestination>(
            enterTransition = { EnterTransition.None }
        ) {
            ChatScreen(navigateBack = navigateBack)
        }
    }
}
