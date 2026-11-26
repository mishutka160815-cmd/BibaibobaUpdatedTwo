package com.bibaiboba.app.ui

import androidx.compose.runtime.Composable
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.bibaiboba.app.ui.screens.ChatScreen
import com.bibaiboba.app.ui.screens.SettingsScreen

object Routes {
    const val CHAT = "chat"
    const val SETTINGS = "settings"
}

@Composable
fun BibaibobaRoot() {
    val navController = rememberNavController()
    val chatViewModel: ChatViewModel = viewModel()

    NavHost(navController = navController, startDestination = Routes.CHAT) {
        composable(Routes.CHAT) {
            ChatScreen(
                viewModel = chatViewModel,
                onOpenSettings = { navController.navigate(Routes.SETTINGS) }
            )
        }
        composable(Routes.SETTINGS) {
            SettingsScreen(
                viewModel = chatViewModel,
                onBack = { navController.popBackStack() }
            )
        }
    }
}
