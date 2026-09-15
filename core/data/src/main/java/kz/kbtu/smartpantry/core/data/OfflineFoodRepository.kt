package kz.kbtu.smartpantry.core.data

import java.time.LocalDate
import java.util.UUID
import javax.inject.Inject
import javax.inject.Singleton
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import kz.kbtu.smartpantry.core.database.FoodDao
import kz.kbtu.smartpantry.core.domain.AddFoodItemCommand
import kz.kbtu.smartpantry.core.domain.FoodRepository
import kz.kbtu.smartpantry.core.domain.UpdateFoodItemCommand
import kz.kbtu.smartpantry.core.model.FoodCategory
import kz.kbtu.smartpantry.core.model.FoodItem
import kz.kbtu.smartpantry.core.model.FoodStatus
import kz.kbtu.smartpantry.core.model.StorageLocation

@Singleton
class OfflineFoodRepository @Inject constructor(
    private val foodDao: FoodDao,
) : FoodRepository {

    override fun observeInventory(): Flow<List<FoodItem>> =
        foodDao.observeAll().map { items -> items.map { it.toModel() } }

    override fun observeFoodItem(itemId: String): Flow<FoodItem?> =
        foodDao.observeById(itemId).map { entity -> entity?.toModel() }

    override suspend fun addFoodItem(command: AddFoodItemCommand) {
        val today = LocalDate.now().toEpochDay()
        foodDao.insert(
            command.toEntity(
                id = UUID.randomUUID().toString(),
                purchaseEpochDay = today,
                expirationEpochDay = today + command.expiresInDays,
            ),
        )
    }

    override suspend fun updateFoodStatus(itemId: String, status: FoodStatus) {
        val item = foodDao.getById(itemId) ?: return
        foodDao.update(item.copy(status = status.name))
    }

    override suspend fun updateFoodItem(command: UpdateFoodItemCommand) {
        val item = foodDao.getById(command.itemId) ?: return
        val updatedExpiration = LocalDate.now().toEpochDay() + command.expiresInDays.coerceAtLeast(0)
        foodDao.update(
            item.copy(
                quantity = command.quantity.coerceAtLeast(1),
                storageLocation = command.storageLocation,
                expirationEpochDay = updatedExpiration,
            ),
        )
    }

    override suspend fun seedDemoDataIfEmpty(ownerEmail: String) {
        if (foodDao.countAll() > 0) return

        val today = LocalDate.now().toEpochDay()
        val demoItems = listOf(
            FoodItem(
                id = UUID.randomUUID().toString(),
                ownerEmail = ownerEmail,
                name = "Milk",
                quantity = 1,
                unit = "pack",
                category = FoodCategory.DAIRY,
                storageLocation = StorageLocation.FRIDGE,
                purchaseEpochDay = today - 2,
                expirationEpochDay = today + 1,
                estimatedCost = 850.0,
                status = FoodStatus.ACTIVE,
            ),
            FoodItem(
                id = UUID.randomUUID().toString(),
                ownerEmail = ownerEmail,
                name = "Tomatoes",
                quantity = 4,
                unit = "pcs",
                category = FoodCategory.PRODUCE,
                storageLocation = StorageLocation.COUNTER,
                purchaseEpochDay = today - 1,
                expirationEpochDay = today + 2,
                estimatedCost = 900.0,
                status = FoodStatus.ACTIVE,
            ),
            FoodItem(
                id = UUID.randomUUID().toString(),
                ownerEmail = ownerEmail,
                name = "Cooked rice",
                quantity = 1,
                unit = "box",
                category = FoodCategory.LEFTOVERS,
                storageLocation = StorageLocation.FRIDGE,
                purchaseEpochDay = today,
                expirationEpochDay = today + 1,
                estimatedCost = 500.0,
                status = FoodStatus.ACTIVE,
            ),
        )

        foodDao.insertAll(demoItems.map(FoodItem::toEntity))
    }
}
