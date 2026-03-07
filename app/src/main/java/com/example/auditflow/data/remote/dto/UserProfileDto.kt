package com.example.auditflow.data.remote.dto

import java.util.Date

data class UserProfileDto(
    val department: String = "",
    val joinDate: Date? = null // Changed to Date for Firestore Timestamp
)