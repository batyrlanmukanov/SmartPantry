package kz.kbtu.smartpantry.core.data

import android.content.Context
import androidx.datastore.preferences.core.edit
import dagger.hilt.android.qualifiers.ApplicationContext
import javax.inject.Inject
import javax.inject.Singleton
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import kz.kbtu.smartpantry.core.model.AppLanguage
import kz.kbtu.smartpantry.core.model.AppThemeMode
import kz.kbtu.smartpantry.core.domain.ProfileRepository
import kz.kbtu.smartpantry.core.model.UserProfile

@Singleton
class LocalProfileRepository @Inject constructor(
    @ApplicationContext private val context: Context,
) : ProfileRepository {

    override fun observeProfile(): Flow<UserProfile> =
        context.smartPantryDataStore.data.map { preferences ->
            UserProfile(
                displayName = preferences[PreferenceKeys.profileName]
                    ?: preferences[PreferenceKeys.sessionName].orEmpty(),
                email = preferences[PreferenceKeys.profileEmail]
                    ?: preferences[PreferenceKeys.sessionEmail].orEmpty(),
                householdSize = preferences[PreferenceKeys.householdSize] ?: 1,
                reminderHour = preferences[PreferenceKeys.reminderHour] ?: 19,
                monthlySavingsGoal = preferences[PreferenceKeys.savingsGoal] ?: 10000.0,
                foodPreference = preferences[PreferenceKeys.preference] ?: "Balanced",
                appLanguage = preferences[PreferenceKeys.appLanguage]
                    ?.let(AppLanguage::valueOf)
                    ?: AppLanguage.ENGLISH,
                themeMode = preferences[PreferenceKeys.appThemeMode]
                    ?.let(AppThemeMode::valueOf)
                    ?: AppThemeMode.SYSTEM,
            )
        }

    override suspend fun saveProfile(profile: UserProfile) {
        context.smartPantryDataStore.edit { preferences ->
            preferences[PreferenceKeys.profileName] = profile.displayName
            preferences[PreferenceKeys.profileEmail] = profile.email
            preferences[PreferenceKeys.householdSize] = profile.householdSize
            preferences[PreferenceKeys.reminderHour] = profile.reminderHour
            preferences[PreferenceKeys.savingsGoal] = profile.monthlySavingsGoal
            preferences[PreferenceKeys.preference] = profile.foodPreference
            preferences[PreferenceKeys.appLanguage] = profile.appLanguage.name
            preferences[PreferenceKeys.appThemeMode] = profile.themeMode.name
        }
    }
}
