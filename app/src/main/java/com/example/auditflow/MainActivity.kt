package com.example.auditflow

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.auditflow.data.repository.AuthRepositoryImpl
import com.example.auditflow.data.repository.ExpenseRepositoryImpl
import com.example.auditflow.data.repository.ProfileRepositoryImpl
import com.example.auditflow.domain.use_case.auth.*
import com.example.auditflow.domain.use_case.expense.SubmitExpenseUseCase
import com.example.auditflow.domain.use_case.profile.GetUserProfileUseCase
import com.example.auditflow.domain.use_case.profile.SaveUserProfileUseCase
import com.example.auditflow.presentation.auth.AuthScreen
import com.example.auditflow.presentation.auth.AuthViewModel
import com.example.auditflow.presentation.dashboard.DashboardScreen
import com.example.auditflow.presentation.dashboard.DashboardViewModel
import com.example.auditflow.presentation.onboarding.OnboardingScreen
import com.example.auditflow.presentation.onboarding.OnboardingViewModel

// THE CRITICAL FIX: Properly importing the AuditFlowTheme function
import com.example.auditflow.presentation.theme.AuditFlowTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            AuditFlowTheme {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    AuditFlowApp()
                }
            }
        }
    }
}

@Composable
fun AuditFlowApp() {
    val navController = rememberNavController()

    // 1. Data Layer: Repositories
    val authRepository = remember { AuthRepositoryImpl() }
    val profileRepository = remember { ProfileRepositoryImpl() }
    val expenseRepository = remember { ExpenseRepositoryImpl() }

    // 2. Domain Layer: Use Cases
    val registerUseCase = remember { RegisterUseCase(authRepository) }
    val loginUseCase = remember { LoginUseCase(authRepository) }
    val observeVerificationUseCase = remember { ObserveVerificationUseCase(authRepository) }
    val resendVerificationUseCase = remember { ResendVerificationUseCase(authRepository) }
    val logoutUseCase = remember { LogoutUseCase(authRepository) }
    val getCurrentUserIdUseCase = remember { GetCurrentUserIdUseCase(authRepository) }

    val saveUserProfileUseCase = remember { SaveUserProfileUseCase(profileRepository) }
    val getUserProfileUseCase = remember { GetUserProfileUseCase(profileRepository) }
    val submitExpenseUseCase = remember { SubmitExpenseUseCase(expenseRepository) }

    // Initial Routing Logic
    val startDestination = if (getCurrentUserIdUseCase() != null) "dashboard" else "auth"

    NavHost(navController = navController, startDestination = startDestination) {

        composable("auth") {
            val authViewModel = remember {
                AuthViewModel(registerUseCase, loginUseCase, observeVerificationUseCase, resendVerificationUseCase)
            }
            AuthScreen(
                viewModel = authViewModel,
                onNavigateToDashboard = {
                    navController.navigate("dashboard") {
                        popUpTo("auth") { inclusive = true }
                    }
                }
            )
        }

        composable("onboarding") {
            val onboardingViewModel = remember {
                OnboardingViewModel(saveUserProfileUseCase, getCurrentUserIdUseCase)
            }
            OnboardingScreen(
                viewModel = onboardingViewModel,
                onNavigateToDashboard = {
                    navController.navigate("dashboard") {
                        popUpTo("onboarding") { inclusive = true }
                    }
                }
            )
        }

        composable("dashboard") {
            val dashboardViewModel = remember {
                DashboardViewModel(getUserProfileUseCase, submitExpenseUseCase, logoutUseCase, getCurrentUserIdUseCase)
            }
            DashboardScreen(
                viewModel = dashboardViewModel,
                onNavigateToAuth = {
                    navController.navigate("auth") {
                        popUpTo("dashboard") { inclusive = true }
                    }
                },
                onNavigateToOnboarding = {
                    navController.navigate("onboarding")
                }
            )
        }
    }
}