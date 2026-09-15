package kz.kbtu.smartpantry.feature.inventory

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import kz.kbtu.smartpantry.core.domain.AddFoodItemCommand
import kz.kbtu.smartpantry.core.domain.AddFoodItemUseCase
import kz.kbtu.smartpantry.core.domain.AddShoppingListItemUseCase
import kz.kbtu.smartpantry.core.domain.GenerateShoppingListUseCase
import kz.kbtu.smartpantry.core.domain.ObserveFoodItemUseCase
import kz.kbtu.smartpantry.core.domain.ObserveInventoryUseCase
import kz.kbtu.smartpantry.core.domain.ObserveProfileUseCase
import kz.kbtu.smartpantry.core.domain.ObserveShoppingListUseCase
import kz.kbtu.smartpantry.core.domain.RemoveShoppingListItemUseCase
import kz.kbtu.smartpantry.core.domain.UpdateFoodItemCommand
import kz.kbtu.smartpantry.core.domain.UpdateFoodItemUseCase
import kz.kbtu.smartpantry.core.domain.UpdateFoodStatusUseCase
import kz.kbtu.smartpantry.core.model.FoodItem
import kz.kbtu.smartpantry.core.model.FoodStatus
import kz.kbtu.smartpantry.core.model.ShoppingListItem
import kz.kbtu.smartpantry.core.model.StorageLocation

const val INVENTORY_ROUTE = "inventory"
const val INVENTORY_DETAIL_ROUTE = "inventory/{itemId}"

data class InventoryUiState(
    val ownerEmail: String = "",
    val items: List<FoodItem> = emptyList(),
    val shoppingList: List<ShoppingListItem> = emptyList(),
)

@HiltViewModel
class InventoryViewModel @Inject constructor(
    observeInventoryUseCase: ObserveInventoryUseCase,
    observeProfileUseCase: ObserveProfileUseCase,
    observeShoppingListUseCase: ObserveShoppingListUseCase,
    private val addFoodItemUseCase: AddFoodItemUseCase,
    private val updateFoodStatusUseCase: UpdateFoodStatusUseCase,
    private val updateFoodItemUseCase: UpdateFoodItemUseCase,
    private val addShoppingListItemUseCase: AddShoppingListItemUseCase,
    private val removeShoppingListItemUseCase: RemoveShoppingListItemUseCase,
    private val generateShoppingListUseCase: GenerateShoppingListUseCase,
) : ViewModel() {

    val state: StateFlow<InventoryUiState> = combine(
        observeInventoryUseCase(),
        observeProfileUseCase(),
        observeShoppingListUseCase(),
    ) { items, profile, shoppingList ->
        InventoryUiState(
            ownerEmail = profile.email,
            items = items,
            shoppingList = shoppingList,
        )
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5_000),
        initialValue = InventoryUiState(),
    )

    fun addItem(command: AddFoodItemCommand) {
        viewModelScope.launch {
            addFoodItemUseCase(command)
        }
    }

    fun updateStatus(itemId: String, status: FoodStatus) {
        viewModelScope.launch {
            updateFoodStatusUseCase(itemId, status)
        }
    }

    fun rotateStorage(item: FoodItem) {
        val nextStorage = when (item.storageLocation) {
            StorageLocation.FRIDGE -> StorageLocation.FREEZER
            StorageLocation.FREEZER -> StorageLocation.CUPBOARD
            StorageLocation.CUPBOARD -> StorageLocation.COUNTER
            StorageLocation.COUNTER -> StorageLocation.FRIDGE
        }
        updateItem(item, item.quantity, nextStorage, ((item.expirationEpochDay - java.time.LocalDate.now().toEpochDay()).toInt()).coerceAtLeast(0))
    }

    fun updateItem(item: FoodItem, quantity: Int, storageLocation: StorageLocation, expiresInDays: Int) {
        viewModelScope.launch {
            updateFoodItemUseCase(
                UpdateFoodItemCommand(
                    itemId = item.id,
                    quantity = quantity,
                    storageLocation = storageLocation.name,
                    expiresInDays = expiresInDays,
                ),
            )
        }
    }

    fun addToShoppingList(name: String, source: String) {
        viewModelScope.launch {
            addShoppingListItemUseCase(name, source)
        }
    }

    fun removeFromShoppingList(name: String) {
        viewModelScope.launch {
            removeShoppingListItemUseCase(name)
        }
    }

    fun generateShoppingList() {
        viewModelScope.launch {
            generateShoppingListUseCase()
        }
    }
}

@HiltViewModel
class InventoryDetailViewModel @Inject constructor(
    savedStateHandle: SavedStateHandle,
    observeFoodItemUseCase: ObserveFoodItemUseCase,
) : ViewModel() {

    private val itemId: String = savedStateHandle.get<String>("itemId").orEmpty()

    val item: StateFlow<FoodItem?> = observeFoodItemUseCase(itemId).stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5_000),
        initialValue = null,
    )
}
