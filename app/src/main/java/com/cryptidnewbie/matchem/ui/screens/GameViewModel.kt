package com.cryptidnewbie.matchem.ui.screens

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.cryptidnewbie.matchem.R
import com.cryptidnewbie.matchem.data.Card
import com.cryptidnewbie.matchem.data.CardType
import com.cryptidnewbie.matchem.data.GameDifficulty
import com.cryptidnewbie.matchem.data.GameState
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kotlin.random.Random

class GameViewModel : ViewModel() {
    private val _gameState = MutableStateFlow(GameState())
    val gameState: StateFlow<GameState> = _gameState.asStateFlow()

    private val cardDrawables = listOf(
        R.drawable.aquatic_missing_link,
        R.drawable.black_mage_octopus_with_outline,
        R.drawable.cutout_bigfoot_head,
        R.drawable.gorilla_pop,
        R.drawable.gray_alien_abduction,
        R.drawable.swan_wing,
        R.drawable.cryptid_md_logo_blue_outline,
        R.drawable.vampire_squid,
        R.drawable.cube_uap,
        R.drawable.flatwoods_monster512,
        R.drawable.phoenix_lights,
        R.drawable.tic_tac_ufo,
        R.drawable.loser_card
    )

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

        repeat(difficulty.pairs) { pairIndex ->
            repeat(2) {
                cards.add(
                    Card(
                        id = cardId++,
                        pairId = pairIndex,
                        type = CardType.NORMAL,
                        imageResource = cardDrawables.getOrNull(pairIndex % cardDrawables.size)
                    )
                )
            }
        }

        if (difficulty.hasBadCards) {
            repeat(2) {
                cards.add(
                    Card(
                        id = cardId++,
                        pairId = -1,
                        type = CardType.BAD_CARD,
                        imageResource = R.drawable.loser_card
                    )
                )
            }
        }

        return cards.shuffled(Random.Default)
    }

    fun flipCard(cardId: Int) {
        _gameState.update { currentState ->
            val cardToFlip = currentState.cards.find { it.id == cardId } ?: return@update currentState
            if (cardToFlip.isFlipped || cardToFlip.isMatched || currentState.flippedCards.size >= 2) {
                return@update currentState
            }

            val updatedCards = currentState.cards.map { card ->
                if (card.id == cardId) card.copy(isFlipped = true) else card
            }
            val newFlippedCards = currentState.flippedCards + cardId

            // Check for loser card!
            if (cardToFlip.type == CardType.BAD_CARD) {
                viewModelScope.launch {
                    delay(500L)
                    _gameState.update { resetFlippedCards(it) }
                }
                // Show both cards faced up until delay completes
                return@update currentState.copy(
                    cards = updatedCards,
                    flippedCards = newFlippedCards,
                    moves = currentState.moves + 1
                )
            }

            // If two cards flipped, check for match or bad card
            if (newFlippedCards.size == 2) {
                val card1 = updatedCards.find { it.id == newFlippedCards[0] }
                val card2 = updatedCards.find { it.id == newFlippedCards[1] }

                if (card1 != null && card2 != null) {
                    // If either is a BAD_CARD, flip both back
                    if (card1.type == CardType.BAD_CARD || card2.type == CardType.BAD_CARD) {
                        viewModelScope.launch {
                            delay(500L)
                            _gameState.update { resetFlippedCards(it) }
                        }
                        return@update currentState.copy(
                            cards = updatedCards,
                            flippedCards = newFlippedCards,
                            moves = currentState.moves + 1
                        )
                    }
                    // Only count as match if both are NORMAL and pairId matches
                    if (card1.type == CardType.NORMAL && card2.type == CardType.NORMAL && card1.pairId == card2.pairId) {
                        return@update handleMatch(currentState, updatedCards, newFlippedCards)
                    } else {
                        viewModelScope.launch {
                            delay(500L)
                            _gameState.update { resetFlippedCards(it) }
                        }
                        return@update currentState.copy(
                            cards = updatedCards,
                            flippedCards = newFlippedCards,
                            moves = currentState.moves + 1
                        )
                    }
                }
            }
            // If less than 2 cards flipped, just update state
            return@update currentState.copy(
                cards = updatedCards,
                flippedCards = newFlippedCards,
                moves = currentState.moves + 1
            )
        }
    }

    private fun handleMatch(currentState: GameState, updatedCards: List<Card>, flippedCardIds: List<Int>): GameState {
        val matchedCards = updatedCards.map { card ->
            if (card.id in flippedCardIds) card.copy(isMatched = true) else card
        }
        val newMatches = currentState.matches + 1
        val isComplete = newMatches == currentState.difficulty.pairs

        return currentState.copy(
            cards = matchedCards,
            flippedCards = emptyList(),
            matches = newMatches,
            isGameComplete = isComplete,
            endTime = if (isComplete) System.currentTimeMillis() else null,
            moves = currentState.moves + 1 // Count each match as a move
        )
    }

    private fun resetFlippedCards(currentState: GameState): GameState {
        val resetCards = currentState.cards.map { card ->
            if (card.id in currentState.flippedCards) card.copy(isFlipped = false) else card
        }
        return currentState.copy(cards = resetCards, flippedCards = emptyList())
    }

    fun pauseGame() {
        _gameState.update { it.copy(isPaused = true) }
    }

    fun resumeGame() {
        _gameState.update { it.copy(isPaused = false) }
    }
}