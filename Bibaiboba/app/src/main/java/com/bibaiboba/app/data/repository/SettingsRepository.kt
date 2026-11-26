package com.bibaiboba.app.data.repository

import android.content.Context
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import com.bibaiboba.app.BuildConfig
import com.bibaiboba.app.data.model.AppSettings
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

private val Context.settingsDataStore by preferencesDataStore(name = "bibaiboba_settings")

class SettingsRepository(private val context: Context) {

    private object Keys {
        val API_BASE_URL = stringPreferencesKey("api_base_url")
        val API_KEY = stringPreferencesKey("api_key")
        val MODEL = stringPreferencesKey("model")
        val USER_NAME = stringPreferencesKey("user_name")
        val PERSONA = stringPreferencesKey("assistant_persona")
        val HAPTICS = booleanPreferencesKey("haptics_enabled")
        val ANIMATIONS = booleanPreferencesKey("animations_enabled")
    }

    val settingsFlow: Flow<AppSettings> = context.settingsDataStore.data.map { prefs ->
        AppSettings(
            apiBaseUrl = prefs[Keys.API_BASE_URL] ?: BuildConfig.DEFAULT_API_BASE_URL,
            apiKey = prefs[Keys.API_KEY] ?: BuildConfig.DEFAULT_API_KEY,
            model = prefs[Keys.MODEL] ?: BuildConfig.DEFAULT_API_MODEL,
            userName = prefs[Keys.USER_NAME] ?: "",
            assistantPersona = prefs[Keys.PERSONA] ?: AppSettings(
                apiBaseUrl = BuildConfig.DEFAULT_API_BASE_URL,
                apiKey = BuildConfig.DEFAULT_API_KEY,
                model = BuildConfig.DEFAULT_API_MODEL
            ).assistantPersona,
            hapticsEnabled = prefs[Keys.HAPTICS] ?: true,
            animationsEnabled = prefs[Keys.ANIMATIONS] ?: true
        )
    }

    suspend fun updateApiBaseUrl(value: String) = context.settingsDataStore.edit { it[Keys.API_BASE_URL] = value }
    suspend fun updateApiKey(value: String) = context.settingsDataStore.edit { it[Keys.API_KEY] = value }
    suspend fun updateModel(value: String) = context.settingsDataStore.edit { it[Keys.MODEL] = value }
    suspend fun updateUserName(value: String) = context.settingsDataStore.edit { it[Keys.USER_NAME] = value }
    suspend fun updatePersona(value: String) = context.settingsDataStore.edit { it[Keys.PERSONA] = value }
    suspend fun updateHaptics(value: Boolean) = context.settingsDataStore.edit { it[Keys.HAPTICS] = value }
    suspend fun updateAnimations(value: Boolean) = context.settingsDataStore.edit { it[Keys.ANIMATIONS] = value }
}
