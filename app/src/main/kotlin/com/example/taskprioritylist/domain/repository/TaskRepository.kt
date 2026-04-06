package com.example.taskprioritylist.domain.repository

import com.example.taskprioritylist.domain.model.Task
import kotlinx.coroutines.flow.Flow

interface TaskRepository {
    fun getTasks(): Flow<List<Task>>

    suspend fun addTask(task: Task)
}
