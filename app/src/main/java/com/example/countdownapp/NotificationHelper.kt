package com.example.countdownapp

import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.os.Build
import android.widget.RemoteViews
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat

object NotificationHelper {

    const val CHANNEL_ID = "countdown_notification_channel"
    const val NOTIFICATION_ID = 1001

    fun createNotificationChannel(context: Context) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val name = "Countdown Notifications"
            val descriptionText = "Shows permanent ongoing countdown notifications"
            val importance = NotificationManager.IMPORTANCE_DEFAULT
            val channel = NotificationChannel(CHANNEL_ID, name, importance).apply {
                description = descriptionText
                setShowBadge(false)
            }
            val notificationManager: NotificationManager =
                context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager

            notificationManager.createNotificationChannel(channel)
        }
    }

    fun showCountdownNotification(
        context: Context,
        eventTitle: String,
        targetDateMillis: Long,
    ) {
        createNotificationChannel(context)

        val daysRemaining = CountdownUtils.calculateDaysRemaining(targetDateMillis)
        val components = CountdownUtils.getNotificationComponents(eventTitle, daysRemaining)
        val daysSummaryText = "${components.daysText} ${components.collapsedLabelText}".trim()

        val preferences = CountdownPreferences(context)
        val textColorInt = preferences.textColorInt

        // Collapsed (minimized) view: Only remaining days & collapsed label ("X Days")
        val collapsedView = RemoteViews(context.packageName, R.layout.notification_countdown_collapsed).apply {
            setTextViewText(R.id.tvNotificationDays, components.daysText)
            setTextViewText(R.id.tvNotificationLabel, components.collapsedLabelText)

            if (textColorInt != 0) {
                setTextColor(R.id.tvNotificationDays, textColorInt)
                setTextColor(R.id.tvNotificationLabel, textColorInt)
            }
        }

        // Expanded view: Days, label, and event title ("X days till Event")
        val expandedView = RemoteViews(context.packageName, R.layout.notification_countdown).apply {
            setTextViewText(R.id.tvNotificationDays, components.daysText)
            setTextViewText(R.id.tvNotificationLabel, components.labelText)
            setTextViewText(R.id.tvNotificationTitle, components.titleText)

            if (textColorInt != 0) {
                setTextColor(R.id.tvNotificationDays, textColorInt)
                setTextColor(R.id.tvNotificationLabel, textColorInt)
                setTextColor(R.id.tvNotificationTitle, textColorInt)
            }
        }

        val contentIntent = Intent(context, MainActivity::class.java).apply {
            flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TOP
        }

        val pendingIntentFlags = PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE

        val contentPendingIntent = PendingIntent.getActivity(
            context,
            0,
            contentIntent,
            pendingIntentFlags,
        )

        val deleteIntent = Intent(context, NotificationDismissedReceiver::class.java).apply {
            action = NotificationDismissedReceiver.ACTION_NOTIFICATION_DISMISSED
        }

        val deletePendingIntent = PendingIntent.getBroadcast(
            context,
            0,
            deleteIntent,
            pendingIntentFlags,
        )

        val builder = NotificationCompat.Builder(context, CHANNEL_ID)
            .setSmallIcon(R.drawable.ic_launcher_foreground)
            .setCustomContentView(collapsedView)
            .setCustomBigContentView(expandedView)
            .setStyle(NotificationCompat.DecoratedCustomViewStyle())
            .setContentTitle(daysSummaryText)
            .setPriority(NotificationCompat.PRIORITY_DEFAULT)
            .setContentIntent(contentPendingIntent)
            .setDeleteIntent(deletePendingIntent)
            .setOngoing(true)
            .setOnlyAlertOnce(true)
            .setAutoCancel(false)
            .setCategory(NotificationCompat.CATEGORY_EVENT)

        val notificationManager = NotificationManagerCompat.from(context)
        try {
            notificationManager.notify(NOTIFICATION_ID, builder.build())
        } catch (e: SecurityException) {
            e.printStackTrace()
        }
    }

    fun cancelNotification(context: Context) {
        val notificationManager = NotificationManagerCompat.from(context)
        notificationManager.cancel(NOTIFICATION_ID)
    }
}
