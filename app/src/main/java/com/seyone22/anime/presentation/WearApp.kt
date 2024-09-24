package com.seyone22.anime.presentation

import SettingsScreen
import UserPreferences
import android.content.Context
import androidx.compose.runtime.Composable
import androidx.wear.compose.material3.AppScaffold
import androidx.wear.compose.navigation.SwipeDismissableNavHost
import androidx.wear.compose.navigation.composable
import androidx.wear.compose.navigation.rememberSwipeDismissableNavController
import com.seyone22.anime.presentation.screen.ScheduleScreen

@Composable
fun WearApp(mainViewModel: MainViewModel, context: Context) {
    AppScaffold {
        val navController = rememberSwipeDismissableNavController()
        SwipeDismissableNavHost(navController = navController, startDestination = "main") {
            composable("main") {
                ScheduleScreen(
                    navController = navController,
                    mainViewModel = mainViewModel,
                    userPreferences = UserPreferences(context)
                )
            }
            composable("settings") {
                SettingsScreen(
                    navController = navController,
                    userPreferences = UserPreferences(context)
                )
            }
        }
    }
}
