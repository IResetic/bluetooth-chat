package dev.skybit.bluetoothchat.chat.presentation.navigation

import androidx.compose.animation.EnterTransition
import androidx.navigation.NavGraphBuilder
import androidx.navigation.compose.composable
import androidx.navigation.toRoute
import dev.skybit.bluetoothchat.chat.presentation.ui.model.ScreenType
import dev.skybit.bluetoothchat.chat.presentation.ui.AvailableConnectionsScreen
import kotlinx.serialization.Serializable

@Serializable
data class StartNewChatDestination(
    val screenType: Int
)

fun NavGraphBuilder.startNewChatGraph(
    navigateBack: () -> Unit
) {
    composable<StartNewChatDestination>(
        enterTransition = { EnterTransition.None }
    ) {
        val args = it.toRoute<StartNewChatDestination>()
        AvailableConnectionsScreen(
            screenType = ScreenType.getScreenType(args.screenType),
            navigateBack = navigateBack
        )
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
