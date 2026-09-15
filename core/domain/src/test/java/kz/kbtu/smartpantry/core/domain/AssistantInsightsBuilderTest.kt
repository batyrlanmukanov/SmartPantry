package kz.kbtu.smartpantry.core.domain

import org.junit.Assert.assertTrue
import org.junit.Test
import kz.kbtu.smartpantry.core.model.FoodCategory
import kz.kbtu.smartpantry.core.model.FoodItem
import kz.kbtu.smartpantry.core.model.FoodStatus
import kz.kbtu.smartpantry.core.model.StorageLocation
import kz.kbtu.smartpantry.core.model.UserProfile

class AssistantInsightsBuilderTest {

    @Test
    fun `builder prioritizes expiring items`() {
        val items = listOf(
            FoodItem(
                id = "1",
                ownerEmail = "test@mail.com",
                name = "Milk",
                quantity = 1,
                unit = "pcs",
                category = FoodCategory.DAIRY,
                storageLocation = StorageLocation.FRIDGE,
                purchaseEpochDay = 100L,
                expirationEpochDay = 101L,
                estimatedCost = 1000.0,
                status = FoodStatus.ACTIVE,
            ),
        )

        val result = AssistantInsightsBuilder.build(items, UserProfile())

        assertTrue(result.first().title.contains("Use these items first"))
    }
}
