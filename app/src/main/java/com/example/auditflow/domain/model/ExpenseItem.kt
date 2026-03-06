package com.example.auditflow.domain.model

data class ExpenseItem(
    val expenseType: String = "",
    val amount: Double = 0.0,
    val timestamp: Long = 0L
)