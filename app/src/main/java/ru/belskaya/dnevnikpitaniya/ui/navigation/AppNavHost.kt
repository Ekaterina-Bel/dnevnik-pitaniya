package ru.belskaya.dnevnikpitaniya.ui.navigation

import androidx.compose.runtime.Composable
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import ru.belskaya.dnevnikpitaniya.ui.screen.addfood.AddFoodScreen
import ru.belskaya.dnevnikpitaniya.ui.screen.home.HomeScreen
import ru.belskaya.dnevnikpitaniya.ui.screen.setup.ProfileSetupScreen
import ru.belskaya.dnevnikpitaniya.ui.screen.welcome.WelcomeScreen

@Composable
fun AppNavHost() {
    val navController = rememberNavController()

    NavHost(navController = navController, startDestination = Routes.WELCOME) {

        composable(Routes.WELCOME) {
            WelcomeScreen(
                viewModel = hiltViewModel(),
                onStart = { hasProfile ->
                    navController.navigate(
                        if (hasProfile) Routes.HOME else Routes.SETUP
                    )
                },
            )
        }

        composable(Routes.SETUP) {
            ProfileSetupScreen(
                viewModel = hiltViewModel(),
                onDone = {
                    navController.navigate(Routes.HOME) {
                        popUpTo(0) { inclusive = true }   // назад в приветствие нельзя
                    }
                },
            )
        }

        composable(Routes.HOME) {
            HomeScreen(
                viewModel = hiltViewModel(),
                onAddFood = { navController.navigate(Routes.ADD_FOOD) },
            )
        }

        composable(Routes.ADD_FOOD) {
            AddFoodScreen(
                viewModel = hiltViewModel(),
                onDone = { navController.popBackStack() },
            )
        }
    }
}
