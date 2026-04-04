package com.example.taskprioritylist.presentation.tasklist

import androidx.compose.ui.test.junit4.createAndroidComposeRule
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.example.taskprioritylist.MainActivity
import com.example.taskprioritylist.domain.model.Task
import com.example.taskprioritylist.fake.FakeTaskRepository
import com.example.taskprioritylist.presentation.tasklist.robots.TaskListRobot
import dagger.hilt.android.testing.HiltAndroidRule
import dagger.hilt.android.testing.HiltAndroidTest
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import javax.inject.Inject

@HiltAndroidTest
@RunWith(AndroidJUnit4::class)
class TaskListScreenTest {
    @get:Rule(order = 0)
    val hiltRule = HiltAndroidRule(this)

    @get:Rule(order = 1)
    val composeRule = createAndroidComposeRule<MainActivity>()

    @Inject
    lateinit var fakeRepository: FakeTaskRepository

    private lateinit var robot: TaskListRobot

    @Before
    fun setUp() {
        hiltRule.inject()
        robot = TaskListRobot(composeRule)
    }

    @Test
    fun given_no_tasks_exist_when_screen_is_displayed_then_empty_state_is_shown() {
        fakeRepository.emit(emptyList())
        composeRule.waitForIdle()

        robot.assertEmptyStateIsDisplayed()
    }

    @Test
    fun given_tasks_exist_when_screen_is_displayed_then_task_list_is_shown() {
        fakeRepository.emit(
            listOf(
                aTask(id = 1, title = "Buy groceries"),
                aTask(id = 2, title = "Call doctor"),
            ),
        )
        composeRule.waitForIdle()

        robot.assertTaskListIsDisplayed()
        robot.assertTaskIsDisplayed("Buy groceries")
        robot.assertTaskIsDisplayed("Call doctor")
    }

    @Test
    fun given_important_and_urgent_task_when_screen_is_displayed_then_task_has_important_and_urgent_tier() {
        fakeRepository.emit(
            listOf(
                aTask(id = 1, title = "Critical task", isImportant = true, isUrgent = true),
            ),
        )
        composeRule.waitForIdle()

        robot.assertTaskHasPriorityTier("Critical task", PriorityTier.IMPORTANT_AND_URGENT)
    }

    @Test
    fun given_important_task_and_non_important_task_when_screen_is_displayed_then_important_task_appears_first() {
        fakeRepository.emit(
            listOf(
                aTask(id = 1, title = "Normal task", isImportant = false, isUrgent = false),
                aTask(id = 2, title = "Important task", isImportant = true, isUrgent = false),
            ),
        )
        composeRule.waitForIdle()

        robot.assertTaskAppearsBeforeTask("Important task", "Normal task")
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
        createdAt = 0L,
    )
}
