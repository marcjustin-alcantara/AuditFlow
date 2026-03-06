package com.example.auditflow.presentation.onboarding

import android.widget.Toast
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.*
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInVertically
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CutCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp

@Composable
fun OnboardingScreen(
    viewModel: OnboardingViewModel,
    onNavigateToDashboard: () -> Unit
) {
    val state by viewModel.state.collectAsState()
    val context = LocalContext.current

    val departments = listOf("Sales", "IT", "HR")

    LaunchedEffect(state.isSaved) {
        if (state.isSaved) {
            onNavigateToDashboard()
        }
    }

    LaunchedEffect(state.error) {
        state.error?.let {
            Toast.makeText(context, "SYS_ERR: $it", Toast.LENGTH_SHORT).show()
            viewModel.processIntent(OnboardingIntent.ClearError)
        }
    }

    Column(
        modifier = Modifier.fillMaxSize().padding(24.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Text("SYS.CONFIG // DEPT_SELECT", style = MaterialTheme.typography.headlineMedium, color = MaterialTheme.colorScheme.primary)
            OnboardingCursor(color = MaterialTheme.colorScheme.primary)
        }
        Spacer(modifier = Modifier.height(16.dp))
        Text(">> input_required: select assigned department", style = MaterialTheme.typography.bodyLarge, color = MaterialTheme.colorScheme.onBackground)
        Spacer(modifier = Modifier.height(32.dp))

        departments.forEach { dept ->
            DepartmentItem(
                departmentName = dept,
                isSelected = state.selectedDepartment == dept,
                onClick = { viewModel.processIntent(OnboardingIntent.SelectDepartment(dept)) }
            )
            Spacer(modifier = Modifier.height(16.dp))
        }

        Spacer(modifier = Modifier.height(32.dp))

        if (state.isLoading) {
            CircularProgressIndicator(color = MaterialTheme.colorScheme.secondary)
        } else {
            AnimatedVisibility(
                visible = state.selectedDepartment != null,
                enter = fadeIn() + slideInVertically(),
                exit = fadeOut()
            ) {
                Button(
                    onClick = { viewModel.processIntent(OnboardingIntent.SaveDepartment) },
                    modifier = Modifier.fillMaxWidth().height(56.dp),
                    shape = CutCornerShape(topEnd = 20.dp, bottomStart = 20.dp),
                    enabled = state.selectedDepartment != null
                ) {
                    Text("EXECUTE DIRECTIVE")
                }
            }
        }
    }
}

@Composable
fun DepartmentItem(departmentName: String, isSelected: Boolean, onClick: () -> Unit) {
    val buttonColors = if (isSelected) {
        ButtonDefaults.buttonColors(
            containerColor = MaterialTheme.colorScheme.primary,
            contentColor = MaterialTheme.colorScheme.onPrimary
        )
    } else {
        ButtonDefaults.buttonColors(
            containerColor = MaterialTheme.colorScheme.surfaceVariant,
            contentColor = MaterialTheme.colorScheme.onSurfaceVariant
        )
    }

    Button(
        onClick = onClick,
        modifier = Modifier.fillMaxWidth().height(60.dp),
        shape = CutCornerShape(8.dp),
        colors = buttonColors
    ) {
        Text(text = ">> [ $departmentName ]", style = MaterialTheme.typography.titleLarge)
    }
}

@Composable
fun OnboardingCursor(color: androidx.compose.ui.graphics.Color) {
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