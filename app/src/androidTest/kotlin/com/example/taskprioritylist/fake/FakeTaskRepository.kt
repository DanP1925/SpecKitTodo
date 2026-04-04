package com.example.taskprioritylist.fake

import com.example.taskprioritylist.domain.model.Task
import com.example.taskprioritylist.domain.repository.TaskRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.flow
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class FakeTaskRepository
    @Inject
    constructor() : TaskRepository {
        private val taskFlow = MutableStateFlow<List<Task>>(emptyList())
        private var errorToThrow: Throwable? = null

        fun emit(tasks: List<Task>) {
            errorToThrow = null
            taskFlow.value = tasks
        }

        fun setError(throwable: Throwable) {
            errorToThrow = throwable
        }

        override fun getTasks(): Flow<List<Task>> {
            val error = errorToThrow
            return if (error != null) {
                flow { throw error }
            } else {
                taskFlow.asStateFlow()
            }
        }
    }
