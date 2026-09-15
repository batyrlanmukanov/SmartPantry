package kz.kbtu.smartpantry.feature.profile

import android.content.pm.ApplicationInfo
import android.widget.Toast
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.FilterChip
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import kz.kbtu.smartpantry.core.model.AppLanguage
import kz.kbtu.smartpantry.core.model.AppThemeMode
import kz.kbtu.smartpantry.core.ui.MetricCard
import kz.kbtu.smartpantry.core.ui.SectionTitle
import kz.kbtu.smartpantry.core.ui.themeModeLabel
import kz.kbtu.smartpantry.core.ui.tr

@OptIn(ExperimentalLayoutApi::class)
@Composable
fun ProfileScreen(
    viewModel: ProfileViewModel = hiltViewModel(),
) {
    val context = LocalContext.current
    val isDebuggable = (context.applicationInfo.flags and ApplicationInfo.FLAG_DEBUGGABLE) != 0
    val reminderHelpMessage = tr(
        "Set reminder hour to current time, keep an active item with 1-2 days left, then wait for worker run.",
        "Поставь час напоминания на текущий, оставь активный товар с 1-2 днями до срока и дождись запуска воркера.",
    )
    val reminderToastMessage = tr(
        "Reminder test guide shown.",
        "Подсказка по тесту уведомлений показана.",
    )
    val profile by viewModel.profile.collectAsStateWithLifecycle()
    val editor by viewModel.editor.collectAsStateWithLifecycle()
    val stats by viewModel.stats.collectAsStateWithLifecycle()

    LaunchedEffect(profile) {
        viewModel.syncFromProfile(profile)
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp),
    ) {
        SectionTitle(tr("Profile", "Профиль"))
        Text(
            text = tr(
                "This profile powers reminders, savings goals, assistant behavior, language, and theme.",
                "Этот профиль управляет напоминаниями, целью по экономии, поведением ассистента, языком и темой приложения.",
            ),
            style = MaterialTheme.typography.bodyMedium,
        )

        MetricCard(
            title = tr("Saved products", "Активные продукты"),
            value = stats.savedProducts.toString(),
            subtitle = tr("Currently available in inventory", "Сейчас доступны в запасах"),
            modifier = Modifier.fillMaxWidth(),
        )
        MetricCard(
            title = tr("Used products", "Использовано"),
            value = stats.usedProducts.toString(),
            subtitle = tr("Marked as consumed", "Отмечены как использованные"),
            modifier = Modifier.fillMaxWidth(),
        )
        MetricCard(
            title = tr("Wasted products", "Списано"),
            value = stats.wastedProducts.toString(),
            subtitle = tr("Marked as wasted", "Отмечены как списанные"),
            modifier = Modifier.fillMaxWidth(),
        )

        Text(tr("Language", "Язык"), style = MaterialTheme.typography.titleMedium)
        FlowRow(
            horizontalArrangement = Arrangement.spacedBy(8.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp),
        ) {
            FilterChip(
                selected = editor.appLanguage == AppLanguage.ENGLISH,
                onClick = { viewModel.updateLanguage(AppLanguage.ENGLISH) },
                label = { Text("English") },
            )
            FilterChip(
                selected = editor.appLanguage == AppLanguage.RUSSIAN,
                onClick = { viewModel.updateLanguage(AppLanguage.RUSSIAN) },
                label = { Text("Русский") },
            )
        }

        Text(tr("Theme", "Тема"), style = MaterialTheme.typography.titleMedium)
        FlowRow(
            horizontalArrangement = Arrangement.spacedBy(8.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp),
        ) {
            AppThemeMode.entries.forEach { mode ->
                FilterChip(
                    selected = editor.themeMode == mode,
                    onClick = { viewModel.updateThemeMode(mode) },
                    label = { Text(themeModeLabel(mode)) },
                )
            }
        }

        OutlinedTextField(
            value = editor.displayName,
            onValueChange = viewModel::updateDisplayName,
            label = { Text(tr("Display name", "Имя")) },
            modifier = Modifier.fillMaxWidth(),
        )
        OutlinedTextField(
            value = editor.email,
            onValueChange = viewModel::updateEmail,
            label = { Text(tr("Email", "Почта")) },
            modifier = Modifier.fillMaxWidth(),
        )
        OutlinedTextField(
            value = editor.householdSize,
            onValueChange = viewModel::updateHouseholdSize,
            label = { Text(tr("Household size", "Размер семьи")) },
            modifier = Modifier.fillMaxWidth(),
        )
        OutlinedTextField(
            value = editor.reminderHour,
            onValueChange = viewModel::updateReminderHour,
            label = { Text(tr("Reminder hour", "Час напоминания")) },
            supportingText = {
                Text(tr("24-hour format, for example 19 = 7 PM", "Формат 24 часа, например 19 = 19:00"))
            },
            modifier = Modifier.fillMaxWidth(),
        )
        OutlinedTextField(
            value = editor.monthlySavingsGoal,
            onValueChange = viewModel::updateGoal,
            label = { Text(tr("Monthly savings goal (KZT)", "Цель экономии в месяц (KZT)")) },
            modifier = Modifier.fillMaxWidth(),
        )
        OutlinedTextField(
            value = editor.foodPreference,
            onValueChange = viewModel::updatePreference,
            label = { Text(tr("Food preference", "Пищевые предпочтения")) },
            supportingText = {
                Text(
                    tr(
                        "Examples: Balanced, Vegetarian, Halal, High protein",
                        "Примеры: Balanced, Vegetarian, Halal, High protein",
                    ),
                )
            },
            modifier = Modifier.fillMaxWidth(),
        )

        Button(
            onClick = viewModel::saveProfile,
            modifier = Modifier.fillMaxWidth(),
        ) {
            Text(tr("Save profile", "Сохранить профиль"))
        }

        if (isDebuggable) {
            Button(
                onClick = {
                    Toast.makeText(
                        context,
                        reminderToastMessage,
                        Toast.LENGTH_SHORT,
                    ).show()
                },
                modifier = Modifier.fillMaxWidth(),
            ) {
                Text(tr("Show reminder test guide", "Показать гайд по тесту напоминаний"))
            }
            Text(reminderHelpMessage, style = MaterialTheme.typography.bodySmall)
        }

        Button(
            onClick = viewModel::signOut,
            modifier = Modifier.fillMaxWidth(),
        ) {
            Text(tr("Sign out", "Выйти"))
        }
    }
}
