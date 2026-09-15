package kz.kbtu.smartpantry.core.model

data class AssistantInsight(
    val title: String,
    val message: String,
    val priority: Int,
)

enum class RecipeDuration {
    FAST,
    MEDIUM,
    LONG,
}

data class RecipeSuggestion(
    val title: String,
    val duration: RecipeDuration,
    val calories: Int,
    val reason: String,
)

data class AssistantReply(
    val text: String,
    val recipes: List<RecipeSuggestion> = emptyList(),
)

data class WeeklyStats(
    val activeItems: Int,
    val expiringSoonItems: Int,
    val usedItems: Int,
    val wastedItems: Int,
    val leftoverItems: Int,
    val estimatedSavedAmount: Double,
    val wastedAmount: Double,
    val usedBeforeExpiryPercent: Int,
    val wasteRatePercent: Int,
    val antiWasteScore: Int,
    val shoppingSuggestions: List<String>,
    val weeklyTrend: List<Int>,
)

data class ShoppingListItem(
    val name: String,
    val source: String,
)
