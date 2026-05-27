package org.censusmate.mobile.data.local

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

enum class Theme { SYSTEM, LIGHT, DARK }

private val Context.themeDataStore: DataStore<Preferences> by preferencesDataStore("theme_prefs")

class ThemeDataStore(private val context: Context) {
    companion object {
        private val THEME_KEY = stringPreferencesKey("app_theme")
    }

    val themeFlow: Flow<Theme> = context.themeDataStore.data.map { prefs ->
            when (prefs[THEME_KEY]) {
                "LIGHT" -> Theme.LIGHT
                "DARK" -> Theme.DARK
                else -> Theme.SYSTEM
            }
        }

    suspend fun saveTheme(theme: Theme) {
        context.themeDataStore.edit { it[THEME_KEY] = theme.name }
    }
}