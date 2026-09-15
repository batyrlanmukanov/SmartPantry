package kz.kbtu.smartpantry.core.domain

import java.time.LocalDate
import javax.inject.Inject
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import kz.kbtu.smartpantry.core.model.FoodCategory
import kz.kbtu.smartpantry.core.model.FoodItem
import kz.kbtu.smartpantry.core.model.FoodStatus
import kz.kbtu.smartpantry.core.model.WeeklyStats

object WeeklyStatsCalculator {
    fun calculate(items: List<FoodItem>): WeeklyStats {
        val today = LocalDate.now().toEpochDay()
        val active = items.filter { it.status == FoodStatus.ACTIVE }
        val used = items.filter { it.status == FoodStatus.USED }
        val wasted = items.filter { it.status == FoodStatus.WASTED }
        val leftovers = active.filter { it.category == FoodCategory.LEFTOVERS }
        val expiringSoon = active.count { it.expirationEpochDay - today <= 3 }
        val wastedCost = wasted.sumOf { it.estimatedCost }
        val savedAmount = used.sumOf { it.estimatedCost }
        val totalTracked = items.size.coerceAtLeast(1)
        val usedBeforeExpiry = used.count { it.expirationEpochDay >= today }
        val usedBeforeExpiryPercent = if (used.isEmpty()) 0 else ((usedBeforeExpiry * 100.0) / used.size).toInt()
        val antiWasteScore = (
            100 -
                (wasted.size * 15) -
                (expiringSoon * 6) +
                (used.size * 5)
            ).coerceIn(0, 100)
        val categoryCounts = active.groupingBy { it.category }.eachCount()
        val shoppingSuggestions = buildList {
            if ((categoryCounts[FoodCategory.PRODUCE] ?: 0) == 0) add("Fresh vegetables")
            if ((categoryCounts[FoodCategory.DAIRY] ?: 0) == 0) add("Milk or yogurt")
            if ((categoryCounts[FoodCategory.PANTRY] ?: 0) == 0) add("Rice or pasta")
            if ((categoryCounts[FoodCategory.PROTEIN] ?: 0) == 0) add("Eggs or chicken")
            if (active.size < 3) add("Basic staples")
        }.distinct().take(4)

        val weeklyTrend = (0..3).map { week ->
            val horizon = today + (week * 2)
            val weekExpiring = active.count { (it.expirationEpochDay - horizon) in 0..2 }
            (antiWasteScore - (weekExpiring * 4) + (used.size * 2) - (wasted.size * 3)).coerceIn(0, 100)
        }

        return WeeklyStats(
            activeItems = active.size,
            expiringSoonItems = expiringSoon,
            usedItems = used.size,
            wastedItems = wasted.size,
            leftoverItems = leftovers.size,
            estimatedSavedAmount = (savedAmount - wastedCost).coerceAtLeast(0.0),
            wastedAmount = wastedCost,
            usedBeforeExpiryPercent = usedBeforeExpiryPercent,
            wasteRatePercent = ((wasted.size.toDouble() / totalTracked.toDouble()) * 100).toInt(),
            antiWasteScore = antiWasteScore,
            shoppingSuggestions = shoppingSuggestions,
            weeklyTrend = weeklyTrend,
        )
    }
}

class ObserveWeeklyStatsUseCase @Inject constructor(
    private val foodRepository: FoodRepository,
) {
    operator fun invoke(): Flow<WeeklyStats> =
        foodRepository.observeInventory().map(WeeklyStatsCalculator::calculate)
}
