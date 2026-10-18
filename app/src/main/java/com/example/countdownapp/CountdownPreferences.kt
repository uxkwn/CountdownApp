package com.example.countdownapp

import android.content.Context
import android.content.SharedPreferences
import androidx.core.content.edit

class CountdownPreferences(context: Context) {

    private val prefs: SharedPreferences =
        context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)

    var eventTitle: String
        get() = prefs.getString(KEY_EVENT_TITLE, "") ?: ""
        set(value) = prefs.edit { putString(KEY_EVENT_TITLE, value) }

    var targetDateMillis: Long
        get() = prefs.getLong(KEY_TARGET_DATE_MILLIS, 0L)
        set(value) = prefs.edit { putLong(KEY_TARGET_DATE_MILLIS, value) }

    var isCountdownActive: Boolean
        get() = prefs.getBoolean(KEY_IS_ACTIVE, false)
        set(value) = prefs.edit { putBoolean(KEY_IS_ACTIVE, value) }

    var textColorInt: Int
        get() = prefs.getInt(KEY_TEXT_COLOR_INT, 0)
        set(value) = prefs.edit { putInt(KEY_TEXT_COLOR_INT, value) }

    fun saveCountdown(
        title: String,
        targetMillis: Long,
        colorInt: Int = textColorInt,
    ) {
        prefs.edit {
            putString(KEY_EVENT_TITLE, title)
            putLong(KEY_TARGET_DATE_MILLIS, targetMillis)
            putInt(KEY_TEXT_COLOR_INT, colorInt)
            putBoolean(KEY_IS_ACTIVE, true)
        }
    }

    fun clearCountdown() {
        prefs.edit {
            remove(KEY_EVENT_TITLE)
            remove(KEY_TARGET_DATE_MILLIS)
            remove(KEY_TEXT_COLOR_INT)
            putBoolean(KEY_IS_ACTIVE, false)
        }
    }

    companion object {
        private const val PREFS_NAME = "countdown_prefs"
        private const val KEY_EVENT_TITLE = "event_title"
        private const val KEY_TARGET_DATE_MILLIS = "target_date_millis"
        private const val KEY_IS_ACTIVE = "is_active"
        private const val KEY_TEXT_COLOR_INT = "text_color_int"
    }
}
