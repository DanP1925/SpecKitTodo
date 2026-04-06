package com.example.taskprioritylist.presentation.tasklist

import app.cash.turbine.test
import com.example.taskprioritylist.domain.model.Task
import com.example.taskprioritylist.domain.usecase.GetPrioritizedTasksUseCase
import com.example.taskprioritylist.utils.MainDispatcherExtension
import io.mockk.every
import io.mockk.mockk
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.test.runTest
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertInstanceOf
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.RegisterExtension

class TaskListViewModelTest {
    companion object {
        @JvmField
        @RegisterExtension
        val mainDispatcherExtension = MainDispatcherExtension()
    }

    private val useCase: GetPrioritizedTasksUseCase = mockk()

    @Test
    fun `GIVEN use case returns no tasks WHEN viewModel is created THEN uiState is Empty`() =
        runTest {
            every { useCase() } returns flowOf(emptyList())

            val sut = TaskListViewModel(useCase)

            assertEquals(TaskListUiState.Empty, sut.uiState.value)
        }

    @Test
    fun `GIVEN use case returns tasks WHEN viewModel is created THEN uiState is Success`() =
        runTest {
            val tasks =
                listOf(
                    aTask(id = 1L, title = "Alpha"),
                    aTask(id = 2L, title = "Beta", isImportant = true),
                )
            every { useCase() } returns flowOf(tasks)

            val sut = TaskListViewModel(useCase)

            assertInstanceOf(TaskListUiState.Success::class.java, sut.uiState.value)
            assertEquals(2, (sut.uiState.value as TaskListUiState.Success).tasks.size)
        }

    @Test
    fun `GIVEN use case returns tasks of mixed priority WHEN viewModel is created THEN Success tasks preserve use case order`() =
        runTest {
            val tasks =
                listOf(
                    aTask(id = 1L, title = "Both", isImportant = true, isUrgent = true),
                    aTask(id = 2L, title = "Neither"),
                )
            every { useCase() } returns flowOf(tasks)

            val sut = TaskListViewModel(useCase)

            val success = sut.uiState.value as TaskListUiState.Success
            assertEquals("Both", success.tasks.first().title)
        }

    @Test
    fun `GIVEN use case emits tasks then empty list WHEN collecting THEN uiState transitions from Success to Empty`() =
        runTest {
            val taskSource = MutableStateFlow(listOf(aTask(id = 1L, title = "Alpha")))
            every { useCase() } returns taskSource

            val sut = TaskListViewModel(useCase)

            sut.uiState.test {
                assertEquals(TaskListUiState.Success(listOf(aTask(id = 1L, title = "Alpha"))), awaitItem())
                taskSource.value = emptyList()
                assertEquals(TaskListUiState.Empty, awaitItem())
                cancelAndIgnoreRemainingEvents()
            }
        }

    @Test
    fun `GIVEN use case throws an exception WHEN viewModel is created THEN uiState is Error`() =
        runTest {
            every { useCase() } returns flow { throw RuntimeException("DB error") }

            val sut = TaskListViewModel(useCase)

            assertInstanceOf(TaskListUiState.Error::class.java, sut.uiState.value)
            assertEquals("DB error", (sut.uiState.value as TaskListUiState.Error).message)
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
