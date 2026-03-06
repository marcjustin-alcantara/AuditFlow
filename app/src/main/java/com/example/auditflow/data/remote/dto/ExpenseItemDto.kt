package com.example.auditflow.data.remote.dto

data class ExpenseItemDto(
    val expenseType: String = "",
    val amount: Double = 0.0,
    val timestamp: Long = 0L
)