package com.example.auditflow.data.mapper

import com.example.auditflow.data.remote.dto.UserProfileDto
import com.example.auditflow.domain.model.UserProfile

fun UserProfile.toDto(): UserProfileDto {
    return UserProfileDto(
        department = this.department,
        joinDate = this.joinDate
    )
}

fun UserProfileDto.toDomain(): UserProfile {
    return UserProfile(
        department = this.department,
        joinDate = this.joinDate
    )
}