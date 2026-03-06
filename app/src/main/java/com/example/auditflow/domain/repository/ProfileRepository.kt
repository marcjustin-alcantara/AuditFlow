package com.example.auditflow.domain.repository

import com.example.auditflow.domain.model.UserProfile

interface ProfileRepository {
    suspend fun saveProfile(userId: String, profile: UserProfile): Result<Unit>
    suspend fun getProfile(userId: String): Result<UserProfile?>
}