package dev.skybit.bluetoothchat.chats.presentation.navigation

import androidx.compose.animation.EnterTransition
import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import androidx.navigation.compose.composable
import androidx.navigation.navigation
import androidx.navigation.toRoute
import dev.skybit.bluetoothchat.chats.presentation.navigation.HomeDestination.MessagesDestination
import dev.skybit.bluetoothchat.chats.presentation.navigation.HomeDestination.NewChatDestination
import dev.skybit.bluetoothchat.chats.presentation.ui.HomeScreen
import dev.skybit.bluetoothchat.messages.presentation.ui.MessagesScreen
import kotlinx.serialization.Serializable

@Serializable
object HomeDestination {
    @Serializable
    object MessagesDestination

    @Serializable
    data class NewChatDestination(
        val chatId: String,
        val senderName: String
    )
}

fun NavGraphBuilder.chatsGraph(
    navController: NavController,
    navigateBack: () -> Unit
    ) {
    navigation<HomeDestination>(startDestination = MessagesDestination) {
        composable<MessagesDestination>(
            enterTransition = { EnterTransition.None }
        ) {
            HomeScreen(navigateChat = { chatId, senderName ->
                navController.navigate(
                    NewChatDestination(chatId, senderName)
                )
            })
        }

        composable<NewChatDestination>(
            enterTransition = { EnterTransition.None }
        ) {
            val args = it.toRoute<NewChatDestination>()
            MessagesScreen(
                args.senderName,
                navigateBack = {
                    navController.navigate(MessagesDestination)
                }
            )
        }
    }
}
