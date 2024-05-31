package dev.skybit.bluetoothchat.main

import android.annotation.SuppressLint
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import dev.skybit.bluetoothchat.availableconnection.presentation.navigation.AvailableConnectionsDestination
import dev.skybit.bluetoothchat.availableconnection.presentation.navigation.availableConnectionsGraph

@SuppressLint("UnusedMaterial3ScaffoldPaddingParameter")
@Composable
fun BluetoothChatContent(navController: NavHostController) {
    Scaffold {
        NavHost(
            navController = navController,
            startDestination = AvailableConnectionsDestination
        ) {
            availableConnectionsGraph()
        }
    }
}
