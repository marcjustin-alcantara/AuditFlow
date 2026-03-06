package com.example.auditflow.domain.use_case.profile

import com.example.auditflow.domain.model.UserProfile
import com.example.auditflow.domain.repository.ProfileRepository

class GetUserProfileUseCase(private val repository: ProfileRepository) {
    suspend operator fun invoke(userId: String): Result<UserProfile?> {
        if (userId.isBlank()) {
            return Result.failure(Exception("Cannot fetch profile: User ID is missing."))
        }
        return repository.getProfile(userId)
    }
}