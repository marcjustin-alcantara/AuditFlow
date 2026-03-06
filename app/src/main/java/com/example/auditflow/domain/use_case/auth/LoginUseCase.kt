package com.example.auditflow.domain.use_case.auth

import com.example.auditflow.domain.repository.AuthRepository

class LoginUseCase(private val repository: AuthRepository) {
    suspend operator fun invoke(email: String, password: String): Result<Unit> {
        if (email.isBlank() || password.isBlank()) {
            return Result.failure(Exception("Email and password cannot be empty."))
        }
        return repository.login(email, password)
    }
}