package com.example.taskprioritylist.presentation.addtask

data class AddTaskUiState(
    val title: String = "",
    val description: String = "",
    val isImportant: Boolean = false,
    val isUrgent: Boolean = false,
    val titleError: String? = null,
    val isDirty: Boolean = false,
)
