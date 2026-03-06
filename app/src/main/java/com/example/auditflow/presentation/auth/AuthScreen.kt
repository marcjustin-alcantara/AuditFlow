package com.example.auditflow.presentation.auth

import android.widget.Toast
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.unit.dp

@Composable
fun AuthScreen(
    viewModel: AuthViewModel,
    onNavigateToDashboard: () -> Unit
) {
    val state by viewModel.state.collectAsState()
    val context = LocalContext.current

    LaunchedEffect(state.isEmailVerified) {
        if (state.isEmailVerified) {
            onNavigateToDashboard()
        }
    }

    LaunchedEffect(state.error, state.successMessage) {
        state.error?.let {
            Toast.makeText(context, it, Toast.LENGTH_SHORT).show()
            viewModel.processIntent(AuthIntent.ClearMessages)
        }
        state.successMessage?.let {
            Toast.makeText(context, it, Toast.LENGTH_SHORT).show()
            viewModel.processIntent(AuthIntent.ClearMessages)
        }
    }

    Surface(modifier = Modifier.fillMaxSize(), color = MaterialTheme.colorScheme.background) {
        Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
            when {
                state.isLoading -> CircularProgressIndicator()
                state.isRegistered && !state.isEmailVerified -> VerificationGateUi(
                    onResendEmail = { viewModel.processIntent(AuthIntent.ResendVerificationEmail) }
                )
                else -> AuthForm(
                    onRegister = { email, pass ->
                        viewModel.processIntent(AuthIntent.Register(email, pass))
                    },
                    onLogin = { email, pass ->
                        viewModel.processIntent(AuthIntent.Login(email, pass))
                    }
                )
            }
        }
    }
}

@Composable
fun VerificationGateUi(onResendEmail: () -> Unit) {
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        CircularProgressIndicator()
        Spacer(modifier = Modifier.height(24.dp))
        Text(text = "Verify Your Identity", style = MaterialTheme.typography.titleLarge)
        Spacer(modifier = Modifier.height(8.dp))
        Text(text = "Awaiting email verification to proceed...")
        Spacer(modifier = Modifier.height(24.dp))
        OutlinedButton(onClick = onResendEmail) {
            Text("Resend Verification Email")
        }
    }
}

@Composable
fun AuthForm(onRegister: (String, String) -> Unit, onLogin: (String, String) -> Unit) {
    var email by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }

    Column(horizontalAlignment = Alignment.CenterHorizontally, modifier = Modifier.padding(24.dp)) {
        Text("AuditFlow Portal", style = MaterialTheme.typography.headlineMedium)
        Spacer(modifier = Modifier.height(32.dp))

        OutlinedTextField(
            value = email,
            onValueChange = { email = it },
            label = { Text("Corporate Email") },
            modifier = Modifier.fillMaxWidth()
        )
        Spacer(modifier = Modifier.height(16.dp))
        OutlinedTextField(
            value = password,
            onValueChange = { password = it },
            label = { Text("Password") },
            visualTransformation = PasswordVisualTransformation(),
            modifier = Modifier.fillMaxWidth()
        )
        Spacer(modifier = Modifier.height(32.dp))

        Button(
            onClick = { onLogin(email, password) },
            modifier = Modifier.fillMaxWidth().height(50.dp)
        ) {
            Text("Secure Login")
        }
        Spacer(modifier = Modifier.height(16.dp))
        OutlinedButton(
            onClick = { onRegister(email, password) },
            modifier = Modifier.fillMaxWidth().height(50.dp)
        ) {
            Text("Register Account")
        }
    }
}