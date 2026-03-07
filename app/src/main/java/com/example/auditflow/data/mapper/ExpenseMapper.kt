package com.example.auditflow.data.mapper

import com.example.auditflow.data.remote.dto.ExpenseItemDto
import com.example.auditflow.domain.model.ExpenseItem
import java.util.Date

fun ExpenseItem.toDto(): ExpenseItemDto {
    return ExpenseItemDto(
        expenseType = this.expenseType,
        amount = this.amount,
        timestamp = Date(this.timestamp) // Converts Long into a Date object
    )
}

fun ExpenseItemDto.toDomain(): ExpenseItem {
    return ExpenseItem(
        expenseType = this.expenseType,
        amount = this.amount,
        timestamp = this.timestamp?.time ?: 0L // Safely converts Date back to Long
    )
}