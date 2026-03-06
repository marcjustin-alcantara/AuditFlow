package com.example.auditflow.presentation.auth

data class AuthUiState(
    val isLoading: Boolean = false,
    val isRegistered: Boolean = false,
    val isEmailVerified: Boolean = false,
    val error: String? = null,
    val successMessage: String? = null
)