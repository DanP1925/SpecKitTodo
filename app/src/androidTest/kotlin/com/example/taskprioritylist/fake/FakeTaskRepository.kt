package com.example.taskprioritylist.fake

import com.example.taskprioritylist.domain.model.Task
import com.example.taskprioritylist.domain.repository.TaskRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class FakeTaskRepository
    @Inject
    constructor() : TaskRepository {
        private val taskFlow = MutableStateFlow<List<Task>>(emptyList())

        fun emit(tasks: List<Task>) {
            taskFlow.value = tasks
        }

        override fun getTasks(): Flow<List<Task>> = taskFlow.asStateFlow()
    }
