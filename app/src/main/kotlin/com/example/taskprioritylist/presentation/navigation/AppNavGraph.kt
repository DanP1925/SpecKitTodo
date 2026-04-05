package com.example.taskprioritylist.presentation.navigation

import androidx.compose.runtime.Composable
import androidx.navigation3.runtime.entryProvider
import androidx.navigation3.runtime.rememberNavBackStack
import androidx.navigation3.ui.NavDisplay
import com.example.taskprioritylist.presentation.addtask.AddTaskScreen
import com.example.taskprioritylist.presentation.tasklist.TaskListScreen

@Composable
fun AppNavGraph() {
    val backStack = rememberNavBackStack(TaskList)

    NavDisplay(
        backStack = backStack,
        onBack = { backStack.removeLastOrNull() },
        entryProvider =
            entryProvider {
                entry<TaskList> {
                    TaskListScreen(
                        onAddTask = { backStack.add(AddTask) },
                    )
                }
                entry<AddTask> {
                    AddTaskScreen(
                        onNavigateBack = { backStack.removeLastOrNull() },
                    )
                }
            },
    )
}
