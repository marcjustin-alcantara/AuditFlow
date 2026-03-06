package com.example.auditflow.presentation.onboarding

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.auditflow.domain.use_case.auth.GetCurrentUserIdUseCase
import com.example.auditflow.domain.use_case.profile.SaveUserProfileUseCase
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class OnboardingViewModel(
    private val saveUserProfileUseCase: SaveUserProfileUseCase,
    private val getCurrentUserIdUseCase: GetCurrentUserIdUseCase
) : ViewModel() {

    private val _state = MutableStateFlow(OnboardingUiState())
    val state: StateFlow<OnboardingUiState> = _state.asStateFlow()

    fun processIntent(intent: OnboardingIntent) {
        when (intent) {
            is OnboardingIntent.SelectDepartment -> {
                _state.update { it.copy(selectedDepartment = intent.department, error = null) }
            }
            is OnboardingIntent.SaveDepartment -> handleSaveDepartment()
            is OnboardingIntent.ClearError -> _state.update { it.copy(error = null) }
        }
    }

    private fun handleSaveDepartment() {
        val department = _state.value.selectedDepartment
        if (department == null) {
            _state.update { it.copy(error = "Please select a department.") }
            return
        }

        val userId = getCurrentUserIdUseCase()
        if (userId == null) {
            _state.update { it.copy(error = "Authentication error: User not found.") }
            return
        }

        viewModelScope.launch {
            _state.update { it.copy(isLoading = true, error = null) }

            val result = saveUserProfileUseCase(userId, department)

            if (result.isSuccess) {
                _state.update { it.copy(isLoading = false, isSaved = true) }
            } else {
                _state.update {
                    it.copy(isLoading = false, error = result.exceptionOrNull()?.message)
                }
            }
        }
    }
}