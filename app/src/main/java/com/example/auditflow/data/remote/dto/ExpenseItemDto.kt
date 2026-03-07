package com.example.auditflow.data.remote.dto

import java.util.Date

data class ExpenseItemDto(
    val expenseType: String = "",
    val amount: Double = 0.0,
    val timestamp: Date? = null // Changed to Date for Firestore Timestamp
)