package com.cryptidnewbie.matchem

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Surface
import androidx.compose.ui.Modifier
import com.cryptidnewbie.matchem.ui.MatchEmNavigation
import com.cryptidnewbie.matchem.ui.theme.MatchEmTheme
import com.google.android.gms.ads.MobileAds

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // This is a one-time initialization for the AdMob SDK
        MobileAds.initialize(this) {}

        setContent {
            MatchEmTheme {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = androidx.compose.material3.MaterialTheme.colorScheme.background
                ) {
                    MatchEmNavigation()
                }
            }
        }
    }
}