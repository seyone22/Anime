package com.seyone22.anime

import android.content.Context
import androidx.work.Constraints
import androidx.work.PeriodicWorkRequestBuilder
import androidx.work.WorkManager
import java.util.concurrent.TimeUnit

fun scheduleNotificationWork(context: Context) {
    // Set constraints, if needed (e.g., no charging required)
    val constraints = Constraints.Builder()
        .setRequiresCharging(false) // Remove or modify constraints as needed
        .build()

    // Set up periodic work to run every 15 minutes
    val workRequest = PeriodicWorkRequestBuilder<NotificationWorker>(60, TimeUnit.MINUTES)
        .setConstraints(constraints)
        .build()

    // Schedule the periodic work using WorkManager
    WorkManager.getInstance(context).enqueue(workRequest)
}
