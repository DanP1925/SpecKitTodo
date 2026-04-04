package com.example.taskprioritylist.data.mapper

import com.example.taskprioritylist.data.local.TaskEntity
import com.example.taskprioritylist.domain.model.Task

fun TaskEntity.toDomain(): Task =
    Task(
        id = id,
        title = title,
        description = description,
        isImportant = isImportant,
        isUrgent = isUrgent,
        createdAt = createdAt,
    )

fun Task.toEntity(): TaskEntity =
    TaskEntity(
        id = id,
        title = title,
        description = description,
        isImportant = isImportant,
        isUrgent = isUrgent,
        createdAt = createdAt,
    )
