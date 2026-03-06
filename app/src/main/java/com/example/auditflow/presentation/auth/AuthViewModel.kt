package com.example.auditflow.presentation.auth

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.auditflow.domain.use_case.auth.*
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class AuthViewModel(
    private val registerUseCase: RegisterUseCase,
    private val loginUseCase: LoginUseCase,
    private val observeVerificationUseCase: ObserveVerificationUseCase,
    private val resendVerificationUseCase: ResendVerificationUseCase
) : ViewModel() {

    private val _state = MutableStateFlow(AuthUiState())
    val state: StateFlow<AuthUiState> = _state.asStateFlow()

    fun processIntent(intent: AuthIntent) {
        when (intent) {
            is AuthIntent.Register -> handleRegister(intent.email, intent.pass)
            is AuthIntent.Login -> handleLogin(intent.email, intent.pass)
            is AuthIntent.StartVerificationPolling -> handlePolling()
            is AuthIntent.ResendVerificationEmail -> handleResendVerification()
            is AuthIntent.ClearMessages -> _state.update { it.copy(error = null, successMessage = null) }
        }
    }

    private fun handleRegister(email: String, pass: String) {
        viewModelScope.launch {
            _state.update { it.copy(isLoading = true, error = null) }

            val result = registerUseCase(email, pass)
            if (result.isSuccess) {
                resendVerificationUseCase()
                _state.update { it.copy(isLoading = false, isRegistered = true) }
                handlePolling()
            } else {
                _state.update {
                    it.copy(isLoading = false, error = result.exceptionOrNull()?.message)
                }
            }
        }
    }

    private fun handleLogin(email: String, pass: String) {
        viewModelScope.launch {
            _state.update { it.copy(isLoading = true, error = null) }

            val result = loginUseCase(email, pass)
            if (result.isSuccess) {
                _state.update { it.copy(isRegistered = true) }
                handlePolling()
            } else {
                _state.update {
                    it.copy(isLoading = false, error = result.exceptionOrNull()?.message)
                }
            }
        }
    }

    private fun handlePolling() {
        viewModelScope.launch {
            _state.update { it.copy(isLoading = true, error = null) }

            observeVerificationUseCase().collect { isVerified ->
                if (isVerified) {
                    _state.update {
                        it.copy(isLoading = false, isEmailVerified = true)
                    }
                } else {
                    _state.update {
                        it.copy(isLoading = false, isRegistered = true)
                    }
                }
            }
        }
    }

    private fun handleResendVerification() {
        viewModelScope.launch {
            val result = resendVerificationUseCase()
            if (result.isFailure) {
                _state.update { it.copy(error = result.exceptionOrNull()?.message) }
            } else {
                _state.update { it.copy(successMessage = "Verification email resent successfully!") }
            }
        }
    }
}