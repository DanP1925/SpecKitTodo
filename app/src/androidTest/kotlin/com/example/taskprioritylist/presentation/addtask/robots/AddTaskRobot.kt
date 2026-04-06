package com.example.taskprioritylist.presentation.addtask.robots

import androidx.compose.ui.test.assertDoesNotExist
import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.assertTextContains
import androidx.compose.ui.test.junit4.ComposeContentTestRule
import androidx.compose.ui.test.onNodeWithTag
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import androidx.compose.ui.test.performTextInput
import com.example.taskprioritylist.presentation.addtask.AddTaskTestTags

class AddTaskRobot(private val rule: ComposeContentTestRule) {
    fun enterTitle(text: String) {
        rule.onNodeWithTag(AddTaskTestTags.TITLE_FIELD).performTextInput(text)
    }

    fun enterDescription(text: String) {
        rule.onNodeWithTag(AddTaskTestTags.DESCRIPTION_FIELD).performTextInput(text)
    }

    fun toggleImportant() {
        rule.onNodeWithTag(AddTaskTestTags.IMPORTANT_TOGGLE).performClick()
    }

    fun toggleUrgent() {
        rule.onNodeWithTag(AddTaskTestTags.URGENT_TOGGLE).performClick()
    }

    fun tapSave() {
        rule.onNodeWithTag(AddTaskTestTags.SAVE_BUTTON).performClick()
    }

    fun tapCancel() {
        rule.onNodeWithTag(AddTaskTestTags.CANCEL_BUTTON).performClick()
    }

    fun tapDiscardInDialog() {
        rule.onNodeWithTag(AddTaskTestTags.DISCARD_CONFIRM).performClick()
    }

    fun tapKeepEditingInDialog() {
        rule.onNodeWithTag(AddTaskTestTags.KEEP_EDITING_BUTTON).performClick()
    }

    fun assertTitleError() {
        rule.onNodeWithTag(AddTaskTestTags.TITLE_ERROR, useUnmergedTree = true).assertIsDisplayed()
    }

    fun assertDiscardDialogVisible() {
        rule.onNodeWithTag(AddTaskTestTags.DISCARD_DIALOG).assertIsDisplayed()
    }

    fun assertFormIntact(title: String) {
        rule.onNodeWithTag(AddTaskTestTags.TITLE_FIELD).assertTextContains(title)
    }

    fun assertNotVisible() {
        rule.onNodeWithTag(AddTaskTestTags.TITLE_FIELD).assertDoesNotExist()
    }

    fun assertTitleFieldVisible() {
        rule.onNodeWithTag(AddTaskTestTags.TITLE_FIELD).assertIsDisplayed()
    }

    fun assertTitleErrorText(message: String) {
        rule.onNodeWithText(message).assertIsDisplayed()
    }
}
