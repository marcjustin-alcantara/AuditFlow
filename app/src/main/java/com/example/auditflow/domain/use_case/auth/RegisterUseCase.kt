package com.example.auditflow.domain.use_case.auth

import com.example.auditflow.domain.repository.AuthRepository

class RegisterUseCase(private val repository: AuthRepository) {
    suspend operator fun invoke(email: String, password: String): Result<Unit> {
        if (email.isBlank() || password.isBlank()) {
            return Result.failure(Exception("Email and password cannot be empty."))
        }
        if (password.length < 6) {
            return Result.failure(Exception("Password must be at least 6 characters long."))
        }
        return repository.register(email, password)
    }
}