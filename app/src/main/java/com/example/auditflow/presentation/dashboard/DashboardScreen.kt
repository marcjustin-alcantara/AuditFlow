package com.example.auditflow.presentation.dashboard

import android.widget.Toast
import androidx.compose.animation.core.*
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CutCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import com.example.auditflow.presentation.theme.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DashboardScreen(
    viewModel: DashboardViewModel,
    onNavigateToAuth: () -> Unit,
    onNavigateToOnboarding: () -> Unit
) {
    val state by viewModel.state.collectAsState()
    val context = LocalContext.current

    LaunchedEffect(Unit) {
        viewModel.processIntent(DashboardIntent.LoadProfile)
    }

    LaunchedEffect(state.isLoggedOut) {
        if (state.isLoggedOut) {
            onNavigateToAuth()
        }
    }

    LaunchedEffect(state.needsOnboarding) {
        if (state.needsOnboarding) {
            onNavigateToOnboarding()
        }
    }

    LaunchedEffect(state.error, state.successMessage) {
        state.error?.let {
            Toast.makeText(context, "SYS_ERR: $it", Toast.LENGTH_SHORT).show()
            viewModel.processIntent(DashboardIntent.ClearMessages)
        }
        state.successMessage?.let {
            Toast.makeText(context, "SYS_MSG: $it", Toast.LENGTH_SHORT).show()
            viewModel.processIntent(DashboardIntent.ClearMessages)
        }
    }

    val departmentColor = when (state.department) {
        "Sales" -> SalesGreen
        "IT" -> ItBlue
        "HR" -> HrPurple
        else -> DefaultPrimary
    }

    AuditFlowTheme(dynamicPrimaryColor = departmentColor) {
        Surface(modifier = Modifier.fillMaxSize(), color = MaterialTheme.colorScheme.background) {
            if (state.isLoading && state.department == null) {
                Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    CircularProgressIndicator(color = departmentColor)
                }
            } else {
                Column(
                    modifier = Modifier.fillMaxSize().padding(24.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Text(
                            text = "${state.department?.uppercase() ?: "UNKNOWN"}_PORTAL",
                            style = MaterialTheme.typography.headlineMedium,
                            color = departmentColor
                        )
                        DashboardCursor(color = departmentColor)
                    }
                    Spacer(modifier = Modifier.height(24.dp))

                    Card(
                        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                        border = BorderStroke(2.dp, departmentColor), // Cyberpunk Neon Glow Border
                        shape = CutCornerShape(topStart = 16.dp, bottomEnd = 16.dp),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Column(modifier = Modifier.padding(20.dp)) {
                            Text(">> DATA_ENTRY // EXPENSE", style = MaterialTheme.typography.titleLarge, color = departmentColor)
                            Spacer(modifier = Modifier.height(16.dp))

                            var expanded by remember { mutableStateOf(false) }
                            ExposedDropdownMenuBox(
                                expanded = expanded,
                                onExpandedChange = { expanded = !expanded }
                            ) {
                                OutlinedTextField(
                                    value = state.selectedExpenseType,
                                    onValueChange = {},
                                    readOnly = true,
                                    label = { Text(">> req_category:") },
                                    trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = expanded) },
                                    modifier = Modifier.menuAnchor().fillMaxWidth(),
                                    shape = CutCornerShape(8.dp)
                                )
                                ExposedDropdownMenu(
                                    expanded = expanded,
                                    onDismissRequest = { expanded = false }
                                ) {
                                    state.availableExpenseTypes.forEach { type ->
                                        DropdownMenuItem(
                                            text = { Text(">> $type") },
                                            onClick = {
                                                viewModel.processIntent(DashboardIntent.SelectExpenseType(type))
                                                expanded = false
                                            }
                                        )
                                    }
                                }
                            }

                            Spacer(modifier = Modifier.height(16.dp))

                            OutlinedTextField(
                                value = state.expenseAmount,
                                onValueChange = { viewModel.processIntent(DashboardIntent.UpdateAmount(it)) },
                                label = { Text(">> req_amount_usd:") },
                                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
                                modifier = Modifier.fillMaxWidth(),
                                shape = CutCornerShape(8.dp)
                            )

                            Spacer(modifier = Modifier.height(24.dp))

                            Button(
                                onClick = { viewModel.processIntent(DashboardIntent.SubmitExpense) },
                                modifier = Modifier.fillMaxWidth().height(50.dp),
                                shape = CutCornerShape(topStart = 12.dp, bottomEnd = 12.dp),
                                enabled = !state.isLoading
                            ) {
                                if (state.isLoading) {
                                    CircularProgressIndicator(color = Color.Black, modifier = Modifier.size(24.dp))
                                } else {
                                    Text("// INJECT_DATA")
                                }
                            }
                        }
                    }

                    Spacer(modifier = Modifier.weight(1f))

                    OutlinedButton(
                        onClick = { viewModel.processIntent(DashboardIntent.Logout) },
                        modifier = Modifier.fillMaxWidth().height(50.dp),
                        shape = CutCornerShape(8.dp),
                        border = BorderStroke(1.dp, departmentColor)
                    ) {
                        Text("TERMINATE SESSION", color = departmentColor)
                    }
                }
            }
        }
    }
}

@Composable
fun DashboardCursor(color: androidx.compose.ui.graphics.Color) {
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