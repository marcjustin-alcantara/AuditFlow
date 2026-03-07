package com.example.auditflow.domain.repository

import com.example.auditflow.domain.model.ExpenseItem
import kotlinx.coroutines.flow.Flow

interface ExpenseRepository {
    suspend fun saveExpense(userId: String, expense: ExpenseItem): Result<Unit>
    fun getExpenses(userId: String): Flow<List<ExpenseItem>> // NEW: Real-time data stream
}