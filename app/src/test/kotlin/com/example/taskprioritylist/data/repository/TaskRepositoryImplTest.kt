package com.example.taskprioritylist.data.repository

import com.example.taskprioritylist.data.local.TaskDao
import com.example.taskprioritylist.data.local.TaskEntity
import io.mockk.every
import io.mockk.mockk
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.test.runTest
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test

class TaskRepositoryImplTest {
    private val taskDao: TaskDao = mockk()
    private lateinit var repository: TaskRepositoryImpl

    @BeforeEach
    fun setUp() {
        repository = TaskRepositoryImpl(taskDao)
    }

    @Test
    fun `GIVEN DAO emits entities WHEN getTasks is called THEN returns mapped domain tasks`() =
        runTest {
            val entities =
                listOf(
                    TaskEntity(
                        id = 1L,
                        title = "Task A",
                        description = null,
                        isImportant = true,
                        isUrgent = false,
                        createdAt = 1L,
                    ),
                    TaskEntity(
                        id = 2L,
                        title = "Task B",
                        description = "A description",
                        isImportant = false,
                        isUrgent = true,
                        createdAt = 2L,
                    ),
                )
            every { taskDao.getAllTasks() } returns flowOf(entities)

            val tasks = repository.getTasks().first()

            assertEquals(2, tasks.size)
            assertEquals("Task A", tasks[0].title)
            assertTrue(tasks[0].isImportant)
            assertEquals("Task B", tasks[1].title)
            assertEquals("A description", tasks[1].description)
            assertTrue(tasks[1].isUrgent)
        }

    @Test
    fun `GIVEN DAO has no entries WHEN getTasks is called THEN returns empty list`() =
        runTest {
            every { taskDao.getAllTasks() } returns flowOf(emptyList())

            val tasks = repository.getTasks().first()

            assertTrue(tasks.isEmpty())
        }
}
