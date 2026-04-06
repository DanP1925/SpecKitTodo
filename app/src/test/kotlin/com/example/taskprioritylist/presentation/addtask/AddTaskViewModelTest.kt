package com.example.taskprioritylist.presentation.addtask

import com.example.taskprioritylist.domain.usecase.AddTaskUseCase
import com.example.taskprioritylist.presentation.addtask.AddTaskViewModel.Companion.MAX_DESCRIPTION_LENGTH
import com.example.taskprioritylist.utils.MainDispatcherExtension
import io.mockk.coJustRun
import io.mockk.coVerify
import io.mockk.mockk
import kotlinx.coroutines.test.runTest
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertFalse
import org.junit.jupiter.api.Assertions.assertNull
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.RegisterExtension

class AddTaskViewModelTest {
    companion object {
        @JvmField
        @RegisterExtension
        val mainDispatcherExtension = MainDispatcherExtension()
    }

    private val addTaskUseCase: AddTaskUseCase = mockk()

    @Test
    fun `WHEN onTitleChanged THEN title is updated and isDirty becomes true`() =
        runTest {
            val sut = AddTaskViewModel(addTaskUseCase)

            sut.onTitleChanged("Buy milk")

            assertEquals("Buy milk", sut.uiState.value.title)
            assertTrue(sut.uiState.value.isDirty)
        }

    @Test
    fun `GIVEN existing titleError WHEN onTitleChanged THEN titleError is cleared`() =
        runTest {
            val sut = AddTaskViewModel(addTaskUseCase)
            sut.onSave() // triggers blank-title error

            sut.onTitleChanged("Fixed title")

            assertNull(sut.uiState.value.titleError)
        }

    @Test
    fun `WHEN onDescriptionChanged THEN description is updated and isDirty becomes true`() =
        runTest {
            val sut = AddTaskViewModel(addTaskUseCase)

            sut.onDescriptionChanged("Some notes")

            assertEquals("Some notes", sut.uiState.value.description)
            assertTrue(sut.uiState.value.isDirty)
        }

    @Test
    fun `GIVEN description at exactly max length WHEN onDescriptionChanged THEN description is updated`() =
        runTest {
            val sut = AddTaskViewModel(addTaskUseCase)
            val description = "a".repeat(MAX_DESCRIPTION_LENGTH)

            sut.onDescriptionChanged(description)

            assertEquals(description, sut.uiState.value.description)
        }

    @Test
    fun `GIVEN description exceeds max length WHEN onDescriptionChanged THEN description is not updated`() =
        runTest {
            val sut = AddTaskViewModel(addTaskUseCase)
            sut.onDescriptionChanged("a".repeat(MAX_DESCRIPTION_LENGTH))

            sut.onDescriptionChanged("a".repeat(MAX_DESCRIPTION_LENGTH + 1))

            assertEquals("a".repeat(MAX_DESCRIPTION_LENGTH), sut.uiState.value.description)
        }

    @Test
    fun `WHEN onImportantToggled THEN isImportant flips to true and isDirty becomes true`() =
        runTest {
            val sut = AddTaskViewModel(addTaskUseCase)

            sut.onImportantToggled()

            assertTrue(sut.uiState.value.isImportant)
            assertTrue(sut.uiState.value.isDirty)
        }

    @Test
    fun `WHEN onImportantToggled twice THEN isImportant returns to false`() =
        runTest {
            val sut = AddTaskViewModel(addTaskUseCase)

            sut.onImportantToggled()
            sut.onImportantToggled()

            assertFalse(sut.uiState.value.isImportant)
        }

    @Test
    fun `WHEN onUrgentToggled THEN isUrgent flips to true and isDirty becomes true`() =
        runTest {
            val sut = AddTaskViewModel(addTaskUseCase)

            sut.onUrgentToggled()

            assertTrue(sut.uiState.value.isUrgent)
            assertTrue(sut.uiState.value.isDirty)
        }

    @Test
    fun `WHEN onUrgentToggled twice THEN isUrgent returns to false`() =
        runTest {
            val sut = AddTaskViewModel(addTaskUseCase)

            sut.onUrgentToggled()
            sut.onUrgentToggled()

            assertFalse(sut.uiState.value.isUrgent)
        }

    @Test
    fun `GIVEN blank title WHEN onSave THEN titleError is BLANK`() =
        runTest {
            val sut = AddTaskViewModel(addTaskUseCase)

            sut.onSave()

            assertEquals(TitleValidationError.BLANK, sut.uiState.value.titleError)
        }

    @Test
    fun `GIVEN whitespace-only title WHEN onSave THEN titleError is BLANK`() =
        runTest {
            val sut = AddTaskViewModel(addTaskUseCase)
            sut.onTitleChanged("   ")

            sut.onSave()

            assertEquals(TitleValidationError.BLANK, sut.uiState.value.titleError)
        }

    @Test
    fun `GIVEN blank title WHEN onSave THEN use case is never called`() =
        runTest {
            val sut = AddTaskViewModel(addTaskUseCase)

            sut.onSave()

            coVerify(exactly = 0) { addTaskUseCase(any(), any(), any(), any()) }
        }

    @Test
    fun `GIVEN valid title WHEN onSave THEN use case is called and hasSavedSuccessfully is true`() =
        runTest {
            coJustRun { addTaskUseCase(any(), any(), any(), any()) }
            val sut = AddTaskViewModel(addTaskUseCase)
            sut.onTitleChanged("Buy milk")

            sut.onSave()

            assertTrue(sut.uiState.value.hasSavedSuccessfully)
            coVerify(exactly = 1) { addTaskUseCase("Buy milk", any(), false, false) }
        }

    @Test
    fun `GIVEN valid title WHEN onSave THEN titleError stays null`() =
        runTest {
            coJustRun { addTaskUseCase(any(), any(), any(), any()) }
            val sut = AddTaskViewModel(addTaskUseCase)
            sut.onTitleChanged("Buy milk")

            sut.onSave()

            assertNull(sut.uiState.value.titleError)
        }

    @Test
    fun `WHEN onDiscardRequested THEN showDiscardDialog is true`() =
        runTest {
            val sut = AddTaskViewModel(addTaskUseCase)

            sut.onDiscardRequested()

            assertTrue(sut.uiState.value.showDiscardDialog)
        }

    @Test
    fun `GIVEN dialog shown WHEN onDiscardDismissed THEN showDiscardDialog is false`() =
        runTest {
            val sut = AddTaskViewModel(addTaskUseCase)
            sut.onDiscardRequested()

            sut.onDiscardDismissed()

            assertFalse(sut.uiState.value.showDiscardDialog)
        }

    @Test
    fun `WHEN onDiscardConfirmed THEN hasSavedSuccessfully is true and dialog is hidden`() =
        runTest {
            val sut = AddTaskViewModel(addTaskUseCase)
            sut.onDiscardRequested()

            sut.onDiscardConfirmed()

            assertTrue(sut.uiState.value.hasSavedSuccessfully)
            assertFalse(sut.uiState.value.showDiscardDialog)
            coVerify(exactly = 0) { addTaskUseCase(any(), any(), any(), any()) }
        }
}
