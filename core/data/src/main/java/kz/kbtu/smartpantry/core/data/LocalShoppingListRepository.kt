package kz.kbtu.smartpantry.core.data

import android.content.Context
import androidx.datastore.preferences.core.edit
import dagger.hilt.android.qualifiers.ApplicationContext
import javax.inject.Inject
import javax.inject.Singleton
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import kz.kbtu.smartpantry.core.domain.ShoppingListRepository
import kz.kbtu.smartpantry.core.model.ShoppingListItem

@Singleton
class LocalShoppingListRepository @Inject constructor(
    @ApplicationContext private val context: Context,
) : ShoppingListRepository {

    override fun observeItems(): Flow<List<ShoppingListItem>> =
        context.smartPantryDataStore.data.map { preferences ->
            preferences[PreferenceKeys.shoppingList].orEmpty()
                .mapNotNull(::decodeItem)
                .sortedBy { it.name.lowercase() }
        }

    override suspend fun addItem(item: ShoppingListItem) {
        context.smartPantryDataStore.edit { preferences ->
            val set = preferences[PreferenceKeys.shoppingList].orEmpty().toMutableSet()
            set += encodeItem(item)
            preferences[PreferenceKeys.shoppingList] = set
        }
    }

    override suspend fun removeItem(name: String) {
        context.smartPantryDataStore.edit { preferences ->
            val filtered = preferences[PreferenceKeys.shoppingList].orEmpty()
                .mapNotNull(::decodeItem)
                .filterNot { it.name.equals(name, ignoreCase = true) }
                .map(::encodeItem)
                .toSet()
            preferences[PreferenceKeys.shoppingList] = filtered
        }
    }

    override suspend fun replaceItems(items: List<ShoppingListItem>) {
        context.smartPantryDataStore.edit { preferences ->
            preferences[PreferenceKeys.shoppingList] = items.map(::encodeItem).toSet()
        }
    }

    private fun encodeItem(item: ShoppingListItem): String = "${item.name}|${item.source}"

    private fun decodeItem(value: String): ShoppingListItem? {
        val parts = value.split("|", limit = 2)
        if (parts.size != 2) return null
        return ShoppingListItem(
            name = parts[0],
            source = parts[1],
        )
    }
}
