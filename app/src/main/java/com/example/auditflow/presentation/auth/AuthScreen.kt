package com.example.auditflow.presentation.auth

import android.widget.Toast
import androidx.compose.animation.Crossfade
import androidx.compose.animation.core.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CutCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
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
            Toast.makeText(context, "SYS_ERR: $it", Toast.LENGTH_LONG).show()
            viewModel.processIntent(AuthIntent.ClearMessages)
        }
        state.successMessage?.let {
            Toast.makeText(context, "SYS_MSG: $it", Toast.LENGTH_SHORT).show()
            viewModel.processIntent(AuthIntent.ClearMessages)
        }
    }

    Surface(modifier = Modifier.fillMaxSize(), color = MaterialTheme.colorScheme.background) {
        Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
            when {
                state.isLoading -> CircularProgressIndicator(color = MaterialTheme.colorScheme.primary)
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
        CircularProgressIndicator(color = MaterialTheme.colorScheme.primary)
        Spacer(modifier = Modifier.height(24.dp))
        Row(verticalAlignment = Alignment.CenterVertically) {
            Text(text = "VERIFY NODE IDENTITY", style = MaterialTheme.typography.titleLarge, color = MaterialTheme.colorScheme.primary)
            TerminalCursor(color = MaterialTheme.colorScheme.primary)
        }
        Spacer(modifier = Modifier.height(8.dp))
        Text(text = ">> awaiting_authorization_ping...", color = MaterialTheme.colorScheme.onBackground)
        Spacer(modifier = Modifier.height(24.dp))
        OutlinedButton(
            onClick = onResendEmail,
            shape = CutCornerShape(topStart = 16.dp, bottomEnd = 16.dp)
        ) {
            Text("RESEND_SIGNAL".uppercase())
        }
    }
}

@Composable
fun AuthForm(onRegister: (String, String) -> Unit, onLogin: (String, String) -> Unit) {
    var email by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }

    // NEW: The magic toggle state for intuitive UX!
    var isLoginMode by remember { mutableStateOf(true) }

    Column(horizontalAlignment = Alignment.CenterHorizontally, modifier = Modifier.padding(24.dp)) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            // Title reacts to the current mode
            Text(
                text = if (isLoginMode) "SYS.AUTH // LOGIN" else "SYS.AUTH // REGISTER",
                style = MaterialTheme.typography.headlineMedium,
                color = MaterialTheme.colorScheme.primary
            )
            TerminalCursor(color = MaterialTheme.colorScheme.primary)
        }
        Spacer(modifier = Modifier.height(32.dp))

        OutlinedTextField(
            value = email,
            onValueChange = { email = it },
            label = { Text(">> sys.req_email:") },
            modifier = Modifier.fillMaxWidth(),
            shape = CutCornerShape(8.dp)
        )
        Spacer(modifier = Modifier.height(16.dp))
        OutlinedTextField(
            value = password,
            onValueChange = { password = it },
            label = { Text(">> root@auth_pass:") },
            visualTransformation = PasswordVisualTransformation(),
            modifier = Modifier.fillMaxWidth(),
            shape = CutCornerShape(8.dp)
        )
        Spacer(modifier = Modifier.height(32.dp))

        // NEW: Crossfade animation seamlessly swaps the buttons based on the mode
        Crossfade(targetState = isLoginMode, label = "authModeAnimation") { loginMode ->
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                if (loginMode) {
                    Button(
                        onClick = { onLogin(email, password) },
                        modifier = Modifier.fillMaxWidth().height(50.dp),
                        shape = CutCornerShape(topStart = 16.dp, bottomEnd = 16.dp)
                    ) {
                        Text("INITIALIZE LOGIN")
                    }
                    Spacer(modifier = Modifier.height(16.dp))
                    TextButton(onClick = { isLoginMode = false }) {
                        Text(">> UNREGISTERED? CREATE_NODE", color = MaterialTheme.colorScheme.secondary)
                    }
                } else {
                    Button(
                        onClick = { onRegister(email, password) },
                        modifier = Modifier.fillMaxWidth().height(50.dp),
                        shape = CutCornerShape(topStart = 16.dp, bottomEnd = 16.dp)
                    ) {
                        Text("REGISTER NEW NODE")
                    }
                    Spacer(modifier = Modifier.height(16.dp))
                    TextButton(onClick = { isLoginMode = true }) {
                        Text(">> KNOWN_NODE? AUTHENTICATE", color = MaterialTheme.colorScheme.secondary)
                    }
                }
            }
        }
    }
}

@Composable
fun TerminalCursor(color: androidx.compose.ui.graphics.Color) {
    val infiniteTransition = rememberInfiniteTransition(label = "cursor")
    val alpha by infiniteTransition.animateFloat(
        initialValue = 0f,
        targetValue = 1f,
        animationSpec = infiniteRepeatable(
            animation = tween(400, easing = LinearEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "cursorAlpha"
    )
    Text(
        text = "_",
        style = MaterialTheme.typography.headlineMedium,
        color = color,
        modifier = Modifier.alpha(if (alpha > 0.5f) 1f else 0f)
    )
}