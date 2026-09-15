package kz.kbtu.smartpantry.core.domain

import kotlinx.coroutines.flow.Flow
import kz.kbtu.smartpantry.core.model.FoodItem
import kz.kbtu.smartpantry.core.model.FoodStatus
import kz.kbtu.smartpantry.core.model.ShoppingListItem
import kz.kbtu.smartpantry.core.model.UserProfile
import kz.kbtu.smartpantry.core.model.UserSession

interface AuthRepository {
    fun observeSession(): Flow<UserSession?>
    suspend fun signIn(email: String, password: String): UserSession
    suspend fun signUp(displayName: String, email: String, password: String): UserSession
    suspend fun signOut()
}

interface ProfileRepository {
    fun observeProfile(): Flow<UserProfile>
    suspend fun saveProfile(profile: UserProfile)
}

data class AddFoodItemCommand(
    val ownerEmail: String,
    val name: String,
    val quantity: Int,
    val unit: String,
    val category: String,
    val storageLocation: String,
    val expiresInDays: Int,
    val estimatedCost: Double,
)

data class UpdateFoodItemCommand(
    val itemId: String,
    val quantity: Int,
    val storageLocation: String,
    val expiresInDays: Int,
)

interface FoodRepository {
    fun observeInventory(): Flow<List<FoodItem>>
    fun observeFoodItem(itemId: String): Flow<FoodItem?>
    suspend fun addFoodItem(command: AddFoodItemCommand)
    suspend fun updateFoodStatus(itemId: String, status: FoodStatus)
    suspend fun updateFoodItem(command: UpdateFoodItemCommand)
    suspend fun seedDemoDataIfEmpty(ownerEmail: String)
}

interface ShoppingListRepository {
    fun observeItems(): Flow<List<ShoppingListItem>>
    suspend fun addItem(item: ShoppingListItem)
    suspend fun removeItem(name: String)
    suspend fun replaceItems(items: List<ShoppingListItem>)
}
