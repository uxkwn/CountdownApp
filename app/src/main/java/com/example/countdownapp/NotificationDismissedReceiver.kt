package com.example.countdownapp

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent

class NotificationDismissedReceiver : BroadcastReceiver() {

    override fun onReceive(context: Context, intent: Intent) {
        if (intent.action == ACTION_NOTIFICATION_DISMISSED) {
            val preferences = CountdownPreferences(context)
            if (preferences.isCountdownActive) {
                val title = preferences.eventTitle
                val targetMillis = preferences.targetDateMillis
                // Re-post the notification to keep it persistent while active
                NotificationHelper.showCountdownNotification(context, title, targetMillis)
            }
        }
    }

    companion object {
        const val ACTION_NOTIFICATION_DISMISSED = "com.example.countdownapp.NOTIFICATION_DISMISSED"
    }
}
