package com.naturalsound.data.prefs

import android.content.Context
import dagger.hilt.android.qualifiers.ApplicationContext
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class UserPreferences @Inject constructor(
    @ApplicationContext context: Context
) {
    private val prefs = context.getSharedPreferences("ns_prefs", Context.MODE_PRIVATE)

    // ── Onboarding ────────────────────────────────────────────────────────────
    var isOnboardingDone: Boolean
        get() = prefs.getBoolean("onboarding_done", false)
        set(value) { prefs.edit().putBoolean("onboarding_done", value).apply() }

    var userName: String
        get() = prefs.getString("user_name", "") ?: ""
        set(value) { prefs.edit().putString("user_name", value).apply() }

    var languageCode: String
        get() = prefs.getString("language_code", "uz") ?: "uz"
        set(value) { prefs.edit().putString("language_code", value).apply() }

    // ── Tema ──────────────────────────────────────────────────────────────────
    /** "dark" yoki "light" */
    var themeMode: String
        get() = prefs.getString("theme_mode", "dark") ?: "dark"
        set(value) { prefs.edit().putString("theme_mode", value).apply() }

    // ── Statistika ────────────────────────────────────────────────────────────
    /** "yyyy-MM-dd" formatida bugungi sana */
    var todayDate: String
        get() = prefs.getString("today_date", "") ?: ""
        set(value) { prefs.edit().putString("today_date", value).apply() }

    /** Bugun tinglangan soniyalar */
    var todaySeconds: Int
        get() = prefs.getInt("today_seconds", 0)
        set(value) { prefs.edit().putInt("today_seconds", value).apply() }

    /** Bugungi sessiyalar soni (ovoz >0 bo'lgan ulanishlar) */
    var todaySessions: Int
        get() = prefs.getInt("today_sessions", 0)
        set(value) { prefs.edit().putInt("today_sessions", value).apply() }

    /** Ketma-ket kun seriyasi */
    var streakDays: Int
        get() = prefs.getInt("streak_days", 0)
        set(value) { prefs.edit().putInt("streak_days", value).apply() }

    /** Oxirgi foydalanish sanasi */
    var lastUsedDate: String
        get() = prefs.getString("last_used_date", "") ?: ""
        set(value) { prefs.edit().putString("last_used_date", value).apply() }

    /** Barcha ma'lumotlarni o'chirish (logout) */
    fun clearAll() { prefs.edit().clear().apply() }
}
