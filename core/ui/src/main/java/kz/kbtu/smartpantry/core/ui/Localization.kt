package kz.kbtu.smartpantry.core.ui

import androidx.compose.runtime.Composable
import androidx.compose.runtime.compositionLocalOf
import kz.kbtu.smartpantry.core.model.AppLanguage
import kz.kbtu.smartpantry.core.model.AppThemeMode
import kz.kbtu.smartpantry.core.model.FoodCategory
import kz.kbtu.smartpantry.core.model.FoodStatus
import kz.kbtu.smartpantry.core.model.StorageLocation

val LocalAppLanguage = compositionLocalOf { AppLanguage.ENGLISH }

@Composable
fun tr(
    english: String,
    russian: String,
): String = if (LocalAppLanguage.current == AppLanguage.RUSSIAN) russian else english

@Composable
fun categoryLabel(category: FoodCategory): String = when (category) {
    FoodCategory.PRODUCE -> tr("Produce", "Овощи и фрукты")
    FoodCategory.DAIRY -> tr("Dairy", "Молочные")
    FoodCategory.PROTEIN -> tr("Protein", "Белки")
    FoodCategory.MEAT -> tr("Meat", "Мясо")
    FoodCategory.PANTRY -> tr("Pantry", "Бакалея")
    FoodCategory.FROZEN -> tr("Frozen", "Заморозка")
    FoodCategory.BEVERAGE -> tr("Beverage", "Напитки")
    FoodCategory.LEFTOVERS -> tr("Leftovers", "Готовая еда")
}

@Composable
fun storageLabel(location: StorageLocation): String = when (location) {
    StorageLocation.FRIDGE -> tr("Fridge", "Холодильник")
    StorageLocation.FREEZER -> tr("Freezer", "Морозилка")
    StorageLocation.CUPBOARD -> tr("Cupboard", "Шкаф")
    StorageLocation.COUNTER -> tr("Counter", "Стол")
}

@Composable
fun statusLabel(status: FoodStatus): String = when (status) {
    FoodStatus.ACTIVE -> tr("Active", "Активный")
    FoodStatus.USED -> tr("Used", "Использован")
    FoodStatus.WASTED -> tr("Wasted", "Списан")
}

@Composable
fun themeModeLabel(themeMode: AppThemeMode): String = when (themeMode) {
    AppThemeMode.SYSTEM -> tr("System", "Системная")
    AppThemeMode.LIGHT -> tr("Light", "Светлая")
    AppThemeMode.DARK -> tr("Dark", "Темная")
}

@Composable
fun foodNameLabel(name: String): String = when (name.trim().lowercase()) {
    "milk" -> tr("Milk", "Молоко")
    "tomatoes" -> tr("Tomatoes", "Помидоры")
    "cooked rice" -> tr("Cooked rice", "Готовый рис")
    "eggs" -> tr("Eggs", "Яйца")
    "yogurt" -> tr("Yogurt", "Йогурт")
    else -> name
}

@Composable
fun unitLabel(unit: String): String = when (unit.trim().lowercase()) {
    "pcs" -> tr("pcs", "шт.")
    "pack" -> tr("pack", "уп.")
    "box" -> tr("box", "конт.")
    else -> unit
}

@Composable
fun shoppingSuggestionLabel(suggestion: String): String = when (suggestion) {
    "Fresh vegetables" -> tr("Fresh vegetables", "Свежие овощи")
    "Milk or yogurt" -> tr("Milk or yogurt", "Молоко или йогурт")
    "Rice or pasta" -> tr("Rice or pasta", "Рис или паста")
    "Eggs or chicken" -> tr("Eggs or chicken", "Яйца или курица")
    "Basic staples" -> tr("Basic staples", "Базовые продукты")
    else -> suggestion
}
