package kz.kbtu.smartpantry.core.ui

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.ColorScheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import kz.kbtu.smartpantry.core.model.AppThemeMode

private val LightColors: ColorScheme = lightColorScheme(
    primary = Color(0xFF2F6B3B),
    secondary = Color(0xFFDF8A00),
    tertiary = Color(0xFF00796B),
    background = Color(0xFFF7F7F2),
    surface = Color(0xFFFFFFFF),
)

private val DarkColors: ColorScheme = darkColorScheme(
    primary = Color(0xFF7BC17D),
    secondary = Color(0xFFFFB957),
    tertiary = Color(0xFF59C9B5),
)

@Composable
fun SmartPantryTheme(
    themeMode: AppThemeMode = AppThemeMode.SYSTEM,
    content: @Composable () -> Unit,
) {
    val darkTheme = when (themeMode) {
        AppThemeMode.SYSTEM -> isSystemInDarkTheme()
        AppThemeMode.LIGHT -> false
        AppThemeMode.DARK -> true
    }

    MaterialTheme(
        colorScheme = if (darkTheme) DarkColors else LightColors,
        content = content,
    )
}
