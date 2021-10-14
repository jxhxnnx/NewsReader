package at.technikum_wien.if19b173.newsreader

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map


data class UserPreferences(val url : String, val displayImages : Boolean)

private const val USER_PREFERENCES_NAME = "user_preferences"

val Context.dataStore by preferencesDataStore(
    name = USER_PREFERENCES_NAME
)

class UserPreferencesRepository(private val userPreferencesDataStore : DataStore<Preferences>) {
    private object PreferencesKeys {
        val URL = stringPreferencesKey("url")
        val DISPLAYIMAGES = booleanPreferencesKey("displayImages")
    }

    val userPreferencesFlow : Flow<UserPreferences> = userPreferencesDataStore.data
        .map { preferences ->
            val url = preferences[PreferencesKeys.URL] ?: ""
            val displayImages = preferences[PreferencesKeys.DISPLAYIMAGES] ?: true
            UserPreferences(url = url, displayImages = displayImages)
        }

    suspend fun updateUrl(newUrl : String) {
        userPreferencesDataStore.edit { preferences ->
            preferences[PreferencesKeys.URL] = newUrl
        }
    }

    suspend fun updateDisplayImages(newDisplayImages : Boolean) {
        userPreferencesDataStore.edit { preferences ->
            preferences[PreferencesKeys.DISPLAYIMAGES] = newDisplayImages
        }
    }
}