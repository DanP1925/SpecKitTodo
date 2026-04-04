package com.example.taskprioritylist.presentation.tasklist

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.taskprioritylist.domain.usecase.GetPrioritizedTasksUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class TaskListViewModel @Inject constructor(
    private val getPrioritizedTasks: GetPrioritizedTasksUseCase,
) : ViewModel() {
    private val _uiState = MutableStateFlow<TaskListUiState>(TaskListUiState.Loading)
    val uiState: StateFlow<TaskListUiState> = _uiState.asStateFlow()

    init {
        loadTasks()
    }

    private fun loadTasks() {
        viewModelScope.launch {
            try {
                getPrioritizedTasks().collect { tasks ->
                    _uiState.value =
                        if (tasks.isEmpty()) {
                            TaskListUiState.Empty
                        } else {
                            TaskListUiState.Success(tasks)
                        }
                }
            } catch (e: Exception) {
                _uiState.value = TaskListUiState.Error(e.message ?: "An unexpected error occurred")
            }
        }
    }
}
