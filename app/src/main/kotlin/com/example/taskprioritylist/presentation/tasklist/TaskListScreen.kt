package com.example.taskprioritylist.presentation.tasklist

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.taskprioritylist.R
import com.example.taskprioritylist.domain.model.Task
import com.example.taskprioritylist.presentation.tasklist.TaskListTestTags.EMPTY_STATE
import com.example.taskprioritylist.presentation.tasklist.TaskListTestTags.TASK_LIST
import com.example.taskprioritylist.presentation.theme.TaskPriorityListTheme

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TaskListScreen(viewModel: TaskListViewModel = hiltViewModel()) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    TaskListContent(uiState = uiState)
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
internal fun TaskListContent(uiState: TaskListUiState) {
    Scaffold(
        topBar = {
            TopAppBar(title = { Text(stringResource(R.string.app_name)) })
        },
    ) { innerPadding ->
        Box(
            modifier =
                Modifier
                    .fillMaxSize()
                    .padding(innerPadding),
        ) {
            when (val state = uiState) {
                is TaskListUiState.Loading -> {
                    CircularProgressIndicator(modifier = Modifier.align(Alignment.Center))
                }

                is TaskListUiState.Empty -> {
                    Text(
                        text = stringResource(R.string.empty_state_message),
                        modifier =
                            Modifier
                                .align(Alignment.Center)
                                .testTag(EMPTY_STATE),
                    )
                }

                is TaskListUiState.Success -> {
                    LazyColumn(
                        modifier =
                            Modifier
                                .fillMaxSize()
                                .testTag(TASK_LIST),
                        verticalArrangement = Arrangement.spacedBy(8.dp),
                        contentPadding = PaddingValues(16.dp),
                    ) {
                        items(state.tasks, key = { it.id }) { task ->
                            TaskItem(task = task)
                        }
                    }
                }

                is TaskListUiState.Error -> {
                    Text(
                        text = state.message,
                        color = MaterialTheme.colorScheme.error,
                        modifier =
                            Modifier
                                .align(Alignment.Center)
                                .padding(16.dp),
                    )
                }
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
private fun TaskListContentLoadingPreview() {
    TaskPriorityListTheme {
        TaskListContent(uiState = TaskListUiState.Loading)
    }
}

@Preview(showBackground = true)
@Composable
private fun TaskListContentEmptyPreview() {
    TaskPriorityListTheme {
        TaskListContent(uiState = TaskListUiState.Empty)
    }
}

@Preview(showBackground = true)
@Composable
private fun TaskListContentSuccessPreview() {
    TaskPriorityListTheme {
        TaskListContent(
            uiState =
                TaskListUiState.Success(
                    tasks =
                        listOf(
                            Task(
                                id = 1,
                                title = "Fix critical bug",
                                description = "App crashes on launch",
                                isImportant = true,
                                isUrgent = true,
                                createdAt = 0L,
                            ),
                            Task(id = 2, title = "Write report", description = null, isImportant = true, isUrgent = false, createdAt = 0L),
                            Task(
                                id = 3,
                                title = "Reply to emails",
                                description = "Check inbox",
                                isImportant = false,
                                isUrgent = true,
                                createdAt = 0L,
                            ),
                            Task(id = 4, title = "Read book", description = null, isImportant = false, isUrgent = false, createdAt = 0L),
                        ),
                ),
        )
    }
}

@Preview(showBackground = true)
@Composable
private fun TaskListContentErrorPreview() {
    TaskPriorityListTheme {
        TaskListContent(uiState = TaskListUiState.Error(message = "Failed to load tasks"))
    }
}
