package com.seyone22.anime.presentation

import android.app.Application
import com.google.android.gms.wearable.Wearable
import com.seyone22.anime.scheduleNotificationWork

class AnimeApplication : Application() {
    val capabilityClient by lazy { Wearable.getCapabilityClient(this) }

    override fun onCreate() {
        super.onCreate()

        // Schedule background notification work using WorkManager
        scheduleNotificationWork(this)
    }
}
