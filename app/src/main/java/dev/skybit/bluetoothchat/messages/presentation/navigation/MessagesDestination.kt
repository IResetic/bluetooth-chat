package dev.skybit.bluetoothchat.messages.presentation.navigation

import androidx.navigation.NavGraphBuilder
import androidx.navigation.compose.composable
import dev.skybit.bluetoothchat.availableconnections.presentation.ui.screens.chat.ChatScreen
import kotlinx.serialization.Serializable

@Serializable
object MessagesDestination

fun NavGraphBuilder.messagesGraph(
    navigateBack: () -> Unit
) {
    composable<MessagesDestination> {
        ChatScreen(navigateBack = navigateBack)
    }
}
