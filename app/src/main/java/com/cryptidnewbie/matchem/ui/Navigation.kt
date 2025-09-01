package com.cryptidnewbie.matchem.ui

import androidx.compose.runtime.*
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.cryptidnewbie.matchem.data.GameDifficulty
import com.cryptidnewbie.matchem.ui.screens.*

sealed class Screen(val route: String) {
    object MainMenu : Screen("main_menu")
    object DifficultySelection : Screen("difficulty_selection")
    object Game : Screen("game/{difficulty}") {
        fun createRoute(difficulty: GameDifficulty) = "game/${difficulty.name}"
    }
    object GameOver : Screen("game_over/{difficulty}/{moves}/{time}") {
        fun createRoute(difficulty: GameDifficulty, moves: Int, time: Int) =
            "game_over/${difficulty.name}/$moves/$time"
    }
    object Settings : Screen("settings")
    object CardShop : Screen("card_shop")
}

@Composable
fun MatchEmNavigation(
    navController: NavHostController = rememberNavController()
) {
    NavHost(
        navController = navController,
        startDestination = Screen.MainMenu.route
    ) {
        composable(Screen.MainMenu.route) {
            MainMenuScreen(
                onPlayClick = { navController.navigate(Screen.DifficultySelection.route) },
                onSettingsClick = { navController.navigate(Screen.Settings.route) },
                onCardShopClick = { navController.navigate(Screen.CardShop.route) }
            )
        }

        composable(Screen.DifficultySelection.route) {
            DifficultySelectionScreen(
                onDifficultySelected = { difficulty ->
                    navController.navigate(Screen.Game.createRoute(difficulty))
                },
                onBackClick = { navController.popBackStack() }
            )
        }

        composable(Screen.Game.route) { backStackEntry ->
            val difficultyStr = backStackEntry.arguments?.getString("difficulty") ?: GameDifficulty.EASY.name
            val difficultyEnum = GameDifficulty.valueOf(difficultyStr)
            GameScreen(
                difficulty = difficultyEnum,
                onGameComplete = { moves: Int, timeInSeconds: Int ->
                    navController.navigate(
                        Screen.GameOver.createRoute(difficultyEnum, moves, timeInSeconds)
                    ) {
                        popUpTo(Screen.Game.route) { inclusive = true }
                    }
                },
                onBackClick = { navController.popBackStack() }
            )
        }

        composable(Screen.GameOver.route) { backStackEntry ->
            val difficultyStr = backStackEntry.arguments?.getString("difficulty") ?: GameDifficulty.EASY.name
            val moves = backStackEntry.arguments?.getString("moves")?.toIntOrNull() ?: 0
            val time = backStackEntry.arguments?.getString("time")?.toIntOrNull() ?: 0
            val difficultyEnum = GameDifficulty.valueOf(difficultyStr)

            GameOverScreen(
                difficulty = difficultyEnum,
                moves = moves,
                timeInSeconds = time,
                onPlayAgain = {
                    navController.navigate(Screen.Game.createRoute(difficultyEnum)) {
                        popUpTo(Screen.MainMenu.route) { inclusive = false }
                    }
                },
                onMainMenu = {
                    navController.navigate(Screen.MainMenu.route) {
                        popUpTo(Screen.MainMenu.route) { inclusive = true }
                    }
                }
            )
        }

        composable(Screen.Settings.route) {
            SettingsScreen(
                onBackClick = { navController.popBackStack() }
            )
        }

        composable(Screen.CardShop.route) {
            CardShopScreen(
                onBackClick = { navController.popBackStack() }
            )
        }
    }
}