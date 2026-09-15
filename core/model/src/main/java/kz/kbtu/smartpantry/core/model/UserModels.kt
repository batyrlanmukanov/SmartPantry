package kz.kbtu.smartpantry.core.model

enum class AppLanguage {
    ENGLISH,
    RUSSIAN,
}

enum class AppThemeMode {
    SYSTEM,
    LIGHT,
    DARK,
}

data class UserSession(
    val displayName: String,
    val email: String,
)

data class UserProfile(
    val displayName: String = "",
    val email: String = "",
    val householdSize: Int = 1,
    val reminderHour: Int = 19,
    val monthlySavingsGoal: Double = 10000.0,
    val foodPreference: String = "Balanced",
    val appLanguage: AppLanguage = AppLanguage.ENGLISH,
    val themeMode: AppThemeMode = AppThemeMode.SYSTEM,
)
