package com.example.taskprioritylist.presentation.addtask

sealed interface AddTaskEvent {
    data object NavigateBack : AddTaskEvent
}
