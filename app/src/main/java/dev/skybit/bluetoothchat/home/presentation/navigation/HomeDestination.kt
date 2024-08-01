package dev.skybit.bluetoothchat.home.presentation.navigation

import androidx.compose.animation.EnterTransition
import androidx.navigation.NavGraphBuilder
import androidx.navigation.compose.composable
import androidx.navigation.navigation
import dev.skybit.bluetoothchat.home.presentation.navigation.HomeDestination.MessagesDestination
import dev.skybit.bluetoothchat.home.presentation.ui.HomeScreen
import kotlinx.serialization.Serializable

@Serializable
object HomeDestination {
    @Serializable
    object MessagesDestination
}

fun NavGraphBuilder.chatsGraph() {
    navigation<HomeDestination>(startDestination = MessagesDestination) {
        composable<MessagesDestination>(
            enterTransition = { EnterTransition.None }
        ) {
            HomeScreen()
        }
    }
}
