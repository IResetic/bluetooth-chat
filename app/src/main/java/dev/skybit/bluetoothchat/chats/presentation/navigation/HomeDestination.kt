package dev.skybit.bluetoothchat.chats.presentation.navigation

import androidx.compose.animation.EnterTransition
import androidx.navigation.NavGraphBuilder
import androidx.navigation.compose.composable
import dev.skybit.bluetoothchat.chats.presentation.ui.HomeScreen
import kotlinx.serialization.Serializable

@Serializable
object HomeDestination

fun NavGraphBuilder.chatsGraph() {
    composable<HomeDestination>(
        enterTransition = { EnterTransition.None }
    ) {
        HomeScreen()
    }
}
