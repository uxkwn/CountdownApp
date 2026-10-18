package com.example.countdownapp

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent

class BootReceiver : BroadcastReceiver() {

    override fun onReceive(context: Context, intent: Intent) {
        if (intent.action == Intent.ACTION_BOOT_COMPLETED) {
            val preferences = CountdownPreferences(context)
            if (preferences.isCountdownActive) {
                val title = preferences.eventTitle
                val targetMillis = preferences.targetDateMillis

                NotificationHelper.showCountdownNotification(context, title, targetMillis)
                CountdownWorker.scheduleDailyWorker(context)
            }
        }
    }
}
