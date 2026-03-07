package com.example.auditflow.domain.use_case.expense

import com.example.auditflow.domain.model.ExpenseItem
import com.example.auditflow.domain.repository.ExpenseRepository
import kotlinx.coroutines.flow.Flow

class GetExpensesUseCase(private val repository: ExpenseRepository) {
    operator fun invoke(userId: String): Flow<List<ExpenseItem>> {
        if (userId.isBlank()) {
            return kotlinx.coroutines.flow.emptyFlow()
        }
        return repository.getExpenses(userId)
    }
}