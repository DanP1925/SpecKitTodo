package com.example.taskprioritylist.presentation.addtask

import androidx.compose.ui.test.junit4.createAndroidComposeRule
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.example.taskprioritylist.MainActivity
import com.example.taskprioritylist.domain.model.Task
import com.example.taskprioritylist.fake.FakeTaskRepository
import com.example.taskprioritylist.presentation.addtask.robots.AddTaskRobot
import com.example.taskprioritylist.presentation.tasklist.PriorityTier
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
class AddTaskScreenTest {
    @get:Rule(order = 0)
    val hiltRule = HiltAndroidRule(this)

    @get:Rule(order = 1)
    val composeRule = createAndroidComposeRule<MainActivity>()

    @Inject
    lateinit var fakeRepository: FakeTaskRepository

    private lateinit var taskListRobot: TaskListRobot
    private lateinit var addTaskRobot: AddTaskRobot

    @Before
    fun setUp() {
        hiltRule.inject()
        fakeRepository.emit(emptyList())
        taskListRobot = TaskListRobot(composeRule)
        addTaskRobot = AddTaskRobot(composeRule)
        composeRule.waitForIdle()
    }

    @Test
    fun given_valid_title_when_save_then_task_is_saved_and_screen_closes() {
        taskListRobot.tapFab()
        composeRule.waitForIdle()

        addTaskRobot.enterTitle("Buy milk")
        addTaskRobot.tapSave()
        composeRule.waitForIdle()

        addTaskRobot.assertNotVisible()
        taskListRobot.assertTaskIsDisplayed("Buy milk")
    }

    @Test
    fun given_blank_title_when_save_then_title_error_is_shown() {
        taskListRobot.tapFab()
        composeRule.waitForIdle()

        addTaskRobot.tapSave()
        composeRule.waitForIdle()

        addTaskRobot.assertTitleError()
    }

    @Test
    fun given_dirty_form_when_back_then_discard_dialog_is_shown() {
        taskListRobot.tapFab()
        composeRule.waitForIdle()

        addTaskRobot.enterTitle("Some task")
        addTaskRobot.tapCancel()
        composeRule.waitForIdle()

        addTaskRobot.assertDiscardDialogVisible()
    }

    @Test
    fun given_clean_form_when_back_then_no_dialog_shown() {
        taskListRobot.tapFab()
        composeRule.waitForIdle()

        addTaskRobot.tapCancel()
        composeRule.waitForIdle()

        taskListRobot.assertFabVisible()
        addTaskRobot.assertNotVisible()
    }

    @Test
    fun given_important_on_and_urgent_off_when_save_then_task_appears_in_important_only_tier() {
        taskListRobot.tapFab()
        composeRule.waitForIdle()

        addTaskRobot.enterTitle("Important task")
        addTaskRobot.toggleImportant()
        addTaskRobot.tapSave()
        composeRule.waitForIdle()

        taskListRobot.assertTaskHasPriorityTier("Important task", PriorityTier.IMPORTANT)
    }

    @Test
    fun given_dirty_form_when_back_and_keep_editing_then_dialog_dismissed_and_form_intact() {
        taskListRobot.tapFab()
        composeRule.waitForIdle()

        addTaskRobot.enterTitle("Draft title")
        addTaskRobot.tapCancel()
        composeRule.waitForIdle()
        addTaskRobot.assertDiscardDialogVisible()

        addTaskRobot.tapKeepEditingInDialog()
        composeRule.waitForIdle()

        addTaskRobot.assertFormIntact("Draft title")
    }

    @Test
    fun given_scrolled_task_list_when_add_task_and_save_then_scroll_position_is_preserved() {
        val tasks =
            (1..20).map { i ->
                Task(id = i.toLong(), title = "Task $i", description = null, isImportant = false, isUrgent = false, createdAt = i.toLong())
            }
        fakeRepository.emit(tasks)
        composeRule.waitForIdle()
        taskListRobot.scrollToIndex(19)
        composeRule.waitForIdle()

        taskListRobot.tapFab()
        composeRule.waitForIdle()

        addTaskRobot.enterTitle("New task")
        addTaskRobot.tapSave()
        composeRule.waitForIdle()

        taskListRobot.assertTaskIsDisplayed("Task 20")
    }
}
