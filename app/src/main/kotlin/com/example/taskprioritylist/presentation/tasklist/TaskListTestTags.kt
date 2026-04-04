package com.example.taskprioritylist.presentation.tasklist

object TaskListTestTags {
    const val TASK_LIST = "task_list"
    const val EMPTY_STATE = "empty_state"

    fun taskItem(title: String) = "task_item_$title"

    fun importantBadge(title: String) = "badge_important_$title"

    fun urgentBadge(title: String) = "badge_urgent_$title"
}
