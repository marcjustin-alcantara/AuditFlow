package com.example.auditflow.domain.use_case.profile

import com.example.auditflow.domain.model.UserProfile
import com.example.auditflow.domain.repository.ProfileRepository

class SaveUserProfileUseCase(private val repository: ProfileRepository) {
    suspend operator fun invoke(userId: String, department: String): Result<Unit> {
        if (userId.isBlank() || department.isBlank()) {
            return Result.failure(Exception("User ID or Department is missing."))
        }

        // Business Rule: The join date is strictly stamped at the exact moment of onboarding
        val profile = UserProfile(
            department = department,
            joinDate = System.currentTimeMillis()
        )

        return repository.saveProfile(userId, profile)
    }
}