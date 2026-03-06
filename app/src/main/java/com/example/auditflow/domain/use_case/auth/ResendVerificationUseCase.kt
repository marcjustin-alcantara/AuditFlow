package com.example.auditflow.domain.use_case.auth

import com.example.auditflow.domain.repository.AuthRepository

class ResendVerificationUseCase(private val repository: AuthRepository) {
    suspend operator fun invoke(): Result<Unit> {
        return repository.sendVerificationEmail()
    }
}