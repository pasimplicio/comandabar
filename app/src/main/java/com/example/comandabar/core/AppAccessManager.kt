package com.example.comandabar.core

import android.content.Context

object AppAccessManager {

    private const val PREFS = "app_access_prefs"
    private const val KEY_FIRST_OPEN = "first_open_time"
    private const val TRIAL_DAYS = 7L

    fun isAppBlocked(context: Context): Boolean {
        val prefs = context.getSharedPreferences(PREFS, Context.MODE_PRIVATE)

        val firstOpen = prefs.getLong(KEY_FIRST_OPEN, -1L)
        val now = System.currentTimeMillis()

        if (firstOpen == -1L) {
            prefs.edit().putLong(KEY_FIRST_OPEN, now).apply()
            return false
        }

        val daysPassed =
            (now - firstOpen) / (1000 * 60 * 60 * 24)

        return daysPassed > TRIAL_DAYS
    }
}
