package kz.kbtu.smartpantry.core.domain

import java.time.LocalDate
import javax.inject.Inject
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map
import kz.kbtu.smartpantry.core.model.AppLanguage
import kz.kbtu.smartpantry.core.model.AssistantInsight
import kz.kbtu.smartpantry.core.model.AssistantReply
import kz.kbtu.smartpantry.core.model.FoodCategory
import kz.kbtu.smartpantry.core.model.FoodItem
import kz.kbtu.smartpantry.core.model.FoodStatus
import kz.kbtu.smartpantry.core.model.RecipeDuration
import kz.kbtu.smartpantry.core.model.RecipeSuggestion
import kz.kbtu.smartpantry.core.model.UserProfile

private fun customFoodPreference(profile: UserProfile): String? {
    val preference = profile.foodPreference.trim()
    return preference.takeIf {
        it.isNotBlank() && !it.equals("Balanced", ignoreCase = true)
    }
}

object AssistantInsightsBuilder {
    fun build(items: List<FoodItem>, profile: UserProfile): List<AssistantInsight> {
        val language = profile.appLanguage
        val activeItems = items.filter { it.status == FoodStatus.ACTIVE }
        val today = LocalDate.now().toEpochDay()
        val expiringSoon = activeItems.filter { it.expirationEpochDay - today <= 3 }
        val leftovers = activeItems.filter { it.category == FoodCategory.LEFTOVERS }
        val pantryItems = activeItems.filter { it.category == FoodCategory.PANTRY }
        val preference = customFoodPreference(profile)

        val insights = mutableListOf<AssistantInsight>()

        if (expiringSoon.isNotEmpty()) {
            insights += AssistantInsight(
                title = if (language == AppLanguage.RUSSIAN) {
                    "Используй это первым"
                } else {
                    "Use these items first"
                },
                message = if (language == AppLanguage.RUSSIAN) {
                    "Скоро истекает срок у: ${expiringSoon.joinToString { it.name }}."
                } else {
                    expiringSoon.joinToString { it.name } + " will expire soon."
                },
                priority = 3,
            )
        }

        if (leftovers.isNotEmpty()) {
            insights += AssistantInsight(
                title = if (language == AppLanguage.RUSSIAN) {
                    "Спаси остатки"
                } else {
                    "Leftovers rescue"
                },
                message = if (language == AppLanguage.RUSSIAN) {
                    "У тебя уже есть готовая еда, которую лучше использовать до новых покупок."
                } else {
                    "You already have cooked food that should be used before new shopping."
                },
                priority = 2,
            )
        }

        if (pantryItems.size >= 2) {
            insights += AssistantInsight(
                title = if (language == AppLanguage.RUSSIAN) {
                    "Основа для быстрого ужина"
                } else {
                    "Quick dinner base"
                },
                message = if (language == AppLanguage.RUSSIAN) {
                    "Бакалею можно быстро сочетать с овощами и собрать ужин без лишних решений."
                } else {
                    "Pantry staples can be paired with vegetables for a fast dinner tonight."
                },
                priority = 2,
            )
        }

        if (preference != null) {
            insights += AssistantInsight(
                title = "Preference saved",
                message = if (language == AppLanguage.RUSSIAN) {
                    "Сохранено пищевое предпочтение: $preference. Ассистент будет учитывать его в советах."
                } else {
                    "Saved food preference: $preference. The assistant will keep it in mind in suggestions."
                },
                priority = 1,
            )
        }

        insights += AssistantInsight(
            title = if (language == AppLanguage.RUSSIAN) {
                "Напоминание о цели"
            } else {
                "Savings goal reminder"
            },
            message = if (language == AppLanguage.RUSSIAN) {
                "Твоя месячная цель по экономии: %.0f KZT. Сначала используй более старые продукты."
                    .format(profile.monthlySavingsGoal)
            } else {
                "Your monthly savings goal is %.0f KZT. Reduce waste by finishing older products first."
                    .format(profile.monthlySavingsGoal)
            },
            priority = 1,
        )

        if (insights.isEmpty()) {
            insights += AssistantInsight(
                title = if (language == AppLanguage.RUSSIAN) {
                    "Запасы в хорошем состоянии"
                } else {
                    "Inventory looks healthy"
                },
                message = if (language == AppLanguage.RUSSIAN) {
                    "Продолжай отслеживать продукты и обновляй их статус после еды."
                } else {
                    "Keep tracking your stock and update item status after meals."
                },
                priority = 1,
            )
        }

        return insights.sortedByDescending { it.priority }
    }
}

object RecipeSuggestionEngine {
    fun suggest(items: List<FoodItem>, language: AppLanguage): List<RecipeSuggestion> {
        val activeItems = items.filter { it.status == FoodStatus.ACTIVE }
        val activeNames = activeItems.map { it.name.lowercase() }.toSet()
        val activeCategories = activeItems.map { it.category }.toSet()
        val recipes = mutableListOf<RecipeSuggestion>()

        fun hasAny(vararg names: String): Boolean = names.any { expected ->
            activeNames.any { name -> name.contains(expected) }
        }

        if (hasAny("egg", "eggs", "яйцо", "яйца") && hasAny("milk", "молоко")) {
            recipes += RecipeSuggestion(
                title = if (language == AppLanguage.RUSSIAN) "Омлет" else "Omelette",
                duration = RecipeDuration.FAST,
                calories = 220,
                reason = if (language == AppLanguage.RUSSIAN) {
                    "У тебя уже есть яйца и молоко для быстрого горячего завтрака."
                } else {
                    "You already have eggs and milk for a fast hot breakfast."
                },
            )
        }

        if (hasAny("rice", "рис") && hasAny("egg", "eggs", "яйцо", "яйца")) {
            recipes += RecipeSuggestion(
                title = if (language == AppLanguage.RUSSIAN) "Жареный рис с яйцом" else "Egg Fried Rice",
                duration = RecipeDuration.MEDIUM,
                calories = 420,
                reason = if (language == AppLanguage.RUSSIAN) {
                    "Рис и яйца помогут быстро сделать сытный обед."
                } else {
                    "Rice and eggs make a quick and filling meal."
                },
            )
        }

        if (hasAny("tomato", "tomatoes", "помидор", "помидоры") &&
            hasAny("pasta", "макарон", "спагетти")
        ) {
            recipes += RecipeSuggestion(
                title = if (language == AppLanguage.RUSSIAN) "Томатная паста" else "Tomato Pasta",
                duration = RecipeDuration.MEDIUM,
                calories = 510,
                reason = if (language == AppLanguage.RUSSIAN) {
                    "Помидоры и паста хорошо сочетаются для основного блюда."
                } else {
                    "Tomatoes and pasta match well for a main dish."
                },
            )
        }

        if (hasAny("banana", "банан") && hasAny("milk", "молоко", "yogurt", "йогурт")) {
            recipes += RecipeSuggestion(
                title = if (language == AppLanguage.RUSSIAN) "Банановый смузи" else "Banana Smoothie",
                duration = RecipeDuration.FAST,
                calories = 180,
                reason = if (language == AppLanguage.RUSSIAN) {
                    "Банан и молочные продукты уже есть в запасе."
                } else {
                    "You already have banana and dairy ingredients."
                },
            )
        }

        if (hasAny("cucumber", "огурец", "tomato", "помидор")) {
            recipes += RecipeSuggestion(
                title = if (language == AppLanguage.RUSSIAN) "Свежий салат" else "Fresh Salad",
                duration = RecipeDuration.FAST,
                calories = 120,
                reason = if (language == AppLanguage.RUSSIAN) {
                    "Овощи лучше использовать до потери свежести."
                } else {
                    "Fresh produce is better used before it loses freshness."
                },
            )
        }

        if (hasAny("bread", "toast", "хлеб") && hasAny("cheese", "сыр", "milk", "молоко")) {
            recipes += RecipeSuggestion(
                title = if (language == AppLanguage.RUSSIAN) "Горячие сырные тосты" else "Cheesy Toasts",
                duration = RecipeDuration.FAST,
                calories = 260,
                reason = if (language == AppLanguage.RUSSIAN) {
                    "Хлеб и молочные продукты уже есть, поэтому можно быстро приготовить перекус."
                } else {
                    "Bread and dairy are already available for a quick snack."
                },
            )
        }

        if (recipes.isEmpty()) {
            val fallbackRecipe = when {
                FoodCategory.LEFTOVERS in activeCategories -> RecipeSuggestion(
                    title = if (language == AppLanguage.RUSSIAN) "Теплая тарелка из остатков" else "Leftover Rice Bowl",
                    duration = RecipeDuration.FAST,
                    calories = 320,
                    reason = if (language == AppLanguage.RUSSIAN) {
                        "Готовую еду лучше использовать в первую очередь, пока она свежая."
                    } else {
                        "Leftovers are best used first while they are still fresh."
                    },
                )
                FoodCategory.PRODUCE in activeCategories && FoodCategory.PANTRY in activeCategories -> RecipeSuggestion(
                    title = if (language == AppLanguage.RUSSIAN) "Овощная паста" else "Vegetable Pasta",
                    duration = RecipeDuration.MEDIUM,
                    calories = 390,
                    reason = if (language == AppLanguage.RUSSIAN) {
                        "Овощи и базовые продукты можно быстро превратить в полноценный ужин."
                    } else {
                        "Produce and pantry staples can become a simple full dinner."
                    },
                )
                FoodCategory.PRODUCE in activeCategories -> RecipeSuggestion(
                    title = if (language == AppLanguage.RUSSIAN) "Овощное рагу" else "Vegetable Stir-Fry",
                    duration = RecipeDuration.MEDIUM,
                    calories = 280,
                    reason = if (language == AppLanguage.RUSSIAN) {
                        "Так ты используешь свежие продукты до истечения срока."
                    } else {
                        "This helps you use fresh products before they expire."
                    },
                )
                else -> RecipeSuggestion(
                    title = if (language == AppLanguage.RUSSIAN) "Домашний быстрый ужин" else "Quick Home Dinner",
                    duration = RecipeDuration.FAST,
                    calories = 300,
                    reason = if (language == AppLanguage.RUSSIAN) {
                        "Начни с продуктов, у которых срок годности заканчивается раньше остальных."
                    } else {
                        "Start with the ingredients that expire the soonest."
                    },
                )
            }
            recipes += fallbackRecipe
        }

        return recipes.take(3)
    }
}

object AssistantReplyBuilder {
    fun answer(
        prompt: String,
        items: List<FoodItem>,
        profile: UserProfile,
    ): AssistantReply {
        val language = profile.appLanguage
        val activeItems = items.filter { it.status == FoodStatus.ACTIVE }
        val expiringSoon = activeItems.filter {
            it.expirationEpochDay - LocalDate.now().toEpochDay() <= 3
        }
        val cookNowItems = activeItems.filter {
            it.expirationEpochDay - LocalDate.now().toEpochDay() <= 2
        }
        val recipes = RecipeSuggestionEngine.suggest(activeItems, language)
        val normalized = prompt.lowercase()
        val preferenceNote = customFoodPreference(profile)?.let { preference ->
            if (language == AppLanguage.RUSSIAN) {
                " Учту пищевое предпочтение: $preference."
            } else {
                " I will keep your food preference in mind: $preference."
            }
        }.orEmpty()

        return when {
            normalized.contains("cook now") || normalized.contains("готовь сейчас") -> {
                val prioritized = recipes.sortedByDescending { recipe ->
                    val title = recipe.title.lowercase()
                    cookNowItems.count { item -> title.contains(item.name.lowercase()) }
                }
                AssistantReply(
                    text = if (language == AppLanguage.RUSSIAN) {
                        if (cookNowItems.isEmpty()) {
                            "На ближайшие 2 дня нет критичных продуктов, но вот быстрые варианты из текущего запаса."
                        } else {
                            "Режим \"Готовь сейчас\": сначала используй ${cookNowItems.joinToString { it.name }}."
                        }
                    } else {
                        if (cookNowItems.isEmpty()) {
                            "No critical items in the next 2 days, but here are quick recipes from your stock."
                        } else {
                            "Cook now mode: prioritize ${cookNowItems.joinToString { it.name }} first."
                        }
                    } + preferenceNote,
                    recipes = prioritized,
                )
            }

            normalized.contains("cook") || normalized.contains("recipe") ||
                normalized.contains("готов") || normalized.contains("рецепт") -> {
                AssistantReply(
                    text = if (language == AppLanguage.RUSSIAN) {
                        "Я посмотрел твой текущий запас продуктов и подобрал блюда, которые помогут использовать продукты вовремя."
                    } else {
                        "I checked your current inventory and picked meals that help you use products on time."
                    } + preferenceNote,
                    recipes = recipes,
                )
            }

            normalized.contains("expire") || normalized.contains("expiration") ||
                normalized.contains("срок") || normalized.contains("испорт") -> {
                AssistantReply(
                    text = if (expiringSoon.isNotEmpty()) {
                        if (language == AppLanguage.RUSSIAN) {
                            "Скоро истекает срок у: ${expiringSoon.joinToString { it.name }}. Лучше использовать их в первую очередь."
                        } else {
                            "These items are expiring soon: ${expiringSoon.joinToString { it.name }}. Use them first."
                        }
                    } else {
                        if (language == AppLanguage.RUSSIAN) {
                            "Сейчас у тебя нет критичных продуктов с истекающим сроком."
                        } else {
                            "You do not have critically expiring items right now."
                        }
                    } + preferenceNote,
                )
            }

            normalized.contains("money") || normalized.contains("save") ||
                normalized.contains("деньг") || normalized.contains("эконом") -> {
                AssistantReply(
                    text = if (language == AppLanguage.RUSSIAN) {
                        "Чтобы экономить деньги, сначала используй более старые продукты, следи за остатками и готовь из того, что уже есть дома. Текущая цель по экономии: %.0f KZT."
                            .format(profile.monthlySavingsGoal)
                    } else {
                        "To save money, use older products first, track leftovers, and cook from what you already have at home. Your current savings goal is %.0f KZT."
                            .format(profile.monthlySavingsGoal)
                    },
                )
            }

            normalized.contains("hello") || normalized.contains("hi") ||
                normalized.contains("привет") || normalized.contains("здрав") -> {
                AssistantReply(
                    text = if (language == AppLanguage.RUSSIAN) {
                        "Привет, ${profile.displayName.ifBlank { "друг" }}. Я могу подсказать, что приготовить, что скоро испортится и как сократить пищевые потери."
                    } else {
                        "Hi, ${profile.displayName.ifBlank { "friend" }}. I can help you decide what to cook, what expires soon, and how to reduce food waste."
                    },
                )
            }

            else -> {
                AssistantReply(
                    text = if (language == AppLanguage.RUSSIAN) {
                        "Сейчас у тебя ${activeItems.size} активных продуктов. Попробуй спросить: что приготовить, что скоро испортится, или как сэкономить деньги."
                    } else {
                        "You currently have ${activeItems.size} active food items. Try asking what to cook, what expires soon, or how to save money."
                    } + preferenceNote,
                    recipes = recipes.take(2),
                )
            }
        }
    }
}

class ObserveAssistantInsightsUseCase @Inject constructor(
    private val foodRepository: FoodRepository,
    private val profileRepository: ProfileRepository,
) {
    operator fun invoke(): Flow<List<AssistantInsight>> =
        combine(
            foodRepository.observeInventory(),
            profileRepository.observeProfile(),
        ) { items, profile ->
            AssistantInsightsBuilder.build(items, profile)
        }
}

class ObserveWasteRiskUseCase @Inject constructor(
    private val foodRepository: FoodRepository,
) {
    operator fun invoke(): Flow<Int> =
        foodRepository.observeInventory().map { items ->
            val today = LocalDate.now().toEpochDay()
            items.count {
                it.status == FoodStatus.ACTIVE && it.expirationEpochDay - today <= 3
            }
        }
}

class GenerateAssistantReplyUseCase @Inject constructor(
    private val foodRepository: FoodRepository,
    private val profileRepository: ProfileRepository,
) {
    suspend operator fun invoke(prompt: String): AssistantReply {
        val items = foodRepository.observeInventory().first()
        val profile = profileRepository.observeProfile().first()
        return AssistantReplyBuilder.answer(prompt, items, profile)
    }
}
