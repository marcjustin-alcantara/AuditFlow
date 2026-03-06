package com.example.auditflow.data.repository

import com.example.auditflow.domain.repository.AuthRepository
import com.google.firebase.auth.FirebaseAuth
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.tasks.await

class AuthRepositoryImpl : AuthRepository {
    private val auth = FirebaseAuth.getInstance()

    override suspend fun register(email: String, password: String): Result<Unit> {
        return try {
            auth.createUserWithEmailAndPassword(email, password).await()
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override suspend fun login(email: String, password: String): Result<Unit> {
        return try {
            auth.signInWithEmailAndPassword(email, password).await()
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override suspend fun sendVerificationEmail(): Result<Unit> {
        return try {
            auth.currentUser?.sendEmailVerification()?.await()
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    // Polling mechanism to ensure a strict "Verification Gate"
    override fun observeEmailVerificationState(): Flow<Boolean> = flow {
        while (true) {
            val user = auth.currentUser
            if (user != null) {
                user.reload().await() // Forces Firebase to fetch the latest status
                val isVerified = user.isEmailVerified
                emit(isVerified)
                if (isVerified) break // Stop polling once verified
            }
            delay(3000) // Poll every 3 seconds to avoid rate limiting
        }
    }

    override fun getCurrentUserId(): String? {
        return auth.currentUser?.uid
    }

    override suspend fun logout() {
        auth.signOut()
    }
}