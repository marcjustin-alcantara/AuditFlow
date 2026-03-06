package com.example.auditflow.domain.use_case.auth

import com.example.auditflow.domain.repository.AuthRepository

class LogoutUseCase(private val repository: AuthRepository) {
    suspend operator fun invoke() {
        repository.logout()
    }
}