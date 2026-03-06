package com.example.auditflow.presentation.onboarding

data class OnboardingUiState(
    val selectedDepartment: String? = null,
    val isLoading: Boolean = false,
    val isSaved: Boolean = false,
    val error: String? = null
)