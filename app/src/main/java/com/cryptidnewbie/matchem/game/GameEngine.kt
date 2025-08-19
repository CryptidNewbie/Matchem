package com.cryptidnewbie.matchem.game

import com.cryptidnewbie.matchem.data.*
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlin.random.Random

class GameEngine {
    private val _gameState = MutableStateFlow(GameState())
    val gameState: StateFlow<GameState> = _gameState.asStateFlow()

    fun startNewGame(difficulty: GameDifficulty) {
        val cards = generateCards(difficulty)
        _gameState.value = GameState(
            cards = cards,
            difficulty = difficulty,
            startTime = System.currentTimeMillis()
        )
    }

    private fun generateCards(difficulty: GameDifficulty): List<Card> {
        val cards = mutableListOf<Card>()
        var cardId = 0

        // Add normal pairs
        repeat(difficulty.pairs) { pairIndex ->
            repeat(2) {
                cards.add(
                    Card(
                        id = cardId++,
                        pairId = pairIndex,
                        type = CardType.NORMAL,
                        imageResource = getCardImageResource(pairIndex)
                    )
                )
            }
        }

        // Add bad cards for Hard difficulty
        if (difficulty.hasBadCards) {
            repeat(2) {
                cards.add(
                    Card(
                        id = cardId++,
                        pairId = -1, // Bad cards don't have pairs
                        type = CardType.BAD_CARD,
                        imageResource = getBadCardImageResource()
                    )
                )
            }
        }

        return cards.shuffled(Random.Default)
    }

    fun flipCard(cardId: Int) {
        val currentState = _gameState.value
        if (currentState.isPaused || currentState.isGameComplete) return

        val card = currentState.cards.find { it.id == cardId }
        if (card == null || card.isFlipped || card.isMatched) return

        // If two cards are already flipped, reset them first
        if (currentState.flippedCards.size >= 2) {
            resetFlippedCards()
            return
        }

        // Flip the card
        val updatedCards = currentState.cards.map { 
            if (it.id == cardId) it.copy(isFlipped = true) else it 
        }
        
        val newFlippedCards = currentState.flippedCards + cardId
        
        _gameState.value = currentState.copy(
            cards = updatedCards,
            flippedCards = newFlippedCards,
            moves = currentState.moves + 1
        )

        // Check for bad card
        if (card.type == CardType.BAD_CARD) {
            handleBadCard()
            return
        }

        // Check for match when two cards are flipped
        if (newFlippedCards.size == 2) {
            checkForMatch()
        }
    }

    private fun resetFlippedCards() {
        val currentState = _gameState.value
        val updatedCards = currentState.cards.map { card ->
            if (card.id in currentState.flippedCards && !card.isMatched) {
                card.copy(isFlipped = false)
            } else {
                card
            }
        }
        
        _gameState.value = currentState.copy(
            cards = updatedCards,
            flippedCards = emptyList()
        )
    }

    private fun checkForMatch() {
        val currentState = _gameState.value
        val flippedCards = currentState.flippedCards
        if (flippedCards.size != 2) return

        val card1 = currentState.cards.find { it.id == flippedCards[0] }
        val card2 = currentState.cards.find { it.id == flippedCards[1] }

        if (card1 != null && card2 != null && card1.pairId == card2.pairId) {
            // Match found
            val updatedCards = currentState.cards.map { card ->
                if (card.id in flippedCards) {
                    card.copy(isMatched = true)
                } else {
                    card
                }
            }

            val newMatches = currentState.matches + 1
            val isComplete = newMatches == currentState.difficulty.pairs

            _gameState.value = currentState.copy(
                cards = updatedCards,
                flippedCards = emptyList(),
                matches = newMatches,
                isGameComplete = isComplete,
                endTime = if (isComplete) System.currentTimeMillis() else null
            )
        }
        // If no match, cards will be reset on next flip
    }

    private fun handleBadCard() {
        // Immediately end the turn for bad cards
        resetFlippedCards()
    }

    fun pauseGame() {
        _gameState.value = _gameState.value.copy(isPaused = true)
    }

    fun resumeGame() {
        _gameState.value = _gameState.value.copy(isPaused = false)
    }

    private fun getCardImageResource(pairIndex: Int): Int {
        // Return placeholder resource IDs - these would be actual card images
        return android.R.drawable.ic_dialog_info
    }

    private fun getBadCardImageResource(): Int {
        // Return placeholder resource ID for bad card
        return android.R.drawable.ic_dialog_alert
    }
}