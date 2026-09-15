package kz.kbtu.smartpantry.core.database

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "food_items")
data class FoodEntity(
    @PrimaryKey val id: String,
    val ownerEmail: String,
    val name: String,
    val quantity: Int,
    val unit: String,
    val category: String,
    val storageLocation: String,
    val purchaseEpochDay: Long,
    val expirationEpochDay: Long,
    val estimatedCost: Double,
    val status: String,
)
