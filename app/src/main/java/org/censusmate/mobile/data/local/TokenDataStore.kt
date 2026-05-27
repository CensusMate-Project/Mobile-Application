package org.censusmate.mobile.data.local

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.flow.map

private val Context.dataStore: DataStore<Preferences> by preferencesDataStore("auth_prefs")

class TokenDataStore(private val context: Context) {
    companion object {
        private val TOKEN_KEY = stringPreferencesKey("access_token")
    }

    val tokenFlow: Flow<String?> = context.dataStore.data.map { it[TOKEN_KEY] }

    suspend fun saveToken(token: String) {
        context.dataStore.edit { it[TOKEN_KEY] = token }
    }

    suspend fun clear() {
        context.dataStore.edit { it.remove(TOKEN_KEY) }
    }

    suspend fun get(): String? = tokenFlow.firstOrNull()
}