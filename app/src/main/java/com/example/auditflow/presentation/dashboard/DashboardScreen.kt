package com.example.auditflow.presentation.dashboard

import android.widget.Toast
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
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
            Toast.makeText(context, it, Toast.LENGTH_SHORT).show()
            viewModel.processIntent(DashboardIntent.ClearMessages)
        }
        state.successMessage?.let {
            Toast.makeText(context, it, Toast.LENGTH_SHORT).show()
            viewModel.processIntent(DashboardIntent.ClearMessages)
        }
    }

    // Dynamic Theming Based on Department
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
                    Text(
                        text = "${state.department?.uppercase() ?: ""} PORTAL",
                        style = MaterialTheme.typography.headlineMedium,
                        color = departmentColor
                    )
                    Spacer(modifier = Modifier.height(24.dp))

                    Card(
                        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Column(modifier = Modifier.padding(16.dp)) {
                            Text("Submit New Expense", style = MaterialTheme.typography.titleLarge)
                            Spacer(modifier = Modifier.height(16.dp))

                            // Expense Type Dropdown
                            var expanded by remember { mutableStateOf(false) }
                            ExposedDropdownMenuBox(
                                expanded = expanded,
                                onExpandedChange = { expanded = !expanded }
                            ) {
                                OutlinedTextField(
                                    value = state.selectedExpenseType,
                                    onValueChange = {},
                                    readOnly = true,
                                    label = { Text("Expense Category") },
                                    trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = expanded) },
                                    modifier = Modifier.menuAnchor().fillMaxWidth()
                                )
                                ExposedDropdownMenu(
                                    expanded = expanded,
                                    onDismissRequest = { expanded = false }
                                ) {
                                    state.availableExpenseTypes.forEach { type ->
                                        DropdownMenuItem(
                                            text = { Text(type) },
                                            onClick = {
                                                viewModel.processIntent(DashboardIntent.SelectExpenseType(type))
                                                expanded = false
                                            }
                                        )
                                    }
                                }
                            }

                            Spacer(modifier = Modifier.height(16.dp))

                            // Amount Input
                            OutlinedTextField(
                                value = state.expenseAmount,
                                onValueChange = { viewModel.processIntent(DashboardIntent.UpdateAmount(it)) },
                                label = { Text("Amount (USD)") },
                                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
                                modifier = Modifier.fillMaxWidth()
                            )

                            Spacer(modifier = Modifier.height(24.dp))

                            Button(
                                onClick = { viewModel.processIntent(DashboardIntent.SubmitExpense) },
                                modifier = Modifier.fillMaxWidth().height(50.dp),
                                enabled = !state.isLoading
                            ) {
                                if (state.isLoading) {
                                    CircularProgressIndicator(color = Color.White, modifier = Modifier.size(24.dp))
                                } else {
                                    Text("Submit Expense Record")
                                }
                            }
                        }
                    }

                    Spacer(modifier = Modifier.weight(1f))

                    OutlinedButton(
                        onClick = { viewModel.processIntent(DashboardIntent.Logout) },
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Text("Log Out", color = departmentColor)
                    }
                }
            }
        }
    }
}