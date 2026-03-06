package com.example.auditflow.data.repository

import com.example.auditflow.data.mapper.toDto
import com.example.auditflow.domain.model.ExpenseItem
import com.example.auditflow.domain.repository.ExpenseRepository
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.tasks.await

class ExpenseRepositoryImpl : ExpenseRepository {
    private val db = FirebaseFirestore.getInstance()

    override suspend fun saveExpense(userId: String, expense: ExpenseItem): Result<Unit> {
        return try {
            // Adds the expense DTO into the specific user's 'expenses' sub-collection
            db.collection("users")
                .document(userId)
                .collection("expenses")
                .add(expense.toDto())
                .await()

            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}