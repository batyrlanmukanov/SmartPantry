package kz.kbtu.smartpantry.feature.assistant

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kz.kbtu.smartpantry.core.domain.GenerateAssistantReplyUseCase
import kz.kbtu.smartpantry.core.domain.ObserveAssistantInsightsUseCase
import kz.kbtu.smartpantry.core.model.AppLanguage
import kz.kbtu.smartpantry.core.model.AssistantReply
import kz.kbtu.smartpantry.core.model.RecipeSuggestion

const val ASSISTANT_ROUTE = "assistant"

data class AssistantChatMessage(
    val text: String,
    val fromUser: Boolean,
    val recipes: List<RecipeSuggestion> = emptyList(),
)

data class AssistantUiState(
    val input: String = "",
    val messages: List<AssistantChatMessage> = emptyList(),
)

@HiltViewModel
class AssistantViewModel @Inject constructor(
    observeAssistantInsightsUseCase: ObserveAssistantInsightsUseCase,
    private val generateAssistantReplyUseCase: GenerateAssistantReplyUseCase,
) : ViewModel() {

    val insights = observeAssistantInsightsUseCase().stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5_000),
        initialValue = emptyList(),
    )

    private val _state = MutableStateFlow(AssistantUiState())
    val state: StateFlow<AssistantUiState> = _state.asStateFlow()

    fun ensureGreeting(language: AppLanguage) {
        val greeting = AssistantChatMessage(
            text = if (language == AppLanguage.RUSSIAN) {
                "Привет. Я помогу понять, что приготовить, что скоро испортится и как снизить пищевые потери."
            } else {
                "Hi. I can help you decide what to cook, what expires soon, and how to reduce food waste."
            },
            fromUser = false,
        )

        val messages = _state.value.messages
        _state.value = when {
            messages.isEmpty() -> AssistantUiState(messages = listOf(greeting))
            messages.size == 1 && !messages.first().fromUser -> {
                _state.value.copy(messages = listOf(greeting))
            }
            else -> _state.value
        }
    }

    fun updateInput(value: String) {
        _state.update { it.copy(input = value) }
    }

    fun sendPrompt(prompt: String) {
        if (prompt.isBlank()) return

        _state.update {
            it.copy(
                input = "",
                messages = it.messages + AssistantChatMessage(
                    text = prompt,
                    fromUser = true,
                ),
            )
        }

        viewModelScope.launch {
            val reply: AssistantReply = generateAssistantReplyUseCase(prompt)
            _state.update {
                it.copy(
                    messages = it.messages + AssistantChatMessage(
                        text = reply.text,
                        fromUser = false,
                        recipes = reply.recipes,
                    ),
                )
            }
        }
    }
}
