package com.example.taskprioritylist.domain.usecase

import com.example.taskprioritylist.domain.model.Task
import com.example.taskprioritylist.domain.repository.TaskRepository
import javax.inject.Inject

class AddTaskUseCase @Inject constructor(
    private val repository: TaskRepository,
) {
    suspend operator fun invoke(
        title: String,
        description: String?,
        isImportant: Boolean,
        isUrgent: Boolean,
    ) {
        repository.addTask(
            Task(
                id = 0,
                title = title.trim(),
                description = description?.trim()?.ifBlank { null },
                isImportant = isImportant,
                isUrgent = isUrgent,
                createdAt = System.currentTimeMillis(),
            ),
        )
    }
}
