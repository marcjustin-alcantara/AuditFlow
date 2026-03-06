package com.example.auditflow.domain.repository

import kotlinx.coroutines.flow.Flow

interface AuthRepository {
    suspend fun register(email: String, password: String): Result<Unit>
    suspend fun login(email: String, password: String): Result<Unit>
    suspend fun sendVerificationEmail(): Result<Unit>
    fun observeEmailVerificationState(): Flow<Boolean>
    fun getCurrentUserId(): String?
    suspend fun logout()
}