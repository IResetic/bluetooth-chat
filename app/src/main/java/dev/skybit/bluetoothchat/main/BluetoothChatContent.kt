package dev.skybit.bluetoothchat.main

import android.annotation.SuppressLint
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import dev.skybit.bluetoothchat.availableconnections.presentation.navigation.StartNewChatDestination
import dev.skybit.bluetoothchat.availableconnections.presentation.navigation.nestedScreensGraph
import dev.skybit.bluetoothchat.home.presentation.navigation.HomeDestination
import dev.skybit.bluetoothchat.home.presentation.navigation.chatsGraph

@SuppressLint("UnusedMaterial3ScaffoldPaddingParameter")
@Composable
fun BluetoothChatContent(navController: NavHostController) {
    Scaffold {
        NavHost(
            navController = navController,
            startDestination = HomeDestination
        ) {
            chatsGraph(
                navigateToContacts = { navController.navigate(StartNewChatDestination.AvailableConnectionsDestination) }
            )
            nestedScreensGraph(
                navController = navController,
                navigateBack = navController::popBackStack
            )
/*            chatsGraph(navigateToContacts = { navController.navigate(AvailableConnectionsDestination) })
            availableConnectionsGraph(
                navigateToNewChat = { navController.navigate(MessagesDestination) },
                navigateBack = navController::popBackStack
            )
            messagesGraph(
                navigateBack = navController::popBackStack
            )*/
        }
    }
}
