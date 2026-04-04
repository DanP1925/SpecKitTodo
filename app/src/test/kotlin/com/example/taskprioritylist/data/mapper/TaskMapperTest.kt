package com.example.taskprioritylist.data.mapper

import com.example.taskprioritylist.data.local.TaskEntity
import com.example.taskprioritylist.domain.model.Task
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertFalse
import org.junit.jupiter.api.Assertions.assertNull
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.Test

class TaskMapperTest {
    @Test
    fun `GIVEN a TaskEntity with all fields WHEN mapped to domain THEN all fields are preserved`() {
        val entity =
            TaskEntity(
                id = 1L,
                title = "Buy groceries",
                description = "Milk, eggs, bread",
                isImportant = true,
                isUrgent = false,
                createdAt = 1000L,
            )

        val task = entity.toDomain()

        assertEquals(1L, task.id)
        assertEquals("Buy groceries", task.title)
        assertEquals("Milk, eggs, bread", task.description)
        assertTrue(task.isImportant)
        assertFalse(task.isUrgent)
        assertEquals(1000L, task.createdAt)
    }

    @Test
    fun `GIVEN a TaskEntity with null description WHEN mapped to domain THEN description is null`() {
        val entity =
            TaskEntity(
                id = 2L,
                title = "Task without description",
                description = null,
                isImportant = false,
                isUrgent = true,
                createdAt = 2000L,
            )

        val task = entity.toDomain()

        assertNull(task.description)
    }

    @Test
    fun `GIVEN a Task with all fields WHEN mapped to entity THEN all fields are preserved`() {
        val task =
            Task(
                id = 3L,
                title = "Finish report",
                description = "Q4 summary",
                isImportant = true,
                isUrgent = true,
                createdAt = 3000L,
            )

        val entity = task.toEntity()

        assertEquals(3L, entity.id)
        assertEquals("Finish report", entity.title)
        assertEquals("Q4 summary", entity.description)
        assertTrue(entity.isImportant)
        assertTrue(entity.isUrgent)
        assertEquals(3000L, entity.createdAt)
    }

    @Test
    fun `GIVEN a Task WHEN mapped to entity and back to domain THEN original task is preserved`() {
        val original =
            Task(
                id = 4L,
                title = "Round-trip task",
                description = null,
                isImportant = false,
                isUrgent = false,
                createdAt = 4000L,
            )

        val result = original.toEntity().toDomain()

        assertEquals(original, result)
    }
}
