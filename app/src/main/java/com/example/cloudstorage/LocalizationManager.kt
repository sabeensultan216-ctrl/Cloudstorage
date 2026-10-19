package com.example.cloudstorage

import android.content.Context
import android.content.res.Configuration
import androidx.compose.runtime.State
import androidx.compose.runtime.mutableStateOf
import java.util.Locale

object LocalizationManager {
    private const val PREFS_NAME = "cloudbox_preferences"
    private const val KEY_LANGUAGE = "app_language"

    private val _currentLanguage = mutableStateOf("en")
    val currentLanguage: State<String> = _currentLanguage

    fun init(context: Context) {
        val savedLang = getSavedLanguage(context)
        _currentLanguage.value = savedLang
    }

    fun getSavedLanguage(context: Context): String {
        val prefs = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
        return prefs.getString(KEY_LANGUAGE, "en") ?: "en"
    }

    fun setLanguage(context: Context, langCode: String) {
        val prefs = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
        prefs.edit().putString(KEY_LANGUAGE, langCode).commit()

        val locale = getLocale(langCode)
        Locale.setDefault(locale)
        val config = Configuration(context.resources.configuration).apply {
            setLocale(locale)
            setLayoutDirection(locale)
        }
        @Suppress("DEPRECATION")
        context.resources.updateConfiguration(config, context.resources.displayMetrics)
        @Suppress("DEPRECATION")
        context.applicationContext?.resources?.updateConfiguration(config, context.applicationContext.resources.displayMetrics)
        _currentLanguage.value = langCode
    }

    fun getLocale(langCode: String): Locale {
        return when (langCode) {
            "zh" -> Locale.SIMPLIFIED_CHINESE
            "fr" -> Locale.FRENCH
            "de" -> Locale.GERMAN
            "en" -> Locale.ENGLISH
            else -> Locale.forLanguageTag(langCode)
        }
    }

    fun isRtl(langCode: String): Boolean {
        return langCode == "ar" || langCode == "ur"
    }

    fun applyLocale(context: Context): Context {
        val langCode = getSavedLanguage(context)
        val locale = getLocale(langCode)
        Locale.setDefault(locale)
        val config = Configuration(context.resources.configuration).apply {
            setLocale(locale)
            setLayoutDirection(locale)
        }
        return context.createConfigurationContext(config)
    }
}