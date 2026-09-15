package kz.kbtu.smartpantry.core.database

import androidx.room.Database
import androidx.room.RoomDatabase

@Database(
    entities = [FoodEntity::class],
    version = 1,
    exportSchema = false,
)
abstract class SmartPantryDatabase : RoomDatabase() {
    abstract fun foodDao(): FoodDao
}
