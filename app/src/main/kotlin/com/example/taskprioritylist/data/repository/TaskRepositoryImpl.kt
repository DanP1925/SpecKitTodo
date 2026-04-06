package com.example.taskprioritylist.data.repository

import com.example.taskprioritylist.data.local.TaskDao
import com.example.taskprioritylist.data.local.TaskEntity
import com.example.taskprioritylist.data.mapper.toDomain
import com.example.taskprioritylist.data.mapper.toEntity
import com.example.taskprioritylist.domain.model.Task
import com.example.taskprioritylist.domain.repository.TaskRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject

class TaskRepositoryImpl @Inject constructor(
    private val dao: TaskDao,
) : TaskRepository {
    override fun getTasks(): Flow<List<Task>> = dao.getAllTasks().map { it.map(TaskEntity::toDomain) }

    override suspend fun addTask(task: Task) {
        dao.insertTask(task.toEntity())
    }
}
