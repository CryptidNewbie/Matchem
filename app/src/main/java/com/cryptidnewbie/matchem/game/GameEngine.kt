package com.cryptidnewbie.matchem.game

import com.cryptidnewbie.matchem.R
import com.cryptidnewbie.matchem.data.*
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlin.random.Random

class GameEngine {
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
        _gameState.value = _gameState.value.copy(
            cards = _gameState.value.cards.map {
                if (it.id == cardId) it.copy(isFlipped = true) else it
            },
            flippedCards = _gameState.value.flippedCards + cardId,
            moves = _gameState.value.moves + 1
        )
    }

    fun markCardsAsMatched(cardIds: List<Int>) {
        val currentState = _gameState.value
        val updatedCards = currentState.cards.map { card ->
            if (card.id in cardIds) {
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

    fun resetFlippedCards() {
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

    fun pauseGame() {
        _gameState.value = _gameState.value.copy(isPaused = true)
    }

    fun resumeGame() {
        _gameState.value = _gameState.value.copy(isPaused = false)
    }

    private fun getCardImageResource(pairIndex: Int): Int? {
        return cardDrawables.getOrNull(pairIndex % cardDrawables.size)
    }

    private fun getBadCardImageResource(): Int {
        return R.drawable.loser_card
    }
}