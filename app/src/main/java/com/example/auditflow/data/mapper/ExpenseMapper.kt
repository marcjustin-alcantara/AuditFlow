package com.example.auditflow.data.mapper

import com.example.auditflow.data.remote.dto.ExpenseItemDto
import com.example.auditflow.domain.model.ExpenseItem

fun ExpenseItem.toDto(): ExpenseItemDto {
    return ExpenseItemDto(
        expenseType = this.expenseType,
        amount = this.amount,
        timestamp = this.timestamp
    )
}

fun ExpenseItemDto.toDomain(): ExpenseItem {
    return ExpenseItem(
        expenseType = this.expenseType,
        amount = this.amount,
        timestamp = this.timestamp
    )
}