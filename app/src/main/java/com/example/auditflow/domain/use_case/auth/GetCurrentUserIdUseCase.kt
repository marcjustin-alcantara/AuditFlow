package com.example.auditflow.domain.use_case.auth

import com.example.auditflow.domain.repository.AuthRepository

class GetCurrentUserIdUseCase(private val repository: AuthRepository) {
    operator fun invoke(): String? {
        return repository.getCurrentUserId()
    }
}