package kz.kbtu.smartpantry.core.domain

import org.junit.Assert.assertEquals
import org.junit.Test
import kz.kbtu.smartpantry.core.model.FoodCategory
import kz.kbtu.smartpantry.core.model.FoodItem
import kz.kbtu.smartpantry.core.model.FoodStatus
import kz.kbtu.smartpantry.core.model.StorageLocation

class WeeklyStatsCalculatorTest {

    @Test
    fun `calculator counts wasted and used items`() {
        val items = listOf(
            sampleItem("1", FoodStatus.ACTIVE, 600.0),
            sampleItem("2", FoodStatus.USED, 400.0),
            sampleItem("3", FoodStatus.WASTED, 200.0),
        )

        val stats = WeeklyStatsCalculator.calculate(items)

        assertEquals(1, stats.activeItems)
        assertEquals(1, stats.usedItems)
        assertEquals(1, stats.wastedItems)
        assertEquals(200.0, stats.estimatedSavedAmount, 0.0)
    }

    private fun sampleItem(
        id: String,
        status: FoodStatus,
        cost: Double,
    ) = FoodItem(
        id = id,
        ownerEmail = "test@mail.com",
        name = "Item$id",
        quantity = 1,
        unit = "pcs",
        category = FoodCategory.PANTRY,
        storageLocation = StorageLocation.CUPBOARD,
        purchaseEpochDay = 100L,
        expirationEpochDay = 105L,
        estimatedCost = cost,
        status = status,
    )
}
