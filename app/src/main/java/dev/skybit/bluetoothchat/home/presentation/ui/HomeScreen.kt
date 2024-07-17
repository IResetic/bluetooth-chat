package dev.skybit.bluetoothchat.home.presentation.ui

import android.annotation.SuppressLint
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.hilt.navigation.compose.hiltViewModel

@OptIn(ExperimentalMaterial3Api::class)
@SuppressLint("UnusedMaterial3ScaffoldPaddingParameter")
@Composable
fun HomeScreen(
    navigateToContacts: () -> Unit
) {
    val viewModel = hiltViewModel<HomeScreenViewModel>()

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Chats") },
                colors = TopAppBarDefaults.topAppBarColors().copy(
                    containerColor = MaterialTheme.colorScheme.inversePrimary
                )
            )
        },
        floatingActionButton = {
            FloatingActionButton(
                onClick = { navigateToContacts() }
            ) {
                Icon(imageVector = Icons.Filled.Add, contentDescription = "Add new chat")
            }
        }
    ) {
        Text(text = "This is chats screen")
    }
}
