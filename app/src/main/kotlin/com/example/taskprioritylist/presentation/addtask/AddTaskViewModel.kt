package com.example.taskprioritylist.presentation.addtask

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.taskprioritylist.domain.usecase.AddTaskUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class AddTaskViewModel @Inject constructor(
    private val addTaskUseCase: AddTaskUseCase,
) : ViewModel() {
    companion object {
        const val MAX_DESCRIPTION_LENGTH = 140
    }

    private val _uiState = MutableStateFlow(AddTaskUiState())
    val uiState: StateFlow<AddTaskUiState> = _uiState.asStateFlow()

    fun onTitleChanged(title: String) {
        _uiState.update { it.copy(title = title, titleError = null, isDirty = true) }
    }

    fun onDescriptionChanged(description: String) {
        _uiState.update { it.copy(description = description.take(MAX_DESCRIPTION_LENGTH), isDirty = true) }
    }

    fun onImportantToggled() {
        _uiState.update { it.copy(isImportant = !it.isImportant, isDirty = true) }
    }

    fun onUrgentToggled() {
        _uiState.update { it.copy(isUrgent = !it.isUrgent, isDirty = true) }
    }

    fun onSave() {
        val state = _uiState.value
        if (state.isSaving) return
        if (state.title.isBlank()) {
            _uiState.update { it.copy(titleError = TitleValidationError.BLANK) }
            return
        }
        _uiState.update { it.copy(isSaving = true) }
        viewModelScope.launch {
            runCatching {
                addTaskUseCase(
                    title = state.title,
                    description = state.description,
                    isImportant = state.isImportant,
                    isUrgent = state.isUrgent,
                )
            }.onSuccess {
                _uiState.update { it.copy(isSaving = false, shouldNavigateBack = true) }
            }.onFailure {
                _uiState.update { it.copy(isSaving = false) }
            }
        }
    }

    fun onDiscardRequested() {
        _uiState.update { it.copy(showDiscardDialog = true) }
    }

    fun onDiscardDismissed() {
        _uiState.update { it.copy(showDiscardDialog = false) }
    }

    fun onDiscardConfirmed() {
        _uiState.update { it.copy(isDirty = false, showDiscardDialog = false, shouldNavigateBack = true) }
    }
}
