package com.example.countdownapp

import android.content.Context
import androidx.work.CoroutineWorker
import androidx.work.ExistingPeriodicWorkPolicy
import androidx.work.PeriodicWorkRequestBuilder
import androidx.work.WorkManager
import androidx.work.WorkerParameters
import java.util.concurrent.TimeUnit

class CountdownWorker(
    private val context: Context,
    workerParams: WorkerParameters,
) : CoroutineWorker(context, workerParams) {

    override suspend fun doWork(): Result {
        val preferences = CountdownPreferences(context)
        if (preferences.isCountdownActive) {
            val title = preferences.eventTitle
            val targetMillis = preferences.targetDateMillis
            NotificationHelper.showCountdownNotification(context, title, targetMillis)
        } else {
            NotificationHelper.cancelNotification(context)
            WorkManager.getInstance(context).cancelUniqueWork(WORK_NAME)
        }
        return Result.success()
    }

    companion object {
        const val WORK_NAME = "countdown_daily_worker"

        fun scheduleDailyWorker(context: Context) {
            val workRequest = PeriodicWorkRequestBuilder<CountdownWorker>(
                1,
                TimeUnit.DAYS,
            ).build()

            WorkManager.getInstance(context).enqueueUniquePeriodicWork(
                WORK_NAME,
                ExistingPeriodicWorkPolicy.UPDATE,
                workRequest,
            )
        }

        fun cancelWorker(context: Context) {
            WorkManager.getInstance(context).cancelUniqueWork(WORK_NAME)
        }
    }
}
