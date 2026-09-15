package kz.kbtu.smartpantry.core.data

import android.content.Context
import androidx.datastore.preferences.core.doublePreferencesKey
import androidx.datastore.preferences.core.intPreferencesKey
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.core.stringSetPreferencesKey
import androidx.datastore.preferences.preferencesDataStore

val Context.smartPantryDataStore by preferencesDataStore("smart_pantry_preferences")

object PreferenceKeys {
    val sessionName = stringPreferencesKey("session_name")
    val sessionEmail = stringPreferencesKey("session_email")
    val authRegisteredName = stringPreferencesKey("auth_registered_name")
    val authRegisteredEmail = stringPreferencesKey("auth_registered_email")
    val authRegisteredPassword = stringPreferencesKey("auth_registered_password")
    val profileName = stringPreferencesKey("profile_name")
    val profileEmail = stringPreferencesKey("profile_email")
    val householdSize = intPreferencesKey("household_size")
    val reminderHour = intPreferencesKey("reminder_hour")
    val savingsGoal = doublePreferencesKey("savings_goal")
    val preference = stringPreferencesKey("food_preference")
    val appLanguage = stringPreferencesKey("app_language")
    val appThemeMode = stringPreferencesKey("app_theme_mode")
    val shoppingList = stringSetPreferencesKey("shopping_list_items")
}
