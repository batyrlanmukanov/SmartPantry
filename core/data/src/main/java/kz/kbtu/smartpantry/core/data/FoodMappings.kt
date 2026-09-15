package kz.kbtu.smartpantry.core.data

import kz.kbtu.smartpantry.core.database.FoodEntity
import kz.kbtu.smartpantry.core.domain.AddFoodItemCommand
import kz.kbtu.smartpantry.core.model.FoodCategory
import kz.kbtu.smartpantry.core.model.FoodItem
import kz.kbtu.smartpantry.core.model.FoodStatus
import kz.kbtu.smartpantry.core.model.StorageLocation

internal fun FoodEntity.toModel(): FoodItem =
    FoodItem(
        id = id,
        ownerEmail = ownerEmail,
        name = name,
        quantity = quantity,
        unit = unit,
        category = FoodCategory.valueOf(category),
        storageLocation = StorageLocation.valueOf(storageLocation),
        purchaseEpochDay = purchaseEpochDay,
        expirationEpochDay = expirationEpochDay,
        estimatedCost = estimatedCost,
        status = FoodStatus.valueOf(status),
    )

internal fun FoodItem.toEntity(): FoodEntity =
    FoodEntity(
        id = id,
        ownerEmail = ownerEmail,
        name = name,
        quantity = quantity,
        unit = unit,
        category = category.name,
        storageLocation = storageLocation.name,
        purchaseEpochDay = purchaseEpochDay,
        expirationEpochDay = expirationEpochDay,
        estimatedCost = estimatedCost,
        status = status.name,
    )

internal fun AddFoodItemCommand.toEntity(
    id: String,
    purchaseEpochDay: Long,
    expirationEpochDay: Long,
): FoodEntity =
    FoodEntity(
        id = id,
        ownerEmail = ownerEmail,
        name = name,
        quantity = quantity,
        unit = unit,
        category = category,
        storageLocation = storageLocation,
        purchaseEpochDay = purchaseEpochDay,
        expirationEpochDay = expirationEpochDay,
        estimatedCost = estimatedCost,
        status = FoodStatus.ACTIVE.name,
    )
