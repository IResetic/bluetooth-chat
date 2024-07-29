package dev.skybit.bluetoothchat.home.presentation.navigation

import androidx.compose.animation.EnterTransition
import androidx.navigation.NavGraphBuilder
import androidx.navigation.compose.composable
import androidx.navigation.navigation
import dev.skybit.bluetoothchat.chat.presentation.ui.AvailableConnectionsScreen
import dev.skybit.bluetoothchat.chat.presentation.ui.model.ScreenType
import dev.skybit.bluetoothchat.home.presentation.navigation.HomeDestination.MessagesDestination
import dev.skybit.bluetoothchat.home.presentation.ui.HomeScreen
import kotlinx.serialization.Serializable

@Serializable
object HomeDestination {
    @Serializable
    object MessagesDestination

    @Serializable
    object AvailableConnectionsDestination
}

fun NavGraphBuilder.chatsGraph(
    navigateBack: () -> Unit
) {
    navigation<HomeDestination>(startDestination = MessagesDestination) {
        composable<MessagesDestination>(
            enterTransition = { EnterTransition.None }
        ) {
            HomeScreen()
        }

        composable<HomeDestination.AvailableConnectionsDestination>(
            enterTransition = { EnterTransition.None }
        ) {
            AvailableConnectionsScreen(
                screenType = ScreenType.DEVICES,
                navigateBack = navigateBack
            )
        }
    }
}

/*
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
*/

/*

@Serializable
object HomeDestination

fun NavGraphBuilder.chatsGraph(
    navigateToContacts: () -> Unit
) {
    composable<HomeDestination>(
        enterTransition = { EnterTransition.None }
    ) {
        HomeScreen(navigateToContacts = navigateToContacts)
    }
}

 */