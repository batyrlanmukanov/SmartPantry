package kz.kbtu.smartpantry.core.model

enum class FoodCategory {
    PRODUCE,
    DAIRY,
    PROTEIN,
    MEAT,
    PANTRY,
    FROZEN,
    BEVERAGE,
    LEFTOVERS,
}

enum class StorageLocation {
    FRIDGE,
    FREEZER,
    CUPBOARD,
    COUNTER,
}

enum class FoodStatus {
    ACTIVE,
    USED,
    WASTED,
}

data class FoodItem(
    val id: String,
    val ownerEmail: String,
    val name: String,
    val quantity: Int,
    val unit: String,
    val category: FoodCategory,
    val storageLocation: StorageLocation,
    val purchaseEpochDay: Long,
    val expirationEpochDay: Long,
    val estimatedCost: Double,
    val status: FoodStatus,
)
