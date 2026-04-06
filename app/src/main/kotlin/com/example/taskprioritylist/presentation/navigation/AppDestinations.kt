package com.example.taskprioritylist.presentation.navigation

import androidx.navigation3.runtime.NavKey
import kotlinx.serialization.Serializable

@Serializable
data object TaskList : NavKey

@Serializable
data object AddTask : NavKey
