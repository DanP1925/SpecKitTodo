package com.example.taskprioritylist.presentation.tasklist.robots

import androidx.compose.ui.test.SemanticsMatcher
import androidx.compose.ui.test.assert
import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.junit4.ComposeContentTestRule
import androidx.compose.ui.test.onNodeWithTag
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import androidx.compose.ui.test.performScrollToIndex
import com.example.taskprioritylist.presentation.tasklist.PriorityTierKey
import com.example.taskprioritylist.presentation.tasklist.TaskListTestTags

class TaskListRobot(private val rule: ComposeContentTestRule) {
    fun assertTaskListIsDisplayed() {
        rule.onNodeWithTag(TaskListTestTags.TASK_LIST).assertIsDisplayed()
    }

    fun assertEmptyStateIsDisplayed() {
        rule.onNodeWithTag(TaskListTestTags.EMPTY_STATE).assertIsDisplayed()
    }

    fun assertTaskIsDisplayed(title: String) {
        rule.onNodeWithTag(TaskListTestTags.taskItem(title)).assertIsDisplayed()
    }

    fun assertTaskHasPriorityTier(
        title: String,
        tier: String,
    ) {
        rule.onNodeWithTag(TaskListTestTags.taskItem(title))
            .assert(SemanticsMatcher.expectValue(PriorityTierKey, tier))
    }

    fun assertTaskAppearsBeforeTask(
        firstTitle: String,
        secondTitle: String,
    ) {
        rule.onNodeWithTag(TaskListTestTags.taskItem(firstTitle)).assertIsDisplayed()
        rule.onNodeWithTag(TaskListTestTags.taskItem(secondTitle)).assertIsDisplayed()
        val firstY =
            rule.onNodeWithTag(TaskListTestTags.taskItem(firstTitle))
                .fetchSemanticsNode().positionInRoot.y
        val secondY =
            rule.onNodeWithTag(TaskListTestTags.taskItem(secondTitle))
                .fetchSemanticsNode().positionInRoot.y
        assert(firstY < secondY) {
            "Expected '$firstTitle' (y=$firstY) to appear above '$secondTitle' (y=$secondY)"
        }
    }

    fun assertErrorMessageIsDisplayed(message: String) {
        rule.onNodeWithText(message).assertIsDisplayed()
    }

    fun assertFabVisible() {
        rule.onNodeWithTag(TaskListTestTags.FAB).assertIsDisplayed()
    }

    fun tapFab() {
        rule.onNodeWithTag(TaskListTestTags.FAB).performClick()
    }

    fun scrollToIndex(index: Int) {
        rule.onNodeWithTag(TaskListTestTags.TASK_LIST).performScrollToIndex(index)
    }

    fun assertTaskDoesNotExist(title: String) {
        rule.onNodeWithTag(TaskListTestTags.taskItem(title)).assertDoesNotExist()
    }
}
