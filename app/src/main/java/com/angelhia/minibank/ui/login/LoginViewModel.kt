package com.angelhia.minibank.ui.login

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.angelhia.minibank.auth.BiometricResult
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

data class LoginUiState(
    val username: String = "",
    val password: String = "",
    val passwordVisible: Boolean = false,
    val isLoading: Boolean = false,
    val biometricAvailable: Boolean = false,
    val errorMessage: String? = null,
    val isLoggedIn: Boolean = false,
) {
    val canSubmit: Boolean get() = username.isNotBlank() && password.isNotBlank() && !isLoading
}

class LoginViewModel : ViewModel() {

    private val _uiState = MutableStateFlow(LoginUiState())
    val uiState: StateFlow<LoginUiState> = _uiState.asStateFlow()

    fun setBiometricAvailable(available: Boolean) =
        _uiState.update { it.copy(biometricAvailable = available) }

    fun onUsernameChange(value: String) =
        _uiState.update { it.copy(username = value, errorMessage = null) }

    fun onPasswordChange(value: String) =
        _uiState.update { it.copy(password = value, errorMessage = null) }

    fun togglePasswordVisibility() =
        _uiState.update { it.copy(passwordVisible = !it.passwordVisible) }

    fun dismissError() = _uiState.update { it.copy(errorMessage = null) }

    fun login() {
        val state = _uiState.value
        if (!state.canSubmit) return

        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true, errorMessage = null) }
            // TODO: replace with a real auth repository / API call.
            delay(1_000)
            val ok = state.username == "demo" && state.password == "password"
            _uiState.update {
                it.copy(
                    isLoading = false,
                    isLoggedIn = ok,
                    errorMessage = if (ok) null else "Invalid username or password",
                )
            }
        }
    }

    fun onBiometricResult(result: BiometricResult) {
        when (result) {
            BiometricResult.Success -> _uiState.update { it.copy(isLoggedIn = true, errorMessage = null) }
            BiometricResult.Cancelled -> Unit // User chose to type their password instead.
            is BiometricResult.Error -> _uiState.update { it.copy(errorMessage = result.message) }
        }
    }
}
