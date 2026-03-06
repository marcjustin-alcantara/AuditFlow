package com.example.auditflow.presentation.dashboard

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.auditflow.domain.use_case.auth.GetCurrentUserIdUseCase
import com.example.auditflow.domain.use_case.auth.LogoutUseCase
import com.example.auditflow.domain.use_case.expense.SubmitExpenseUseCase
import com.example.auditflow.domain.use_case.profile.GetUserProfileUseCase
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class DashboardViewModel(
    private val getUserProfileUseCase: GetUserProfileUseCase,
    private val submitExpenseUseCase: SubmitExpenseUseCase,
    private val logoutUseCase: LogoutUseCase,
    private val getCurrentUserIdUseCase: GetCurrentUserIdUseCase
) : ViewModel() {

    private val _state = MutableStateFlow(DashboardUiState())
    val state: StateFlow<DashboardUiState> = _state.asStateFlow()

    fun processIntent(intent: DashboardIntent) {
        when (intent) {
            is DashboardIntent.LoadProfile -> handleLoadProfile()
            is DashboardIntent.SelectExpenseType -> _state.update { it.copy(selectedExpenseType = intent.type) }
            is DashboardIntent.UpdateAmount -> _state.update { it.copy(expenseAmount = intent.amount) }
            is DashboardIntent.SubmitExpense -> handleSubmitExpense()
            is DashboardIntent.Logout -> handleLogout()
            is DashboardIntent.ClearMessages -> _state.update { it.copy(error = null, successMessage = null) }
        }
    }

    private fun handleLoadProfile() {
        val userId = getCurrentUserIdUseCase()
        if (userId == null) {
            _state.update { it.copy(isLoggedOut = true) }
            return
        }

        viewModelScope.launch {
            _state.update { it.copy(isLoading = true, error = null) }
            val result = getUserProfileUseCase(userId)

            if (result.isSuccess) {
                val profile = result.getOrNull()
                if (profile == null || profile.department.isBlank()) {
                    _state.update { it.copy(isLoading = false, needsOnboarding = true) }
                } else {
                    val expenseTypes = when (profile.department) {
                        "Sales" -> listOf("Travel", "Client Meals", "Accommodation")
                        "IT" -> listOf("Software Licenses", "Hardware", "Cloud Services")
                        "HR" -> listOf("Training", "Recruitment Events", "Team Building")
                        else -> emptyList()
                    }
                    _state.update {
                        it.copy(
                            isLoading = false,
                            department = profile.department,
                            availableExpenseTypes = expenseTypes,
                            selectedExpenseType = expenseTypes.firstOrNull() ?: ""
                        )
                    }
                }
            } else {
                _state.update { it.copy(isLoading = false, error = result.exceptionOrNull()?.message) }
            }
        }
    }

    private fun handleSubmitExpense() {
        val currentAmount = _state.value.expenseAmount.toDoubleOrNull()
        val currentType = _state.value.selectedExpenseType
        val userId = getCurrentUserIdUseCase()

        if (currentAmount == null || currentAmount <= 0) {
            _state.update { it.copy(error = "Please enter a valid amount greater than 0.") }
            return
        }
        if (userId == null) {
            _state.update { it.copy(error = "User session invalid.") }
            return
        }

        viewModelScope.launch {
            _state.update { it.copy(isLoading = true, error = null) }
            val result = submitExpenseUseCase(userId, currentType, currentAmount)

            if (result.isSuccess) {
                _state.update {
                    it.copy(
                        isLoading = false,
                        successMessage = "Expense submitted successfully!",
                        expenseAmount = "" // Clear the field
                    )
                }
            } else {
                _state.update { it.copy(isLoading = false, error = result.exceptionOrNull()?.message) }
            }
        }
    }

    private fun handleLogout() {
        viewModelScope.launch {
            logoutUseCase()
            _state.update { it.copy(isLoggedOut = true) }
        }
    }
}