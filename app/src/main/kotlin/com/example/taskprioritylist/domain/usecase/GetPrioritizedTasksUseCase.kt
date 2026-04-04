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

    private fun priorityTier(task: Task): Int =
        when {
            task.isImportant && task.isUrgent -> TIER_IMPORTANT_AND_URGENT
            task.isImportant -> TIER_IMPORTANT_ONLY
            task.isUrgent -> TIER_URGENT_ONLY
            else -> TIER_NONE
        }

    companion object {
        private const val TIER_IMPORTANT_AND_URGENT = 0
        private const val TIER_IMPORTANT_ONLY = 1
        private const val TIER_URGENT_ONLY = 2
        private const val TIER_NONE = 3
    }
}
