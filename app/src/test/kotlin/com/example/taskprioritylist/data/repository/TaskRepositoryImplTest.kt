package com.example.taskprioritylist.data.repository

import com.example.taskprioritylist.data.local.TaskDao
import com.example.taskprioritylist.data.local.TaskEntity
import com.example.taskprioritylist.domain.model.Task
import io.mockk.coJustRun
import io.mockk.coVerify
import io.mockk.every
import io.mockk.mockk
import io.mockk.slot
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.test.runTest
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertNull
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

    @Test
    fun `WHEN addTask THEN dao insertTask is called exactly once`() =
        runTest {
            coJustRun { taskDao.insertTask(any()) }
            val task = Task(id = 0, title = "Buy milk", description = null, isImportant = false, isUrgent = false, createdAt = 1L)

            repository.addTask(task)

            coVerify(exactly = 1) { taskDao.insertTask(any()) }
        }

    @Test
    fun `WHEN addTask THEN entity fields are mapped from domain task`() =
        runTest {
            val slot = slot<TaskEntity>()
            coJustRun { taskDao.insertTask(capture(slot)) }
            val task = Task(id = 0, title = "Buy milk", description = "Some notes", isImportant = true, isUrgent = false, createdAt = 123L)

            repository.addTask(task)

            assertEquals("Buy milk", slot.captured.title)
            assertEquals("Some notes", slot.captured.description)
            assertEquals(true, slot.captured.isImportant)
            assertEquals(false, slot.captured.isUrgent)
            assertEquals(123L, slot.captured.createdAt)
        }

    @Test
    fun `GIVEN task with null description WHEN addTask THEN entity description is null`() =
        runTest {
            val slot = slot<TaskEntity>()
            coJustRun { taskDao.insertTask(capture(slot)) }
            val task = Task(id = 0, title = "Buy milk", description = null, isImportant = false, isUrgent = false, createdAt = 1L)

            repository.addTask(task)

            assertNull(slot.captured.description)
        }
}
