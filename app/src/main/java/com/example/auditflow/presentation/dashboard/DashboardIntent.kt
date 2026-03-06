package com.example.auditflow.presentation.dashboard

sealed class DashboardIntent {
    object LoadProfile : DashboardIntent()
    data class SelectExpenseType(val type: String) : DashboardIntent()
    data class UpdateAmount(val amount: String) : DashboardIntent()
    object SubmitExpense : DashboardIntent()
    object Logout : DashboardIntent()
    object ClearMessages : DashboardIntent()
}