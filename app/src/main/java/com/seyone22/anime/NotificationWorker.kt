package com.seyone22.anime

import android.app.Application
import android.content.Context
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import com.seyone22.anime.presentation.MainViewModel

class NotificationWorker(context: Context, params: WorkerParameters) : CoroutineWorker(context, params) {
    override suspend fun doWork(): Result {
        // Create a ViewModel and fetch airing schedules
        val viewModel = MainViewModel(applicationContext as Application)
        viewModel.fetchWatchingMedia()

        return Result.success()
    }
}
