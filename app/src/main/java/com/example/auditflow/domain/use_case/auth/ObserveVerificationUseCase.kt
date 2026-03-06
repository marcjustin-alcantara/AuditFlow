package com.example.auditflow.domain.use_case.auth

import com.example.auditflow.domain.repository.AuthRepository
import kotlinx.coroutines.flow.Flow

class ObserveVerificationUseCase(private val repository: AuthRepository) {
    operator fun invoke(): Flow<Boolean> {
        return repository.observeEmailVerificationState()
    }
}