package com.cryptidnewbie.matchem.game

import com.cryptidnewbie.matchem.R
import com.cryptidnewbie.matchem.data.*
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import kotlin.random.Random

class GameEngine(private val scope: CoroutineScope) {
    private val _gameState = MutableStateFlow(GameState())
    val gameState: StateFlow<GameState> = _gameState.asStateFlow()

    private val cardImages = listOf(
        R.drawable.aquatic_missing_link,
        R.drawable.black_mage_octopus_with_outline,
        R.drawable.cryptid_md_logo_blue_outline,
        R.drawable.cutout_bigfoot_head,
        R.drawable.gorilla_pop,
        R.drawable.gray_alien_abduction,
        R.drawable.small_cryptid_md_green_logo512,
        R.drawable.swan_wing,
        // Since Hard mode needs 14 pairs, we'll duplicate the images here.
        // You should add more images to your drawable folder to support this!
        R.drawable.aquatic_missing_link,
        R.drawable.black_mage_octopus_with_outline,
        R.drawable.cryptid_md_logo_blue_outline,
        R.drawable.cutout_bigfoot_head,
        R.drawable.gorilla_pop,
        R.drawable.gray_alien_abduction
    )

    fun startNewGame(difficulty: GameDifficulty) {
        scope.launch {
            val cards = generateCards(difficulty)
            _gameState.value = GameState(
                cards = cards,
                difficulty = difficulty,
                startTime = System.currentTimeMillis()
            )
        }
    }

    fun flipCard(cardId: Int) {
        scope.launch {
            val currentState = _gameState.value
            val flippedCard = currentState.cards.find { it.id == cardId } ?: return@launch

            if (flippedCard.isFlipped || flippedCard.isMatched || currentState.flippedCards.size >= 2) {
                return@launch
            }

            val updatedCards = currentState.cards.map {
                if (it.id == cardId) it.copy(isFlipped = true) else it
            }

            val newFlippedCards = currentState.flippedCards + cardId
            val newMoves = currentState.moves + 1

            _gameState.value = currentState.copy(
                cards = updatedCards,
                flippedCards = newFlippedCards,
                moves = newMoves
            )

            checkFlippedCards()
        }
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
                        pairId = -1,
                        type = CardType.BAD_CARD,
                        imageResource = null // Bad cards have no image
                    )
                )
            }
        }

        return cards.shuffled(Random)
    }

    private fun checkFlippedCards() {
        scope.launch {
            val currentState = _gameState.value
            if (currentState.flippedCards.size < 2) return@launch

            // Allow a short delay to let the user see the card
            delay(500)

            val firstCard = currentState.cards.find { it.id == currentState.flippedCards[0] }
            val secondCard = currentState.cards.find { it.id == currentState.flippedCards[1] }

            if (firstCard?.pairId == secondCard?.pairId) {
                // Match
                handleMatch(currentState.flippedCards)
            } else {
                // No match or a bad card, flip them back
                resetFlippedCards()
            }
        }
    }

    private fun resetFlippedCards() {
        val currentState = _gameState.value
        val updatedCards = currentState.cards.map { card ->
            if (card.id in currentState.flippedCards) {
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

    private fun handleMatch(flippedCards: List<Int>) {
        val currentState = _gameState.value
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

    fun pauseGame() {
        _gameState.value = _gameState.value.copy(isPaused = true)
    }

    fun resumeGame() {
        _gameState.value = _gameState.value.copy(isPaused = false)
    }

    private fun getCardImageResource(pairIndex: Int): Int {
        // This function now returns a valid resource ID from the list.
        return cardImages[pairIndex % cardImages.size]
    }
}