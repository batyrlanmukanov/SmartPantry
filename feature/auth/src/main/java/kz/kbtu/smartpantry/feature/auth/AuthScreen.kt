package kz.kbtu.smartpantry.feature.auth

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import kz.kbtu.smartpantry.core.ui.tr

const val AUTH_ROUTE = "auth"

@Composable
fun AuthScreen(
    viewModel: AuthViewModel = hiltViewModel(),
) {
    val state by viewModel.state.collectAsStateWithLifecycle()

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(24.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp),
    ) {
        Text(
            text = tr("SmartPantry", "SmartPantry"),
            style = MaterialTheme.typography.headlineMedium,
        )
        Text(
            text = tr(
                "Local authentication for the project demo. Use sign up once, then sign in with your credentials.",
                "Локальная авторизация для учебного проекта. Сначала зарегистрируйся, затем входи с этими данными.",
            ),
            style = MaterialTheme.typography.bodyLarge,
        )

        Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            OutlinedButton(
                onClick = { viewModel.setSignUpMode(false) },
                modifier = Modifier.weight(1f),
            ) {
                Text(if (state.isSignUpMode) tr("Sign in", "Вход") else "✓ ${tr("Sign in", "Вход")}")
            }
            OutlinedButton(
                onClick = { viewModel.setSignUpMode(true) },
                modifier = Modifier.weight(1f),
            ) {
                Text(if (state.isSignUpMode) "✓ ${tr("Sign up", "Регистрация")}" else tr("Sign up", "Регистрация"))
            }
        }

        if (state.isSignUpMode) {
            OutlinedTextField(
                value = state.displayName,
                onValueChange = viewModel::updateName,
                label = { Text(tr("Full name", "Полное имя")) },
                modifier = Modifier.fillMaxWidth(),
            )
        }
        OutlinedTextField(
            value = state.email,
            onValueChange = viewModel::updateEmail,
            label = { Text(tr("Email", "Почта")) },
            modifier = Modifier.fillMaxWidth(),
        )
        OutlinedTextField(
            value = state.password,
            onValueChange = viewModel::updatePassword,
            label = { Text(tr("Password", "Пароль")) },
            modifier = Modifier.fillMaxWidth(),
        )
        if (state.isSignUpMode) {
            OutlinedTextField(
                value = state.confirmPassword,
                onValueChange = viewModel::updateConfirmPassword,
                label = { Text(tr("Confirm password", "Подтвердите пароль")) },
                modifier = Modifier.fillMaxWidth(),
            )
        }

        Text(
            text = if (state.isSignUpMode) {
                tr(
                    "Use a valid email and a password with at least 6 characters.",
                    "Используй корректный email и пароль минимум из 6 символов.",
                )
            } else {
                tr(
                    "Sign in with your registered email and password.",
                    "Входите с зарегистрированным email и паролем.",
                )
            },
            style = MaterialTheme.typography.bodySmall,
        )

        state.errorMessage?.let {
            Text(
                text = it,
                color = MaterialTheme.colorScheme.error,
                style = MaterialTheme.typography.bodyMedium,
            )
        }

        Button(
            onClick = viewModel::signIn,
            modifier = Modifier.fillMaxWidth(),
        ) {
            Text(if (state.isSignUpMode) tr("Create account", "Создать аккаунт") else tr("Continue", "Продолжить"))
        }
    }
}
