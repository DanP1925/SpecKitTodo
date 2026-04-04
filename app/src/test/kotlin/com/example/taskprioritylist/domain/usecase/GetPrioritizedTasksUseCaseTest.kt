package com.example.taskprioritylist.domain.usecase

import com.example.taskprioritylist.domain.model.Task
import com.example.taskprioritylist.domain.repository.TaskRepository
import io.mockk.every
import io.mockk.mockk
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.test.runTest
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test

class GetPrioritizedTasksUseCaseTest {
    private val repository: TaskRepository = mockk()
    private lateinit var useCase: GetPrioritizedTasksUseCase

    @BeforeEach
    fun setUp() {
        useCase = GetPrioritizedTasksUseCase(repository)
    }

    @Test
    fun `GIVEN tasks of all priority tiers WHEN invoked THEN returns tasks sorted by tier`() =
        runTest {
            val tasks =
                listOf(
                    aTask(id = 1, title = "Neither", isImportant = false, isUrgent = false),
                    aTask(id = 2, title = "Important+Urgent", isImportant = true, isUrgent = true),
                    aTask(id = 3, title = "Urgent only", isImportant = false, isUrgent = true),
                    aTask(id = 4, title = "Important only", isImportant = true, isUrgent = false),
                )
            every { repository.getTasks() } returns flowOf(tasks)

            val result = useCase().first()

            assertEquals("Important+Urgent", result[0].title)
            assertEquals("Important only", result[1].title)
            assertEquals("Urgent only", result[2].title)
            assertEquals("Neither", result[3].title)
        }

    @Test
    fun `GIVEN tasks in the same tier WHEN invoked THEN returns tasks sorted alphabetically by title`() =
        runTest {
            val tasks =
                listOf(
                    aTask(id = 1, title = "Zebra", isImportant = true, isUrgent = true),
                    aTask(id = 2, title = "Apple", isImportant = true, isUrgent = true),
                    aTask(id = 3, title = "Mango", isImportant = true, isUrgent = true),
                )
            every { repository.getTasks() } returns flowOf(tasks)

            val result = useCase().first()

            assertEquals("Apple", result[0].title)
            assertEquals("Mango", result[1].title)
            assertEquals("Zebra", result[2].title)
        }

    @Test
    fun `GIVEN tasks with mixed-case titles WHEN invoked THEN sorts case-insensitively`() =
        runTest {
            val tasks =
                listOf(
                    aTask(id = 1, title = "zebra", isImportant = false, isUrgent = false),
                    aTask(id = 2, title = "Apple", isImportant = false, isUrgent = false),
                )
            every { repository.getTasks() } returns flowOf(tasks)

            val result = useCase().first()

            assertEquals("Apple", result[0].title)
            assertEquals("zebra", result[1].title)
        }

    @Test
    fun `GIVEN empty repository WHEN invoked THEN returns empty list`() =
        runTest {
            every { repository.getTasks() } returns flowOf(emptyList())

            val result = useCase().first()

            assertTrue(result.isEmpty())
        }

    private fun aTask(
        id: Long,
        title: String,
        isImportant: Boolean = false,
        isUrgent: Boolean = false,
    ) = Task(
        id = id,
        title = title,
        description = null,
        isImportant = isImportant,
        isUrgent = isUrgent,
        createdAt = id,
    )
}
