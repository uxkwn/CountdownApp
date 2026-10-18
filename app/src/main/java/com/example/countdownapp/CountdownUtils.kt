package com.example.countdownapp

import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Date
import java.util.Locale
import java.util.concurrent.TimeUnit

object CountdownUtils {

    data class NotificationTextComponents(
        val daysText: String,
        val labelText: String,
        val collapsedLabelText: String,
        val titleText: String,
    )

    /**
     * Calculates calendar days between current date and target date.
     * Time components are set to midnight (start of day) to ensure precise calendar day calculations.
     */
    fun calculateDaysRemaining(
        targetDateMillis: Long,
        currentDateMillis: Long = System.currentTimeMillis(),
    ): Long {
        val currentCalendar = Calendar.getInstance().apply {
            timeInMillis = currentDateMillis
            set(Calendar.HOUR_OF_DAY, 0)
            set(Calendar.MINUTE, 0)
            set(Calendar.SECOND, 0)
            set(Calendar.MILLISECOND, 0)
        }

        val targetCalendar = Calendar.getInstance().apply {
            timeInMillis = targetDateMillis
            set(Calendar.HOUR_OF_DAY, 0)
            set(Calendar.MINUTE, 0)
            set(Calendar.SECOND, 0)
            set(Calendar.MILLISECOND, 0)
        }

        val diffMillis = targetCalendar.timeInMillis - currentCalendar.timeInMillis
        return TimeUnit.MILLISECONDS.toDays(diffMillis)
    }

    /**
     * Formats notification / display text depending on remaining days.
     */
    fun formatCountdownText(eventTitle: String, daysRemaining: Long): String {
        return when {
            daysRemaining > 1 -> "$daysRemaining days till $eventTitle"
            daysRemaining == 1L -> "1 day till $eventTitle"
            daysRemaining == 0L -> "$eventTitle is today!"
            daysRemaining == -1L -> "1 day since $eventTitle"
            else -> "${-daysRemaining} days since $eventTitle"
        }
    }

    /**
     * Extracts text components for custom notification view (large number, small label, large title).
     */
    fun getNotificationComponents(
        eventTitle: String,
        daysRemaining: Long,
    ): NotificationTextComponents {
        return when {
            daysRemaining > 1 -> NotificationTextComponents(
                daysText = daysRemaining.toString(),
                labelText = "days till",
                collapsedLabelText = "Days",
                titleText = eventTitle,
            )
            daysRemaining == 1L -> NotificationTextComponents(
                daysText = "1",
                labelText = "day till",
                collapsedLabelText = "Day",
                titleText = eventTitle,
            )
            daysRemaining == 0L -> NotificationTextComponents(
                daysText = "0",
                labelText = "days till (Today)",
                collapsedLabelText = "Days (Today)",
                titleText = eventTitle,
            )
            daysRemaining == -1L -> NotificationTextComponents(
                daysText = "1",
                labelText = "day since",
                collapsedLabelText = "Day since",
                titleText = eventTitle,
            )
            else -> NotificationTextComponents(
                daysText = (-daysRemaining).toString(),
                labelText = "days since",
                collapsedLabelText = "Days since",
                titleText = eventTitle,
            )
        }
    }

    /**
     * Formats timestamp into a user-friendly date string (e.g. "Oct 25, 2026").
     */
    fun formatDateString(timeInMillis: Long): String {
        if (timeInMillis == 0L) return ""
        val formatter = SimpleDateFormat("MMM dd, yyyy", Locale.getDefault())
        return formatter.format(Date(timeInMillis))
    }
}
