package kz.kbtu.smartpantry.core.domain

import javax.inject.Inject
import kotlinx.coroutines.flow.Flow
import kz.kbtu.smartpantry.core.model.FoodItem
import kz.kbtu.smartpantry.core.model.FoodStatus

class ObserveInventoryUseCase @Inject constructor(
    private val foodRepository: FoodRepository,
) {
    operator fun invoke(): Flow<List<FoodItem>> = foodRepository.observeInventory()
}

class ObserveFoodItemUseCase @Inject constructor(
    private val foodRepository: FoodRepository,
) {
    operator fun invoke(itemId: String): Flow<FoodItem?> = foodRepository.observeFoodItem(itemId)
}

class AddFoodItemUseCase @Inject constructor(
    private val foodRepository: FoodRepository,
) {
    suspend operator fun invoke(command: AddFoodItemCommand) = foodRepository.addFoodItem(command)
}

class UpdateFoodStatusUseCase @Inject constructor(
    private val foodRepository: FoodRepository,
) {
    suspend operator fun invoke(itemId: String, status: FoodStatus) =
        foodRepository.updateFoodStatus(itemId, status)
}

class UpdateFoodItemUseCase @Inject constructor(
    private val foodRepository: FoodRepository,
) {
    suspend operator fun invoke(command: UpdateFoodItemCommand) =
        foodRepository.updateFoodItem(command)
}

class SeedDemoInventoryUseCase @Inject constructor(
    private val foodRepository: FoodRepository,
) {
    suspend operator fun invoke(ownerEmail: String) =
        foodRepository.seedDemoDataIfEmpty(ownerEmail)
}
