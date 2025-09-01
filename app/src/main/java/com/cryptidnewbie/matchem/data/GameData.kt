package com.cryptidnewbie.matchem.data

enum class GameDifficulty(
    val displayName: String,
    val rows: Int,
    val columns: Int,
    val totalCards: Int,
    val pairs: Int,
    val hasBadCards: Boolean = false
) {
    EASY("Easy", 3, 4, 12, 6, false),
    MEDIUM("Medium", 4, 5, 20, 10, false),
    HARD("Hard", 5, 6, 30, 14, true)
}

enum class CardType {
    NORMAL,
    BAD_CARD
}

data class Card(
    val id: Int,
    val pairId: Int,
    val type: CardType = CardType.NORMAL,
    val isFlipped: Boolean = false,
    val isMatched: Boolean = false,
    val imageResource: Int? = null
)

data class GameState(
    val cards: List<Card> = emptyList(),
    val flippedCards: List<Int> = emptyList(),
    val moves: Int = 0,
    val matches: Int = 0,
    val isGameComplete: Boolean = false,
    val isPaused: Boolean = false,
    val startTime: Long = 0L,
    val endTime: Long? = null,
    val difficulty: GameDifficulty = GameDifficulty.EASY
)

data class AppSettings(
    val soundEnabled: Boolean = true,
    val selectedCardBack: String = "default"
)

data class CardBack(
    val id: String,
    val name: String,
    val imageResource: Int,
    val isOwned: Boolean = false,
    val price: String = "$0.99"
)