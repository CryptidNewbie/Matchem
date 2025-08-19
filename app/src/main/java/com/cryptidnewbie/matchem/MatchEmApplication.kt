package com.cryptidnewbie.matchem

import android.app.Application
import com.google.android.gms.ads.MobileAds

class MatchEmApplication : Application() {
    override fun onCreate() {
        super.onCreate()
        
        // Initialize Mobile Ads SDK
        MobileAds.initialize(this) {}
    }
}