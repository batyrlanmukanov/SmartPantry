package kz.kbtu.smartpantry.core.domain

import java.time.LocalDate
import javax.inject.Inject
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first
import kz.kbtu.smartpantry.core.model.FoodStatus
import kz.kbtu.smartpantry.core.model.ShoppingListItem

class ObserveShoppingListUseCase @Inject constructor(
    private val shoppingListRepository: ShoppingListRepository,
) {
    operator fun invoke(): Flow<List<ShoppingListItem>> = shoppingListRepository.observeItems()
}

class AddShoppingListItemUseCase @Inject constructor(
    private val shoppingListRepository: ShoppingListRepository,
) {
    suspend operator fun invoke(name: String, source: String) =
        shoppingListRepository.addItem(ShoppingListItem(name = name, source = source))
}

class RemoveShoppingListItemUseCase @Inject constructor(
    private val shoppingListRepository: ShoppingListRepository,
) {
    suspend operator fun invoke(name: String) = shoppingListRepository.removeItem(name)
}

class GenerateShoppingListUseCase @Inject constructor(
    private val foodRepository: FoodRepository,
    private val shoppingListRepository: ShoppingListRepository,
) {
    suspend operator fun invoke() {
        val items = foodRepository.observeInventory().first()
        val today = LocalDate.now().toEpochDay()

        val generated = buildList {
            items.filter { it.status == FoodStatus.ACTIVE && (it.expirationEpochDay - today) in 0..2 }
                .forEach { add(ShoppingListItem(name = "${it.name} (use now)", source = "Expiring soon")) }
            if (items.none { it.status == FoodStatus.ACTIVE && it.category.name == "PRODUCE" }) {
                add(ShoppingListItem(name = "Fresh vegetables", source = "Category gap"))
            }
            if (items.none { it.status == FoodStatus.ACTIVE && it.category.name == "PROTEIN" }) {
                add(ShoppingListItem(name = "Protein source", source = "Category gap"))
            }
            if (items.count { it.status == FoodStatus.ACTIVE } < 4) {
                add(ShoppingListItem(name = "Basic staples", source = "Low stock"))
            }
        }

        shoppingListRepository.replaceItems(generated.distinctBy { it.name.lowercase() })
    }
}
