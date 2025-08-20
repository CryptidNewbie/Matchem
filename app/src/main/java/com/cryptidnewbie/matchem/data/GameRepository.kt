package com.cryptidnewbie.matchem.data

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.*
import androidx.datastore.preferences.preferencesDataStore
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import kotlinx.serialization.json.Json
import kotlinx.serialization.encodeToString
import kotlinx.serialization.decodeFromString

val Context.dataStore: DataStore<Preferences> by preferencesDataStore(name = "match_em_prefs")

class GameRepository(private val context: Context) {
    
    private companion object {
        val SOUND_ENABLED = booleanPreferencesKey("sound_enabled")
        val SELECTED_CARD_BACK = stringPreferencesKey("selected_card_back")
        val EASY_SCORES = stringPreferencesKey("easy_scores")
        val MEDIUM_SCORES = stringPreferencesKey("medium_scores")
        val HARD_SCORES = stringPreferencesKey("hard_scores")
        val OWNED_CARD_BACKS = stringPreferencesKey("owned_card_backs")
    }

    // Settings
    val appSettings: Flow<AppSettings> = context.dataStore.data.map { preferences ->
        AppSettings(
            soundEnabled = preferences[SOUND_ENABLED] ?: true,
            selectedCardBack = preferences[SELECTED_CARD_BACK] ?: "default"
        )
    }

    suspend fun updateSoundEnabled(enabled: Boolean) {
        context.dataStore.edit { preferences ->
            preferences[SOUND_ENABLED] = enabled
        }
    }

    suspend fun updateSelectedCardBack(cardBackId: String) {
        context.dataStore.edit { preferences ->
            preferences[SELECTED_CARD_BACK] = cardBackId
        }
    }

    // Scores
    suspend fun saveScore(score: Score) {
        val key = when (score.difficulty) {
            GameDifficulty.EASY -> EASY_SCORES
            GameDifficulty.MEDIUM -> MEDIUM_SCORES
            GameDifficulty.HARD -> HARD_SCORES
        }

        context.dataStore.edit { preferences ->
            val currentScores = getScoresFromPrefs(preferences[key])
            val updatedScores = (currentScores + score)
                .sortedBy { it.timeInSeconds }
                .take(10) // Keep only top 10
            
            preferences[key] = Json.encodeToString(updatedScores)
        }
    }

    fun getScores(difficulty: GameDifficulty): Flow<List<Score>> {
        val key = when (difficulty) {
            GameDifficulty.EASY -> EASY_SCORES
            GameDifficulty.MEDIUM -> MEDIUM_SCORES
            GameDifficulty.HARD -> HARD_SCORES
        }

        return context.dataStore.data.map { preferences ->
            getScoresFromPrefs(preferences[key])
        }
    }

    suspend fun resetAllScores() {
        context.dataStore.edit { preferences ->
            preferences.remove(EASY_SCORES)
            preferences.remove(MEDIUM_SCORES)
            preferences.remove(HARD_SCORES)
        }
    }

    private fun getScoresFromPrefs(scoresJson: String?): List<Score> {
        return if (scoresJson.isNullOrEmpty()) {
            emptyList()
        } else {
            try {
                Json.decodeFromString<List<Score>>(scoresJson)
            } catch (e: Exception) {
                emptyList()
            }
        }
    }

    // Card Backs
    fun getOwnedCardBacks(): Flow<Set<String>> {
        return context.dataStore.data.map { preferences ->
            val ownedJson = preferences[OWNED_CARD_BACKS] ?: ""
            if (ownedJson.isEmpty()) {
                setOf("default") // Default card back is always owned
            } else {
                try {
                    Json.decodeFromString<Set<String>>(ownedJson)
                } catch (e: Exception) {
                    setOf("default")
                }
            }
        }
    }

    suspend fun addOwnedCardBack(cardBackId: String) {
        context.dataStore.edit { preferences ->
            val currentOwned = getOwnedCardBacksFromPrefs(preferences[OWNED_CARD_BACKS])
            val updatedOwned = currentOwned + cardBackId
            preferences[OWNED_CARD_BACKS] = Json.encodeToString(updatedOwned)
        }
    }

    private fun getOwnedCardBacksFromPrefs(ownedJson: String?): Set<String> {
        return if (ownedJson.isNullOrEmpty()) {
            setOf("default")
        } else {
            try {
                Json.decodeFromString<Set<String>>(ownedJson)
            } catch (e: Exception) {
                setOf("default")
            }
        }
    }

    fun getAvailableCardBacks(): List<CardBack> {
        return listOf(
            CardBack("default", "Classic", com.cryptidnewbie.matchem.R.drawable.bigfoot_ring_floor, true, "Free"),
            CardBack("red", "Red Pattern", android.R.drawable.ic_dialog_alert, false, "$0.99"),
            CardBack("blue", "Blue Pattern", android.R.drawable.ic_dialog_info, false, "$0.99"),
            CardBack("green", "Green Pattern", android.R.drawable.ic_menu_compass, false, "$0.99"),
            CardBack("purple", "Purple Pattern", android.R.drawable.ic_menu_gallery, false, "$0.99")
        )
    }
}