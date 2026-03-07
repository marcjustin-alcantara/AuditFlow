package com.example.auditflow.data.repository

import com.example.auditflow.data.mapper.toDomain
import com.example.auditflow.data.mapper.toDto
import com.example.auditflow.data.remote.dto.ExpenseItemDto
import com.example.auditflow.domain.model.ExpenseItem
import com.example.auditflow.domain.repository.ExpenseRepository
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.tasks.await

class ExpenseRepositoryImpl : ExpenseRepository {
    private val db = FirebaseFirestore.getInstance()

    override suspend fun saveExpense(userId: String, expense: ExpenseItem): Result<Unit> {
        return try {
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

    // NEW: Real-time listener mapped to a Kotlin Flow
    override fun getExpenses(userId: String): Flow<List<ExpenseItem>> = callbackFlow {
        val listener = db.collection("users")
            .document(userId)
            .collection("expenses")
            .orderBy("timestamp", Query.Direction.DESCENDING) // Newest first
            .addSnapshotListener { snapshot, error ->
                if (error != null) {
                    close(error)
                    return@addSnapshotListener
                }

                if (snapshot != null) {
                    val expenses = snapshot.documents.mapNotNull { doc ->
                        doc.toObject(ExpenseItemDto::class.java)?.toDomain()
                    }
                    trySend(expenses)
                }
            }

        // Prevent memory leaks when the user leaves the screen
        awaitClose { listener.remove() }
    }
}