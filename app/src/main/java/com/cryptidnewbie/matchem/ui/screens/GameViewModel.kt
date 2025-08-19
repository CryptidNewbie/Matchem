package com.cryptidnewbie.matchem.ui.screens

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.cryptidnewbie.matchem.data.GameDifficulty
import com.cryptidnewbie.matchem.data.GameState
import com.cryptidnewbie.matchem.game.GameEngine
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

class GameViewModel : ViewModel() {
    private val gameEngine = GameEngine()
    
    val gameState: StateFlow<GameState> = gameEngine.gameState
    
    fun startNewGame(difficulty: GameDifficulty) {
        viewModelScope.launch {
            gameEngine.startNewGame(difficulty)
        }
    }
    
    fun flipCard(cardId: Int) {
        viewModelScope.launch {
            gameEngine.flipCard(cardId)
        }
    }
    
    fun pauseGame() {
        viewModelScope.launch {
            gameEngine.pauseGame()
        }
    }
    
    fun resumeGame() {
        viewModelScope.launch {
            gameEngine.resumeGame()
        }
    }
}