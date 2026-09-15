package kz.kbtu.smartpantry.feature.inventory

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.ui.Alignment
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.GridItemSpan
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import java.time.LocalDate
import kz.kbtu.smartpantry.core.domain.AddFoodItemCommand
import kz.kbtu.smartpantry.core.model.FoodCategory
import kz.kbtu.smartpantry.core.model.FoodItem
import kz.kbtu.smartpantry.core.model.FoodStatus
import kz.kbtu.smartpantry.core.model.StorageLocation
import kz.kbtu.smartpantry.core.ui.LabelValueRow
import kz.kbtu.smartpantry.core.ui.MetricCard
import kz.kbtu.smartpantry.core.ui.SectionTitle
import kz.kbtu.smartpantry.core.ui.categoryLabel
import kz.kbtu.smartpantry.core.ui.foodNameLabel
import kz.kbtu.smartpantry.core.ui.statusLabel
import kz.kbtu.smartpantry.core.ui.storageLabel
import kz.kbtu.smartpantry.core.ui.tr
import kz.kbtu.smartpantry.core.ui.unitLabel

@Composable
fun InventoryScreen(
    onItemOpen: (String) -> Unit,
    viewModel: InventoryViewModel = hiltViewModel(),
) {
    val state by viewModel.state.collectAsStateWithLifecycle()

    var name by remember { mutableStateOf("") }
    var quantity by remember { mutableStateOf("1") }
    var expiresInDays by remember { mutableStateOf("3") }
    var cost by remember { mutableStateOf("500") }
    var category by remember { mutableStateOf(FoodCategory.PRODUCE) }
    var location by remember { mutableStateOf(StorageLocation.FRIDGE) }
    var categoryExpanded by remember { mutableStateOf(false) }
    var locationExpanded by remember { mutableStateOf(false) }

    val activeItems = remember(state.items) {
        state.items.filter { it.status == FoodStatus.ACTIVE }
    }
    val usedItems = remember(state.items) {
        state.items.filter { it.status == FoodStatus.USED }
    }
    val wastedItems = remember(state.items) {
        state.items.filter { it.status == FoodStatus.WASTED }
    }
    val expiringSoon = remember(activeItems) {
        val today = LocalDate.now().toEpochDay()
        activeItems.count { it.expirationEpochDay - today <= 3 }
    }
    val shoppingSourceLabel = tr("Manual from inventory", "Вручную из запасов")

    LazyVerticalGrid(
        columns = GridCells.Adaptive(minSize = 170.dp),
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp),
        horizontalArrangement = Arrangement.spacedBy(12.dp),
    ) {
        item(span = { GridItemSpan(maxLineSpan) }) {
            Card(modifier = Modifier.fillMaxWidth()) {
                Column(
                    modifier = Modifier.padding(16.dp),
                    verticalArrangement = Arrangement.spacedBy(12.dp),
                ) {
                    SectionTitle(tr("Add food item", "Добавить продукт"))
                    OutlinedTextField(
                        value = name,
                        onValueChange = { name = it },
                        label = { Text(tr("Product name", "Название продукта")) },
                        modifier = Modifier.fillMaxWidth(),
                    )
                    Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                        OutlinedTextField(
                            value = quantity,
                            onValueChange = { quantity = it },
                            label = { Text(tr("Qty", "Кол-во")) },
                            modifier = Modifier.weight(1f),
                        )
                        OutlinedTextField(
                            value = expiresInDays,
                            onValueChange = { expiresInDays = it },
                            label = { Text(tr("Expires in days", "Дней до срока")) },
                            modifier = Modifier.weight(1f),
                        )
                    }
                    OutlinedTextField(
                        value = cost,
                        onValueChange = { cost = it },
                        label = { Text(tr("Estimated cost (KZT)", "Примерная цена (KZT)")) },
                        modifier = Modifier.fillMaxWidth(),
                    )
                    Box {
                        OutlinedButton(
                            onClick = { categoryExpanded = true },
                            modifier = Modifier.fillMaxWidth(),
                        ) {
                            Text("${tr("Category", "Категория")}: ${categoryLabel(category)}")
                        }
                        DropdownMenu(
                            expanded = categoryExpanded,
                            onDismissRequest = { categoryExpanded = false },
                        ) {
                            FoodCategory.entries.forEach { entry ->
                                DropdownMenuItem(
                                    text = { Text(categoryLabel(entry)) },
                                    onClick = {
                                        category = entry
                                        categoryExpanded = false
                                    },
                                )
                            }
                        }
                    }
                    Box {
                        OutlinedButton(
                            onClick = { locationExpanded = true },
                            modifier = Modifier.fillMaxWidth(),
                        ) {
                            Text("${tr("Storage", "Хранение")}: ${storageLabel(location)}")
                        }
                        DropdownMenu(
                            expanded = locationExpanded,
                            onDismissRequest = { locationExpanded = false },
                        ) {
                            StorageLocation.entries.forEach { entry ->
                                DropdownMenuItem(
                                    text = { Text(storageLabel(entry)) },
                                    onClick = {
                                        location = entry
                                        locationExpanded = false
                                    },
                                )
                            }
                        }
                    }
                    Button(
                        onClick = {
                            if (state.ownerEmail.isNotBlank() && name.isNotBlank()) {
                                viewModel.addItem(
                                    AddFoodItemCommand(
                                        ownerEmail = state.ownerEmail,
                                        name = name,
                                        quantity = quantity.toIntOrNull() ?: 1,
                                        unit = "pcs",
                                        category = category.name,
                                        storageLocation = location.name,
                                        expiresInDays = expiresInDays.toIntOrNull() ?: 3,
                                        estimatedCost = cost.toDoubleOrNull() ?: 0.0,
                                    ),
                                )
                                name = ""
                                quantity = "1"
                                expiresInDays = "3"
                                cost = "500"
                            }
                        },
                        modifier = Modifier.fillMaxWidth(),
                    ) {
                        Text(tr("Save item", "Сохранить продукт"))
                    }
                }
            }
        }

        item {
            MetricCard(
                title = tr("Active items", "Активные продукты"),
                value = activeItems.size.toString(),
                subtitle = tr("Tracked right now", "Отслеживаются сейчас"),
            )
        }
        item {
            MetricCard(
                title = tr("Expiring soon", "Скоро истекают"),
                value = expiringSoon.toString(),
                subtitle = tr("Need attention", "Требуют внимания"),
            )
        }
        item {
            MetricCard(
                title = tr("Used", "Использовано"),
                value = usedItems.size.toString(),
                subtitle = tr("Consumed already", "Уже использовано"),
            )
        }
        item {
            MetricCard(
                title = tr("Wasted", "Списано"),
                value = wastedItems.size.toString(),
                subtitle = tr("Lost products", "Потери продуктов"),
            )
        }

        item(span = { GridItemSpan(maxLineSpan) }) {
            SectionTitle(tr("Visual Inventory", "Визуальные запасы"))
        }

        item(span = { GridItemSpan(maxLineSpan) }) {
            Card(modifier = Modifier.fillMaxWidth()) {
                Column(
                    modifier = Modifier.padding(16.dp),
                    verticalArrangement = Arrangement.spacedBy(8.dp),
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                    ) {
                        Text(
                            text = tr("Shopping list", "Список покупок"),
                            style = MaterialTheme.typography.titleMedium,
                        )
                        TextButton(onClick = viewModel::generateShoppingList) {
                            Text(tr("Auto-generate", "Автогенерация"))
                        }
                    }
                    if (state.shoppingList.isEmpty()) {
                        Text(
                            tr(
                                "No shopping items yet. Use quick actions or auto-generate.",
                                "Пока пусто. Используй быстрые действия или автогенерацию.",
                            ),
                        )
                    } else {
                        state.shoppingList.forEach { item ->
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                            ) {
                                Column(modifier = Modifier.weight(1f)) {
                                    Text("• ${item.name}")
                                    Text(item.source, style = MaterialTheme.typography.bodySmall)
                                }
                                TextButton(onClick = { viewModel.removeFromShoppingList(item.name) }) {
                                    Text(tr("Done", "Готово"))
                                }
                            }
                        }
                    }
                }
            }
        }

        items(activeItems, key = { it.id }) { item ->
            InventoryItemCard(
                item = item,
                showActions = true,
                onOpen = { onItemOpen(item.id) },
                onMarkUsed = { viewModel.updateStatus(item.id, FoodStatus.USED) },
                onMarkWaste = { viewModel.updateStatus(item.id, FoodStatus.WASTED) },
                onAddToShopping = {
                    viewModel.addToShoppingList(
                        name = item.name,
                        source = shoppingSourceLabel,
                    )
                },
                onRotateStorage = { viewModel.rotateStorage(item) },
                onQuickEdit = { quantity, expiresInDays ->
                    viewModel.updateItem(
                        item = item,
                        quantity = quantity,
                        storageLocation = item.storageLocation,
                        expiresInDays = expiresInDays,
                    )
                },
            )
        }

        if (usedItems.isNotEmpty()) {
            item(span = { GridItemSpan(maxLineSpan) }) {
                SectionTitle(tr("Used items", "Использованные продукты"))
            }

            items(usedItems, key = { it.id }) { item ->
                InventoryItemCard(
                    item = item,
                    showActions = false,
                    onOpen = { onItemOpen(item.id) },
                    onMarkUsed = {},
                    onMarkWaste = {},
                    onAddToShopping = {},
                    onRotateStorage = {},
                    onQuickEdit = { _, _ -> },
                )
            }
        }

        if (wastedItems.isNotEmpty()) {
            item(span = { GridItemSpan(maxLineSpan) }) {
                SectionTitle(tr("Wasted items", "Списанные продукты"))
            }

            items(wastedItems, key = { it.id }) { item ->
                InventoryItemCard(
                    item = item,
                    showActions = false,
                    onOpen = { onItemOpen(item.id) },
                    onMarkUsed = {},
                    onMarkWaste = {},
                    onAddToShopping = {},
                    onRotateStorage = {},
                    onQuickEdit = { _, _ -> },
                )
            }
        }
    }
}

@Composable
fun InventoryDetailScreen(
    viewModel: InventoryDetailViewModel = hiltViewModel(),
) {
    val item by viewModel.item.collectAsStateWithLifecycle()

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp),
    ) {
        Text(
            text = item?.let { foodNameLabel(it.name) } ?: tr("Item not found", "Продукт не найден"),
            style = MaterialTheme.typography.headlineSmall,
        )
        item?.let {
            val expirationDate = LocalDate.ofEpochDay(it.expirationEpochDay)
            val daysLeft = (it.expirationEpochDay - LocalDate.now().toEpochDay()).toInt()
            LabelValueRow(tr("Quantity", "Количество"), "${it.quantity} ${unitLabel(it.unit)}")
            LabelValueRow(tr("Category", "Категория"), categoryLabel(it.category))
            LabelValueRow(tr("Storage", "Хранение"), storageLabel(it.storageLocation))
            LabelValueRow(tr("Status", "Статус"), statusLabel(it.status))
            LabelValueRow(tr("Estimated cost", "Цена"), "%.0f KZT".format(it.estimatedCost))
            LabelValueRow(
                tr("Expiration date", "Дата срока"),
                expirationDate.toString(),
            )
            ExpiryDaysRow(daysLeft = daysLeft)
        }
    }
}

@Composable
private fun InventoryItemCard(
    item: FoodItem,
    showActions: Boolean,
    onOpen: () -> Unit,
    onMarkUsed: () -> Unit,
    onMarkWaste: () -> Unit,
    onAddToShopping: () -> Unit,
    onRotateStorage: () -> Unit,
    onQuickEdit: (quantity: Int, expiresInDays: Int) -> Unit,
) {
    val remainingDays = (item.expirationEpochDay - LocalDate.now().toEpochDay()).toInt().coerceAtLeast(0)
    var quickQty by remember(item.id) { mutableStateOf(item.quantity.toString()) }
    var quickExpires by remember(item.id) { mutableStateOf(remainingDays.toString()) }

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onOpen),
    ) {
        Column(
            modifier = Modifier.padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp),
        ) {
            Text(text = foodNameLabel(item.name), style = MaterialTheme.typography.titleMedium)
            LabelValueRow(tr("Quantity", "Количество"), "${item.quantity} ${unitLabel(item.unit)}")
            LabelValueRow(tr("Category", "Категория"), categoryLabel(item.category))
            LabelValueRow(tr("Storage", "Хранение"), storageLabel(item.storageLocation))
            LabelValueRow(tr("Status", "Статус"), statusLabel(item.status))
            ExpiryDaysRow(daysLeft = remainingDays)
            if (showActions) {
                Spacer(modifier = Modifier.height(4.dp))
                Column(
                    modifier = Modifier.fillMaxWidth(),
                    verticalArrangement = Arrangement.spacedBy(8.dp),
                ) {
                    OutlinedButton(
                        onClick = onMarkUsed,
                        modifier = Modifier.fillMaxWidth(),
                    ) {
                        Text(
                            text = tr("Mark used", "Отметить как использованное"),
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis,
                        )
                    }
                    Button(
                        onClick = onMarkWaste,
                        modifier = Modifier.fillMaxWidth(),
                    ) {
                        Text(
                            text = tr("Mark wasted", "Отметить как списанное"),
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis,
                        )
                    }
                    OutlinedButton(
                        onClick = onRotateStorage,
                        modifier = Modifier.fillMaxWidth(),
                    ) {
                        Text(tr("Move storage", "Сменить хранение"))
                    }
                    OutlinedButton(
                        onClick = onAddToShopping,
                        modifier = Modifier.fillMaxWidth(),
                    ) {
                        Text(tr("Add to shopping list", "Добавить в список покупок"))
                    }
                    Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                        OutlinedTextField(
                            value = quickQty,
                            onValueChange = { quickQty = it },
                            label = { Text(tr("Qty", "Кол-во")) },
                            modifier = Modifier.weight(1f),
                        )
                        OutlinedTextField(
                            value = quickExpires,
                            onValueChange = { quickExpires = it },
                            label = { Text(tr("Days left", "Дней")) },
                            modifier = Modifier.weight(1f),
                        )
                    }
                    Button(
                        onClick = {
                            onQuickEdit(
                                quickQty.toIntOrNull() ?: item.quantity,
                                quickExpires.toIntOrNull() ?: remainingDays,
                            )
                        },
                        modifier = Modifier.fillMaxWidth(),
                    ) {
                        Text(tr("Save quick edit", "Сохранить быстрое редактирование"))
                    }
                }
            }
        }
    }
}

@Composable
private fun ExpiryDaysRow(daysLeft: Int) {
    val safeDays = daysLeft.coerceAtLeast(0)
    val color = when {
        safeDays <= 1 -> MaterialTheme.colorScheme.error
        safeDays <= 3 -> MaterialTheme.colorScheme.tertiary
        else -> MaterialTheme.colorScheme.primary
    }
    val level = when {
        safeDays <= 1 -> tr("Critical", "Критично")
        safeDays <= 3 -> tr("Warning", "Внимание")
        else -> tr("Safe", "Норма")
    }

    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Text(
            text = tr("Days left", "Осталось дней"),
            style = MaterialTheme.typography.bodyMedium,
        )
        Text(
            text = "$safeDays ($level)",
            color = color,
            style = MaterialTheme.typography.bodyMedium,
        )
    }
}
