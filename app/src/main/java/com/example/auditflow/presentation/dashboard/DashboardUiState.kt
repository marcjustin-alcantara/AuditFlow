package com.example.auditflow.presentation.dashboard

data class DashboardUiState(
    val isLoading: Boolean = true,
    val department: String? = null,
    val availableExpenseTypes: List<String> = emptyList(),
    val selectedExpenseType: String = "",
    val expenseAmount: String = "",
    val error: String? = null,
    val successMessage: String? = null,
    val isLoggedOut: Boolean = false,
    val needsOnboarding: Boolean = false
)