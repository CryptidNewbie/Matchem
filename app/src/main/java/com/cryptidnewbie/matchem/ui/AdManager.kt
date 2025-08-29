package com.cryptidnewbie.matchem.ui.ads

import android.app.Activity
import android.content.Context
import com.google.android.gms.ads.AdRequest
import com.google.android.gms.ads.LoadAdError
import com.google.android.gms.ads.interstitial.InterstitialAd
import com.google.android.gms.ads.interstitial.InterstitialAdLoadCallback

object AdManager {
    private var interstitialAd: InterstitialAd? = null
    private var easyCount = 0

    // Your real interstitial ad unit ID!
    private const val INTERSTITIAL_AD_UNIT_ID = "ca-app-pub-4574748721786364/2859866898"

    fun loadInterstitial(context: Context) {
        InterstitialAd.load(
            context,
            INTERSTITIAL_AD_UNIT_ID,
            AdRequest.Builder().build(),
            object : InterstitialAdLoadCallback() {
                override fun onAdLoaded(ad: InterstitialAd) {
                    interstitialAd = ad
                }
                override fun onAdFailedToLoad(error: LoadAdError) {
                    interstitialAd = null
                }
            }
        )
    }

    /**
     * Call this every time the player wins a game.
     * Shows interstitial ad every 2 easy wins, or every 1 medium/hard win.
     * @param activity the current Activity (for showing ads)
     * @param difficulty the GameDifficulty ("easy", "medium", "hard")
     * @param hasWon true if player won the game
     */
    fun showInterstitialIfNeeded(activity: Activity, difficulty: String, hasWon: Boolean) {
        if (!hasWon) return

        if (difficulty == "easy") {
            easyCount++
            if (easyCount >= 2) {
                interstitialAd?.show(activity)
                easyCount = 0
                loadInterstitial(activity)
            }
        } else if (difficulty == "medium" || difficulty == "hard") {
            interstitialAd?.show(activity)
            loadInterstitial(activity)
        }
    }
}