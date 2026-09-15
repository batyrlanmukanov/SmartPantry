package kz.kbtu.smartpantry.feature.assistant

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import kz.kbtu.smartpantry.core.model.AppLanguage
import kz.kbtu.smartpantry.core.model.RecipeDuration
import kz.kbtu.smartpantry.core.ui.LocalAppLanguage
import kz.kbtu.smartpantry.core.ui.QuickChips
import kz.kbtu.smartpantry.core.ui.SectionTitle
import kz.kbtu.smartpantry.core.ui.tr

@Composable
fun AssistantScreen(
    viewModel: AssistantViewModel = hiltViewModel(),
) {
    val insights by viewModel.insights.collectAsStateWithLifecycle()
    val state by viewModel.state.collectAsStateWithLifecycle()
    val language = LocalAppLanguage.current

    LaunchedEffect(language) {
        viewModel.ensureGreeting(language)
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp),
    ) {
        SectionTitle(tr("AI Assistant", "AI Ассистент"))
        Text(
            text = tr(
                "A practical chat assistant that helps you cook from inventory and reduce waste.",
                "Практичный чат-ассистент, который помогает готовить из текущих запасов и снижать потери.",
            ),
            style = MaterialTheme.typography.bodyMedium,
        )

        QuickChips(
            options = if (language == AppLanguage.RUSSIAN) {
                listOf(
                    "Готовь сейчас (1-2 дня)",
                    "Что приготовить?",
                    "Что скоро испортится?",
                    "Как экономить деньги?",
                )
            } else {
                listOf(
                    "Cook now (1-2 days)",
                    "What can I cook?",
                    "What expires soon?",
                    "How can I save money?",
                )
            },
            onClick = viewModel::sendPrompt,
        )
        Button(
            onClick = {
                viewModel.sendPrompt(
                    if (language == AppLanguage.RUSSIAN) "готовь сейчас"
                    else "cook now",
                )
            },
            modifier = Modifier.fillMaxWidth(),
        ) {
            Text(tr("Cook now mode", "Режим готовь сейчас"))
        }

        LazyColumn(
            modifier = Modifier.weight(1f),
            verticalArrangement = Arrangement.spacedBy(10.dp),
        ) {
            items(state.messages) { message ->
                Card(modifier = Modifier.fillMaxWidth()) {
                    Column(
                        modifier = Modifier.padding(16.dp),
                        verticalArrangement = Arrangement.spacedBy(8.dp),
                    ) {
                        Text(
                            text = if (message.fromUser) tr("You", "Ты") else tr("Assistant", "Ассистент"),
                            style = MaterialTheme.typography.titleSmall,
                        )
                        Text(message.text, style = MaterialTheme.typography.bodyMedium)
                        message.recipes.forEach { recipe ->
                            Card(modifier = Modifier.fillMaxWidth()) {
                                Column(
                                    modifier = Modifier.padding(12.dp),
                                    verticalArrangement = Arrangement.spacedBy(4.dp),
                                ) {
                                    Text(recipe.title, style = MaterialTheme.typography.titleMedium)
                                    Text(
                                        text = tr("Time", "Время") + ": " + when (recipe.duration) {
                                            RecipeDuration.FAST -> tr("Fast", "Быстро")
                                            RecipeDuration.MEDIUM -> tr("Medium", "Средне")
                                            RecipeDuration.LONG -> tr("Long", "Долго")
                                        },
                                    )
                                    Text(text = tr("Calories", "Калории") + ": ${recipe.calories}")
                                    Text(recipe.reason)
                                }
                            }
                        }
                    }
                }
            }

            if (insights.isNotEmpty()) {
                item {
                    Text(
                        text = tr("Live assistant signals", "Живые сигналы ассистента"),
                        style = MaterialTheme.typography.titleMedium,
                    )
                }
                items(insights.take(2)) { insight ->
                    Card(modifier = Modifier.fillMaxWidth()) {
                        Column(
                            modifier = Modifier.padding(12.dp),
                            verticalArrangement = Arrangement.spacedBy(6.dp),
                        ) {
                            Text(insight.title, style = MaterialTheme.typography.titleSmall)
                            Text(insight.message, style = MaterialTheme.typography.bodySmall)
                        }
                    }
                }
            }
        }

        OutlinedTextField(
            value = state.input,
            onValueChange = viewModel::updateInput,
            label = { Text(tr("Ask the assistant", "Спроси ассистента")) },
            modifier = Modifier.fillMaxWidth(),
        )

        Button(
            onClick = { viewModel.sendPrompt(state.input) },
            modifier = Modifier.fillMaxWidth(),
        ) {
            Text(tr("Send", "Отправить"))
        }
    }
}
