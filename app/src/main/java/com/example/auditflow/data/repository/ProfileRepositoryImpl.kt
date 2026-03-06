package com.example.auditflow.data.repository

import com.example.auditflow.data.mapper.toDomain
import com.example.auditflow.data.mapper.toDto
import com.example.auditflow.data.remote.dto.UserProfileDto
import com.example.auditflow.domain.model.UserProfile
import com.example.auditflow.domain.repository.ProfileRepository
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.tasks.await

class ProfileRepositoryImpl : ProfileRepository {
    private val db = FirebaseFirestore.getInstance()

    override suspend fun saveProfile(userId: String, profile: UserProfile): Result<Unit> {
        return try {
            // Converts the pure Domain Model into a Firebase DTO before saving
            db.collection("users").document(userId).set(profile.toDto()).await()
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override suspend fun getProfile(userId: String): Result<UserProfile?> {
        return try {
            val snapshot = db.collection("users").document(userId).get().await()
            if (snapshot.exists()) {
                // Converts the Firebase DTO back into the pure Domain Model
                val dto = snapshot.toObject(UserProfileDto::class.java)
                Result.success(dto?.toDomain())
            } else {
                Result.success(null)
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}