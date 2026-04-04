package com.example.taskprioritylist.presentation.tasklist

import com.example.taskprioritylist.domain.model.Task

sealed class TaskListUiState {
    data object Loading : TaskListUiState()

    data object Empty : TaskListUiState()

    data class Success(val tasks: List<Task>) : TaskListUiState()

    data class Error(val message: String) : TaskListUiState()
}
