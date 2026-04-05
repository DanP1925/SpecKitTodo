package com.example.taskprioritylist

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import com.example.taskprioritylist.presentation.navigation.AppNavGraph
import com.example.taskprioritylist.presentation.theme.TaskPriorityListTheme
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            TaskPriorityListTheme {
                AppNavGraph()
            }
        }
    }
}
