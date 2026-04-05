package com.example.taskprioritylist.presentation.addtask

import androidx.activity.compose.BackHandler
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.taskprioritylist.R

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddTaskScreen(
    onNavigateBack: () -> Unit,
    viewModel: AddTaskViewModel = hiltViewModel(),
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    var showDiscardDialog by remember { mutableStateOf(false) }

    LaunchedEffect(Unit) {
        viewModel.events.collect { event ->
            when (event) {
                AddTaskEvent.NavigateBack -> onNavigateBack()
            }
        }
    }

    BackHandler(enabled = uiState.isDirty) {
        showDiscardDialog = true
    }

    if (showDiscardDialog) {
        AlertDialog(
            modifier = Modifier.testTag(AddTaskTestTags.DISCARD_DIALOG),
            onDismissRequest = { showDiscardDialog = false },
            title = { Text(stringResource(R.string.discard_changes_title)) },
            text = { Text(stringResource(R.string.discard_changes_message)) },
            confirmButton = {
                TextButton(
                    onClick = {
                        showDiscardDialog = false
                        viewModel.onDiscardConfirmed()
                    },
                    modifier = Modifier.testTag(AddTaskTestTags.DISCARD_CONFIRM),
                ) {
                    Text(stringResource(R.string.discard_button))
                }
            },
            dismissButton = {
                TextButton(
                    onClick = { showDiscardDialog = false },
                    modifier = Modifier.testTag(AddTaskTestTags.KEEP_EDITING_BUTTON),
                ) {
                    Text(stringResource(R.string.keep_editing_button))
                }
            },
        )
    }

    val focusRequester = remember { FocusRequester() }
    LaunchedEffect(Unit) {
        focusRequester.requestFocus()
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(stringResource(R.string.add_task_title)) },
                navigationIcon = {
                    IconButton(
                        onClick = {
                            if (uiState.isDirty) {
                                showDiscardDialog = true
                            } else {
                                onNavigateBack()
                            }
                        },
                        modifier = Modifier.testTag(AddTaskTestTags.CANCEL_BUTTON),
                    ) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = stringResource(R.string.cancel_button),
                        )
                    }
                },
            )
        },
    ) { innerPadding ->
        Column(
            modifier =
                Modifier
                    .fillMaxSize()
                    .padding(innerPadding)
                    .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp),
        ) {
            OutlinedTextField(
                value = uiState.title,
                onValueChange = viewModel::onTitleChanged,
                label = { Text(stringResource(R.string.title_label)) },
                isError = uiState.titleError != null,
                supportingText =
                    if (uiState.titleError != null) {
                        {
                            Text(
                                text = uiState.titleError!!,
                                modifier = Modifier.testTag(AddTaskTestTags.TITLE_ERROR),
                            )
                        }
                    } else {
                        null
                    },
                modifier =
                    Modifier
                        .fillMaxWidth()
                        .testTag(AddTaskTestTags.TITLE_FIELD)
                        .focusRequester(focusRequester),
            )

            OutlinedTextField(
                value = uiState.description,
                onValueChange = viewModel::onDescriptionChanged,
                label = { Text(stringResource(R.string.description_label)) },
                modifier =
                    Modifier
                        .fillMaxWidth()
                        .height(160.dp)
                        .testTag(AddTaskTestTags.DESCRIPTION_FIELD),
            )

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically,
            ) {
                Text(stringResource(R.string.important_label))
                Switch(
                    checked = uiState.isImportant,
                    onCheckedChange = { viewModel.onImportantToggled() },
                    modifier = Modifier.testTag(AddTaskTestTags.IMPORTANT_TOGGLE),
                )
            }

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically,
            ) {
                Text(stringResource(R.string.urgent_label))
                Switch(
                    checked = uiState.isUrgent,
                    onCheckedChange = { viewModel.onUrgentToggled() },
                    modifier = Modifier.testTag(AddTaskTestTags.URGENT_TOGGLE),
                )
            }

            Button(
                onClick = viewModel::onSave,
                modifier =
                    Modifier
                        .fillMaxWidth()
                        .testTag(AddTaskTestTags.SAVE_BUTTON),
            ) {
                Text(stringResource(R.string.save_button))
            }
        }
    }
}
