package kz.kbtu.smartpantry.feature.analytics

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Card
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import kz.kbtu.smartpantry.core.ui.MetricCard
import kz.kbtu.smartpantry.core.ui.SectionTitle
import kz.kbtu.smartpantry.core.ui.shoppingSuggestionLabel
import kz.kbtu.smartpantry.core.ui.tr

@Composable
fun AnalyticsScreen(
    viewModel: AnalyticsViewModel = hiltViewModel(),
) {
    val stats by viewModel.stats.collectAsStateWithLifecycle()

    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp),
    ) {
        SectionTitle(tr("Analytics", "Аналитика"))

        MetricCard(
            title = tr("Active items", "Активные продукты"),
            value = "${stats?.activeItems ?: 0}",
            subtitle = tr("Products currently being tracked", "Продукты, которые сейчас отслеживаются"),
            modifier = Modifier.fillMaxWidth(),
        )
        MetricCard(
            title = tr("Expiring soon", "Скоро истекают"),
            value = "${stats?.expiringSoonItems ?: 0}",
            subtitle = tr("Items that need attention in the next 3 days", "Продукты, которым нужно внимание в ближайшие 3 дня"),
            modifier = Modifier.fillMaxWidth(),
        )
        MetricCard(
            title = tr("Estimated saved amount", "Примерно сэкономлено"),
            value = "%.0f KZT".format(stats?.estimatedSavedAmount ?: 0.0),
            subtitle = tr("Used items minus wasted value", "Использованные продукты минус потери"),
            modifier = Modifier.fillMaxWidth(),
        )
        MetricCard(
            title = tr("Wasted amount", "Сумма потерь"),
            value = "%.0f KZT".format(stats?.wastedAmount ?: 0.0),
            subtitle = tr("Cost of products marked as wasted", "Стоимость продуктов со статусом списано"),
            modifier = Modifier.fillMaxWidth(),
        )
        MetricCard(
            title = tr("Used before expiry", "Использовано до срока"),
            value = "${stats?.usedBeforeExpiryPercent ?: 0}%",
            subtitle = tr("Share of used items before expiration date", "Доля использованных продуктов до истечения срока"),
            modifier = Modifier.fillMaxWidth(),
        )
        MetricCard(
            title = tr("Waste rate", "Доля потерь"),
            value = "${stats?.wasteRatePercent ?: 0}%",
            subtitle = tr("Share of items marked as wasted", "Доля продуктов, отмеченных как списанные"),
            modifier = Modifier.fillMaxWidth(),
        )
        MetricCard(
            title = tr("Anti-waste score", "Anti-waste score"),
            value = "${stats?.antiWasteScore ?: 0}/100",
            subtitle = tr("Higher is better", "Чем выше, тем лучше"),
            modifier = Modifier.fillMaxWidth(),
        )
        MetricCard(
            title = tr("Leftovers to use", "Остатки для использования"),
            value = "${stats?.leftoverItems ?: 0}",
            subtitle = tr("Prioritize cooked food first", "Сначала используй готовую еду"),
            modifier = Modifier.fillMaxWidth(),
        )

        Card(modifier = Modifier.fillMaxWidth()) {
            Column(
                modifier = Modifier.padding(16.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp),
            ) {
                Text(
                    tr("Weekly trend (projected)", "Недельный тренд (прогноз)"),
                    style = MaterialTheme.typography.titleMedium,
                )
                Text(
                    tr(
                        "Anti-waste score trend for next 4 weeks based on current stock and expirations.",
                        "Тренд anti-waste score на 4 недели вперед на основе текущих запасов и сроков.",
                    ),
                    style = MaterialTheme.typography.bodySmall,
                )
                Text(
                    (stats?.weeklyTrend ?: listOf(0, 0, 0, 0)).mapIndexed { index, value ->
                        tr("W${index + 1}", "Н${index + 1}") + ": $value"
                    }.joinToString("   "),
                    style = MaterialTheme.typography.bodyMedium,
                )
            }
        }

        Card(modifier = Modifier.fillMaxWidth()) {
            Column(
                modifier = Modifier.padding(16.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp),
            ) {
                Text(
                    tr("Research-driven design", "Дизайн на основе исследования"),
                    style = MaterialTheme.typography.titleMedium,
                )
                Text(
                    text = tr(
                        "Lab 3 result `r = 0.41` shows that visual inventory strongly supports users who forget what food they have.",
                        "Результат Lab 3 `r = 0.41` показывает, что визуальный список продуктов сильно помогает тем, кто забывает, что у них есть дома.",
                    ),
                    style = MaterialTheme.typography.bodyMedium,
                )
                Text(
                    text = tr(
                        "Lab 3 result `r = 0.10` shows that automation is helpful but not the main reason users believe waste will decrease.",
                        "Результат Lab 3 `r = 0.10` показывает, что автоматизация полезна, но не является главной причиной, почему пользователи верят в снижение потерь.",
                    ),
                    style = MaterialTheme.typography.bodyMedium,
                )
                Text(
                    text = tr(
                        "Lab 3 result `p = 0.33` shows forgetfulness is not limited to one role, so the product is valid for both students and employees.",
                        "Результат Lab 3 `p = 0.33` показывает, что забывчивость не ограничена одной группой пользователей, поэтому продукт подходит и студентам, и работающим людям.",
                    ),
                    style = MaterialTheme.typography.bodyMedium,
                )
            }
        }

        Card(modifier = Modifier.fillMaxWidth()) {
            Column(
                modifier = Modifier.padding(16.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp),
            ) {
                Text(
                    tr("Smart shopping suggestions", "Умные подсказки для покупок"),
                    style = MaterialTheme.typography.titleMedium,
                )
                val suggestions = stats?.shoppingSuggestions.orEmpty()
                if (suggestions.isEmpty()) {
                    Text(
                        tr(
                            "Your inventory looks balanced right now.",
                            "Сейчас твои запасы выглядят сбалансированно.",
                        ),
                    )
                } else {
                    suggestions.forEach { suggestion ->
                        Text("• ${shoppingSuggestionLabel(suggestion)}", style = MaterialTheme.typography.bodyMedium)
                    }
                }
            }
        }
    }
}
