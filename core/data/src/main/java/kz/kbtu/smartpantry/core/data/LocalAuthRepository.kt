package kz.kbtu.smartpantry.core.data

import android.content.Context
import androidx.datastore.preferences.core.edit
import dagger.hilt.android.qualifiers.ApplicationContext
import javax.inject.Inject
import javax.inject.Singleton
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map
import kz.kbtu.smartpantry.core.domain.AuthRepository
import kz.kbtu.smartpantry.core.model.UserSession

@Singleton
class LocalAuthRepository @Inject constructor(
    @ApplicationContext private val context: Context,
) : AuthRepository {

    override fun observeSession(): Flow<UserSession?> =
        context.smartPantryDataStore.data.map { preferences ->
            val email = preferences[PreferenceKeys.sessionEmail].orEmpty()
            val name = preferences[PreferenceKeys.sessionName].orEmpty()
            if (email.isBlank()) {
                null
            } else {
                UserSession(
                    displayName = name,
                    email = email,
                )
            }
        }

    override suspend fun signIn(email: String, password: String): UserSession {
        val stored = context.smartPantryDataStore.data.map { preferences ->
            Triple(
                preferences[PreferenceKeys.authRegisteredName].orEmpty(),
                preferences[PreferenceKeys.authRegisteredEmail].orEmpty(),
                preferences[PreferenceKeys.authRegisteredPassword].orEmpty(),
            )
        }
        val (registeredName, registeredEmail, registeredPassword) = stored.first()

        if (registeredEmail.isBlank() || registeredPassword.isBlank()) {
            throw IllegalArgumentException("No account found. Please sign up first.")
        }
        if (email.trim() != registeredEmail || password != registeredPassword) {
            throw IllegalArgumentException("Invalid email or password.")
        }

        val session = UserSession(
            displayName = registeredName.ifBlank { email.substringBefore("@") },
            email = registeredEmail,
        )
        context.smartPantryDataStore.edit { preferences ->
            preferences[PreferenceKeys.sessionName] = session.displayName
            preferences[PreferenceKeys.sessionEmail] = session.email
        }
        return session
    }

    override suspend fun signUp(displayName: String, email: String, password: String): UserSession {
        val normalizedEmail = email.trim()
        val normalizedName = displayName.trim().ifBlank { normalizedEmail.substringBefore("@") }
        val session = UserSession(
            displayName = normalizedName,
            email = normalizedEmail,
        )

        context.smartPantryDataStore.edit { preferences ->
            preferences[PreferenceKeys.authRegisteredName] = normalizedName
            preferences[PreferenceKeys.authRegisteredEmail] = normalizedEmail
            preferences[PreferenceKeys.authRegisteredPassword] = password
            preferences[PreferenceKeys.sessionName] = session.displayName
            preferences[PreferenceKeys.sessionEmail] = session.email
        }
        return session
    }

    override suspend fun signOut() {
        context.smartPantryDataStore.edit { preferences ->
            preferences.remove(PreferenceKeys.sessionName)
            preferences.remove(PreferenceKeys.sessionEmail)
        }
    }
}
