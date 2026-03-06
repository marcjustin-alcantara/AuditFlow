package com.example.auditflow.domain.repository

import com.example.auditflow.domain.model.ExpenseItem

interface ExpenseRepository {
    suspend fun saveExpense(userId: String, expense: ExpenseItem): Result<Unit>
}