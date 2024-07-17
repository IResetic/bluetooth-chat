package dev.skybit.bluetoothchat.home.presentation.navigation

import androidx.compose.animation.EnterTransition
import androidx.navigation.NavGraphBuilder
import androidx.navigation.compose.composable
import dev.skybit.bluetoothchat.home.presentation.ui.HomeScreen
import kotlinx.serialization.Serializable

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
