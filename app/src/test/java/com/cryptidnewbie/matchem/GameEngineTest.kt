package com.cryptidnewbie.matchem

import com.cryptidnewbie.matchem.data.GameDifficulty
import com.cryptidnewbie.matchem.game.GameEngine
import org.junit.Test
import org.junit.Assert.*

/**
 * Test for GameEngine card image functionality
 */
class GameEngineTest {
    
    @Test
    fun test_card_generation_uses_images() {
        val gameEngine = GameEngine()
        gameEngine.startNewGame(GameDifficulty.EASY)
        
        val gameState = gameEngine.gameState.value
        
        // Verify cards are generated
        assertFalse("Cards should be generated", gameState.cards.isEmpty())
        
        // Verify cards have image resources
        gameState.cards.forEach { card ->
            assertNotNull("Card should have image resource", card.imageResource)
            assertNotEquals("Card should not use placeholder image", 
                android.R.drawable.ic_dialog_info, card.imageResource)
        }
    }
    
    @Test
    fun test_difficulty_settings() {
        assertEquals("Easy should have 6 pairs", 6, GameDifficulty.EASY.pairs)
        assertEquals("Medium should have 10 pairs", 10, GameDifficulty.MEDIUM.pairs)
        assertEquals("Hard should have 14 pairs", 14, GameDifficulty.HARD.pairs)
        
        assertFalse("Easy should not have bad cards", GameDifficulty.EASY.hasBadCards)
        assertFalse("Medium should not have bad cards", GameDifficulty.MEDIUM.hasBadCards)
        assertTrue("Hard should have bad cards", GameDifficulty.HARD.hasBadCards)
    }
}