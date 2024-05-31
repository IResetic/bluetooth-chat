package dev.skybit.bluetoothchat

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen
import androidx.navigation.compose.rememberNavController
import dagger.hilt.android.AndroidEntryPoint
import dev.skybit.bluetoothchat.main.BluetoothChatContent
import dev.skybit.bluetoothchat.ui.theme.BluetoothChatTheme

@AndroidEntryPoint
class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        installSplashScreen()

        setContent {
            val navController = rememberNavController()

            BluetoothChatTheme {
                BluetoothChatContent(navController = navController)
            }
        }
    }
}
