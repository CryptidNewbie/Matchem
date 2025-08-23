package com.cryptidnewbie.matchem.ui.screens

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.cryptidnewbie.matchem.data.GameDifficulty
import com.cryptidnewbie.matchem.data.GameState
import com.cryptidnewbie.matchem.game.GameEngine
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

class GameViewModel : ViewModel() {
    private val gameEngine = GameEngine(viewModelScope)

    val gameState: StateFlow<GameState> = gameEngine.gameState

    fun startNewGame(difficulty: GameDifficulty) {
        gameEngine.startNewGame(difficulty)
    }

    fun flipCard(cardId: Int) {
        gameEngine.flipCard(cardId)
    }

    fun pauseGame() {
        gameEngine.pauseGame()
    }

    fun resumeGame() {
        gameEngine.resumeGame()
    }
}