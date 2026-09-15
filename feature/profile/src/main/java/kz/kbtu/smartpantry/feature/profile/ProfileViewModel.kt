package kz.kbtu.smartpantry.feature.profile

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kz.kbtu.smartpantry.core.domain.ObserveInventoryUseCase
import kz.kbtu.smartpantry.core.domain.ObserveProfileUseCase
import kz.kbtu.smartpantry.core.domain.SaveProfileUseCase
import kz.kbtu.smartpantry.core.domain.SignOutUseCase
import kz.kbtu.smartpantry.core.model.AppLanguage
import kz.kbtu.smartpantry.core.model.AppThemeMode
import kz.kbtu.smartpantry.core.model.FoodStatus
import kz.kbtu.smartpantry.core.model.UserProfile

const val PROFILE_ROUTE = "profile"

data class EditableProfileState(
    val displayName: String = "",
    val email: String = "",
    val householdSize: String = "1",
    val reminderHour: String = "19",
    val monthlySavingsGoal: String = "10000",
    val foodPreference: String = "Balanced",
    val appLanguage: AppLanguage = AppLanguage.ENGLISH,
    val themeMode: AppThemeMode = AppThemeMode.SYSTEM,
)

data class ProfileStats(
    val savedProducts: Int = 0,
    val usedProducts: Int = 0,
    val wastedProducts: Int = 0,
)

@HiltViewModel
class ProfileViewModel @Inject constructor(
    observeProfileUseCase: ObserveProfileUseCase,
    observeInventoryUseCase: ObserveInventoryUseCase,
    private val saveProfileUseCase: SaveProfileUseCase,
    private val signOutUseCase: SignOutUseCase,
) : ViewModel() {

    val profile = observeProfileUseCase().stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5_000),
        initialValue = UserProfile(),
    )

    val stats = observeInventoryUseCase().combine(profile) { inventory, _ ->
        ProfileStats(
            savedProducts = inventory.count { it.status == FoodStatus.ACTIVE },
            usedProducts = inventory.count { it.status == FoodStatus.USED },
            wastedProducts = inventory.count { it.status == FoodStatus.WASTED },
        )
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5_000),
        initialValue = ProfileStats(),
    )

    private val _editor = MutableStateFlow(EditableProfileState())
    val editor: StateFlow<EditableProfileState> = _editor.asStateFlow()

    fun syncFromProfile(profile: UserProfile) {
        if (_editor.value.email == profile.email && _editor.value.displayName == profile.displayName) {
            return
        }
        _editor.value = EditableProfileState(
            displayName = profile.displayName,
            email = profile.email,
            householdSize = profile.householdSize.toString(),
            reminderHour = profile.reminderHour.toString(),
            monthlySavingsGoal = profile.monthlySavingsGoal.toInt().toString(),
            foodPreference = profile.foodPreference,
            appLanguage = profile.appLanguage,
            themeMode = profile.themeMode,
        )
    }

    fun updateDisplayName(value: String) = _editor.update { it.copy(displayName = value) }
    fun updateEmail(value: String) = _editor.update { it.copy(email = value) }
    fun updateHouseholdSize(value: String) = _editor.update { it.copy(householdSize = value) }
    fun updateReminderHour(value: String) = _editor.update { it.copy(reminderHour = value) }
    fun updateGoal(value: String) = _editor.update { it.copy(monthlySavingsGoal = value) }
    fun updatePreference(value: String) = _editor.update { it.copy(foodPreference = value) }
    fun updateLanguage(value: AppLanguage) = _editor.update { it.copy(appLanguage = value) }
    fun updateThemeMode(value: AppThemeMode) = _editor.update { it.copy(themeMode = value) }

    fun saveProfile() {
        val snapshot = editor.value
        viewModelScope.launch {
            saveProfileUseCase(
                UserProfile(
                    displayName = snapshot.displayName,
                    email = snapshot.email,
                    householdSize = snapshot.householdSize.toIntOrNull() ?: 1,
                    reminderHour = snapshot.reminderHour.toIntOrNull() ?: 19,
                    monthlySavingsGoal = snapshot.monthlySavingsGoal.toDoubleOrNull() ?: 10000.0,
                    foodPreference = snapshot.foodPreference,
                    appLanguage = snapshot.appLanguage,
                    themeMode = snapshot.themeMode,
                ),
            )
        }
    }

    fun signOut() {
        viewModelScope.launch {
            signOutUseCase()
        }
    }
}
