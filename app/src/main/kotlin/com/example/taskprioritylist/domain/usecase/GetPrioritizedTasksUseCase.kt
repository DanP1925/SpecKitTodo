package com.example.taskprioritylist.domain.usecase

import com.example.taskprioritylist.domain.model.Task
import com.example.taskprioritylist.domain.repository.TaskRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject

class GetPrioritizedTasksUseCase @Inject constructor(
    private val repository: TaskRepository,
) {
    operator fun invoke(): Flow<List<Task>> =
        repository.getTasks().map { tasks ->
            tasks.sortedWith(
                compareBy(
                    { priorityTier(it) },
                    { it.title.lowercase() },
                ),
            )
        }

    private fun priorityTier(task: Task): PriorityLevel =
        when {
            task.isImportant && task.isUrgent -> PriorityLevel.IMPORTANT_AND_URGENT
            task.isImportant -> PriorityLevel.IMPORTANT_ONLY
            task.isUrgent -> PriorityLevel.URGENT_ONLY
            else -> PriorityLevel.NONE
        }

    private enum class PriorityLevel {
        IMPORTANT_AND_URGENT,
        IMPORTANT_ONLY,
        URGENT_ONLY,
        NONE,
    }
}
