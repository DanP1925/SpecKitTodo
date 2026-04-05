package com.example.taskprioritylist.presentation.addtask

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.taskprioritylist.domain.usecase.AddTaskUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class AddTaskViewModel @Inject constructor(
    private val addTaskUseCase: AddTaskUseCase,
) : ViewModel() {
    private val _uiState = MutableStateFlow(AddTaskUiState())
    val uiState: StateFlow<AddTaskUiState> = _uiState.asStateFlow()

    private val _events = MutableSharedFlow<AddTaskEvent>()
    val events: SharedFlow<AddTaskEvent> = _events.asSharedFlow()

    fun onTitleChanged(title: String) {
        _uiState.update { it.copy(title = title, titleError = null, isDirty = true) }
    }

    fun onDescriptionChanged(description: String) {
        _uiState.update { it.copy(description = description, isDirty = true) }
    }

    fun onImportantToggled() {
        _uiState.update { it.copy(isImportant = !it.isImportant, isDirty = true) }
    }

    fun onUrgentToggled() {
        _uiState.update { it.copy(isUrgent = !it.isUrgent, isDirty = true) }
    }

    fun onSave() {
        val state = _uiState.value
        if (state.title.isBlank()) {
            _uiState.update { it.copy(titleError = "Title cannot be empty") }
            return
        }
        viewModelScope.launch {
            addTaskUseCase(
                title = state.title,
                description = state.description,
                isImportant = state.isImportant,
                isUrgent = state.isUrgent,
            )
            _events.emit(AddTaskEvent.NavigateBack)
        }
    }

    fun onDiscardConfirmed() {
        viewModelScope.launch {
            _events.emit(AddTaskEvent.NavigateBack)
        }
    }
}
