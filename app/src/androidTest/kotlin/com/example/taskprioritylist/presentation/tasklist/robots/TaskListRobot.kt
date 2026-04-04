package com.example.taskprioritylist.presentation.tasklist.robots

import androidx.compose.ui.test.SemanticsMatcher
import androidx.compose.ui.test.assert
import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.junit4.ComposeContentTestRule
import androidx.compose.ui.test.onNodeWithTag
import androidx.compose.ui.test.onNodeWithText
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
        val firstNode = rule.onNodeWithTag(TaskListTestTags.taskItem(firstTitle))
        val secondNode = rule.onNodeWithTag(TaskListTestTags.taskItem(secondTitle))
        firstNode.assertIsDisplayed()
        secondNode.assertIsDisplayed()
        rule.onAllNodes(
            androidx.compose.ui.test.hasTestTag(TaskListTestTags.taskItem(firstTitle))
                .or(androidx.compose.ui.test.hasTestTag(TaskListTestTags.taskItem(secondTitle))),
        )[0].assertIsDisplayed()
    }

    fun assertErrorMessageIsDisplayed(message: String) {
        rule.onNodeWithText(message).assertIsDisplayed()
    }
}
