package com.example.naturalsound.utils

import android.content.Context
import android.content.res.Resources
import java.util.Locale

object LanguageHelper {
    fun changeLanguage(
        context: Context,
        resources: Resources,
        languageCode: String = Constants.Languages.ENG
    ) {
        val config = resources.configuration
        val locale = Locale(languageCode)
        Locale.setDefault(locale)
        config.setLocale(locale)
        context.createConfigurationContext(config)
        resources.updateConfiguration(config, resources.displayMetrics)
    }

}