package com.example.auditflow.presentation.onboarding

sealed class OnboardingIntent {
    data class SelectDepartment(val department: String) : OnboardingIntent()
    object SaveDepartment : OnboardingIntent()
    object ClearError : OnboardingIntent()
}