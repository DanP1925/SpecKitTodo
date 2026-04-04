package com.example.taskprioritylist.data.local

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "tasks")
data class TaskEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val title: String,
    val description: String?,
    @ColumnInfo(name = "is_important")
    val isImportant: Boolean,
    @ColumnInfo(name = "is_urgent")
    val isUrgent: Boolean,
    @ColumnInfo(name = "created_at")
    val createdAt: Long,
)
