package kz.kbtu.smartpantry.feature.auth

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kz.kbtu.smartpantry.core.domain.CompleteSignInUseCase
import kz.kbtu.smartpantry.core.domain.CompleteSignUpUseCase

data class AuthUiState(
    val isSignUpMode: Boolean = false,
    val displayName: String = "",
    val email: String = "",
    val password: String = "",
    val confirmPassword: String = "",
    val errorMessage: String? = null,
)

@HiltViewModel
class AuthViewModel @Inject constructor(
    private val completeSignInUseCase: CompleteSignInUseCase,
    private val completeSignUpUseCase: CompleteSignUpUseCase,
) : ViewModel() {

    private val _state = MutableStateFlow(AuthUiState())
    val state: StateFlow<AuthUiState> = _state.asStateFlow()

    fun updateName(value: String) {
        _state.update { it.copy(displayName = value, errorMessage = null) }
    }

    fun updateEmail(value: String) {
        _state.update { it.copy(email = value, errorMessage = null) }
    }

    fun updatePassword(value: String) {
        _state.update { it.copy(password = value, errorMessage = null) }
    }

    fun updateConfirmPassword(value: String) {
        _state.update { it.copy(confirmPassword = value, errorMessage = null) }
    }

    fun setSignUpMode(enabled: Boolean) {
        _state.update {
            it.copy(
                isSignUpMode = enabled,
                confirmPassword = if (enabled) it.confirmPassword else "",
                errorMessage = null,
            )
        }
    }

    fun signIn() {
        val snapshot = state.value
        if (snapshot.email.isBlank() || snapshot.password.isBlank()) {
            _state.update { it.copy(errorMessage = "Fill in all authentication fields / Заполни все поля.") }
            return
        }
        if (!isValidEmail(snapshot.email)) {
            _state.update { it.copy(errorMessage = "Enter a valid email / Введи корректный email.") }
            return
        }
        if (snapshot.password.length < 6) {
            _state.update { it.copy(errorMessage = "Password must be at least 6 characters / Пароль минимум 6 символов.") }
            return
        }
        if (snapshot.isSignUpMode && snapshot.displayName.isBlank()) {
            _state.update { it.copy(errorMessage = "Name is required for sign up / Имя обязательно для регистрации.") }
            return
        }
        if (snapshot.isSignUpMode && snapshot.password != snapshot.confirmPassword) {
            _state.update { it.copy(errorMessage = "Passwords do not match / Пароли не совпадают.") }
            return
        }

        viewModelScope.launch {
            runCatching {
                if (snapshot.isSignUpMode) {
                    completeSignUpUseCase(
                        displayName = snapshot.displayName,
                        email = snapshot.email,
                        password = snapshot.password,
                    )
                } else {
                    completeSignInUseCase(
                        email = snapshot.email,
                        password = snapshot.password,
                    )
                }
            }.onFailure { throwable ->
                _state.update {
                    it.copy(
                        errorMessage = throwable.message
                            ?: "Authentication failed / Ошибка авторизации.",
                    )
                }
            }
        }
    }

    private fun isValidEmail(value: String): Boolean {
        val email = value.trim()
        return email.contains("@") && email.substringAfter("@").contains(".")
    }
}
