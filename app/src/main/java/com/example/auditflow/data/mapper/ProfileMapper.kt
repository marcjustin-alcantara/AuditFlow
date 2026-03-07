package com.example.auditflow.data.mapper

import com.example.auditflow.data.remote.dto.UserProfileDto
import com.example.auditflow.domain.model.UserProfile
import java.util.Date

fun UserProfile.toDto(): UserProfileDto {
    return UserProfileDto(
        department = this.department,
        joinDate = Date(this.joinDate) // Converts Long into a Date object
    )
}

fun UserProfileDto.toDomain(): UserProfile {
    return UserProfile(
        department = this.department,
        joinDate = this.joinDate?.time ?: 0L // Safely converts Date back to Long
    )
}