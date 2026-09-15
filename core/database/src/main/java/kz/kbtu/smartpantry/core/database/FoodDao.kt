package kz.kbtu.smartpantry.core.database

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import kotlinx.coroutines.flow.Flow

@Dao
interface FoodDao {
    @Query("SELECT * FROM food_items ORDER BY expirationEpochDay ASC")
    fun observeAll(): Flow<List<FoodEntity>>

    @Query("SELECT * FROM food_items WHERE id = :itemId LIMIT 1")
    fun observeById(itemId: String): Flow<FoodEntity?>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(item: FoodEntity)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAll(items: List<FoodEntity>)

    @Update
    suspend fun update(item: FoodEntity)

    @Query("SELECT * FROM food_items WHERE id = :itemId LIMIT 1")
    suspend fun getById(itemId: String): FoodEntity?

    @Query("SELECT COUNT(*) FROM food_items")
    suspend fun countAll(): Int
}
