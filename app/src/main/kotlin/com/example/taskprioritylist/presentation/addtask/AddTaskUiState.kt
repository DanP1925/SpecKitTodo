package com.example.taskprioritylist.presentation.addtask

data class AddTaskUiState(
    val title: String = "",
    val description: String = "",
    val isImportant: Boolean = false,
    val isUrgent: Boolean = false,
    val titleError: TitleValidationError? = null,
    val isDirty: Boolean = false,
    val isSaving: Boolean = false,
    val showDiscardDialog: Boolean = false,
    val hasSavedSuccessfully: Boolean = false,
)
