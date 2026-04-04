package com.example.taskprioritylist.presentation.tasklist

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.taskprioritylist.domain.usecase.GetPrioritizedTasksUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import javax.inject.Inject

@HiltViewModel
class TaskListViewModel @Inject constructor(
    private val getPrioritizedTasks: GetPrioritizedTasksUseCase,
) : ViewModel() {
    val uiState: StateFlow<TaskListUiState> =
        getPrioritizedTasks()
            .map { tasks ->
                if (tasks.isEmpty()) TaskListUiState.Empty else TaskListUiState.Success(tasks)
            }
            .catch { e -> emit(TaskListUiState.Error(e.message ?: "An unexpected error occurred")) }
            .stateIn(
                scope = viewModelScope,
                started = SharingStarted.Eagerly,
                initialValue = TaskListUiState.Loading,
            )
}
