package com.example.auditflow.presentation.dashboard

import com.example.auditflow.domain.model.ExpenseItem

data class DashboardUiState(
    val isLoading: Boolean = true,
    val department: String? = null,
    val availableExpenseTypes: List<String> = emptyList(),
    val selectedExpenseType: String = "",
    val expenseAmount: String = "",
    val expenses: List<ExpenseItem> = emptyList(), // NEW: Holds the real-time records
    val error: String? = null,
    val successMessage: String? = null,
    val isLoggedOut: Boolean = false,
    val needsOnboarding: Boolean = false
)