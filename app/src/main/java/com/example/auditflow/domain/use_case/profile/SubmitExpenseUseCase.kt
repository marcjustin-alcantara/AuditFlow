package com.example.auditflow.domain.use_case.expense

import com.example.auditflow.domain.model.ExpenseItem
import com.example.auditflow.domain.repository.ExpenseRepository

class SubmitExpenseUseCase(private val repository: ExpenseRepository) {
    suspend operator fun invoke(userId: String, expenseType: String, amount: Double): Result<Unit> {
        if (userId.isBlank()) {
            return Result.failure(Exception("Cannot submit expense: User ID is missing."))
        }
        if (expenseType.isBlank() || amount <= 0.0) {
            return Result.failure(Exception("Please provide a valid expense type and amount greater than 0."))
        }

        // Business Rule: Expense timestamp is auto-generated upon submission
        val expense = ExpenseItem(
            expenseType = expenseType,
            amount = amount,
            timestamp = System.currentTimeMillis()
        )

        return repository.saveExpense(userId, expense)
    }
}