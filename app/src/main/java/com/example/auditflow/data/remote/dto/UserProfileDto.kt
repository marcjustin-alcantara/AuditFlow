package com.example.auditflow.data.remote.dto

// Firebase requires default values to convert documents back into objects
data class UserProfileDto(
    val department: String = "",
    val joinDate: Long = 0L
)