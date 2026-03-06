package com.example.auditflow.presentation.auth

sealed class AuthIntent {
    data class Register(val email: String, val pass: String) : AuthIntent()
    data class Login(val email: String, val pass: String) : AuthIntent()
    object StartVerificationPolling : AuthIntent()
    object ResendVerificationEmail : AuthIntent()
    object ClearMessages : AuthIntent()
}