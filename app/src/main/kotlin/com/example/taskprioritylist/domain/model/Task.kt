package com.example.taskprioritylist.domain.model

data class Task(
    val id: Long,
    val title: String,
    val description: String?,
    val isImportant: Boolean,
    val isUrgent: Boolean,
    val createdAt: Long,
)
