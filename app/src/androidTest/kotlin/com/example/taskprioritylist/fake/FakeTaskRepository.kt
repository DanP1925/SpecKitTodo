package com.example.taskprioritylist.fake

import com.example.taskprioritylist.domain.model.Task
import com.example.taskprioritylist.domain.repository.TaskRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.map
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class FakeTaskRepository
    @Inject
    constructor() : TaskRepository {
        private val resultFlow = MutableStateFlow<Result<List<Task>>>(Result.success(emptyList()))

        fun emit(tasks: List<Task>) {
            resultFlow.value = Result.success(tasks)
        }

        fun setError(throwable: Throwable) {
            resultFlow.value = Result.failure(throwable)
        }

        override fun getTasks(): Flow<List<Task>> = resultFlow.map { it.getOrThrow() }

        override suspend fun addTask(task: Task) {
            val current = resultFlow.value.getOrElse { emptyList() }
            val newId = (current.maxOfOrNull { it.id } ?: 0L) + 1L
            resultFlow.value = Result.success(current + task.copy(id = newId))
        }
    }
