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
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberUpdatedState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.LocalLifecycleOwner
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.flowWithLifecycle
import com.example.taskprioritylist.R
import kotlinx.coroutines.flow.filter

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddTaskScreen(
    onNavigateBack: () -> Unit,
    viewModel: AddTaskViewModel = hiltViewModel(),
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()

    val lifecycle = LocalLifecycleOwner.current.lifecycle
    val currentOnNavigateBack by rememberUpdatedState(onNavigateBack)
    LaunchedEffect(lifecycle) {
        viewModel.uiState
            .filter { it.shouldNavigateBack }
            .flowWithLifecycle(lifecycle)
            .collect {
                currentOnNavigateBack()
            }
    }

    val focusRequester = remember { FocusRequester() }
    LaunchedEffect(Unit) {
        focusRequester.requestFocus()
    }

    BackHandler(enabled = uiState.isDirty) {
        viewModel.onDiscardRequested()
    }

    AddTaskScreenContent(
        uiState = uiState,
        onNavigateBack = currentOnNavigateBack,
        onTitleChanged = viewModel::onTitleChanged,
        onDescriptionChanged = viewModel::onDescriptionChanged,
        onImportantToggled = viewModel::onImportantToggled,
        onUrgentToggled = viewModel::onUrgentToggled,
        onSave = viewModel::onSave,
        onDiscardRequested = viewModel::onDiscardRequested,
        onDiscardConfirmed = viewModel::onDiscardConfirmed,
        onDiscardDismissed = viewModel::onDiscardDismissed,
        focusRequester = focusRequester,
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun AddTaskScreenContent(
    uiState: AddTaskUiState,
    onNavigateBack: () -> Unit,
    onTitleChanged: (String) -> Unit,
    onDescriptionChanged: (String) -> Unit,
    onImportantToggled: () -> Unit,
    onUrgentToggled: () -> Unit,
    onSave: () -> Unit,
    onDiscardRequested: () -> Unit,
    onDiscardConfirmed: () -> Unit,
    onDiscardDismissed: () -> Unit,
    focusRequester: FocusRequester,
) {
    if (uiState.showDiscardDialog) {
        AlertDialog(
            modifier = Modifier.testTag(AddTaskTestTags.DISCARD_DIALOG),
            onDismissRequest = onDiscardDismissed,
            title = { Text(stringResource(R.string.discard_changes_title)) },
            text = { Text(stringResource(R.string.discard_changes_message)) },
            confirmButton = {
                TextButton(
                    onClick = onDiscardConfirmed,
                    modifier = Modifier.testTag(AddTaskTestTags.DISCARD_CONFIRM),
                ) {
                    Text(stringResource(R.string.discard_button))
                }
            },
            dismissButton = {
                TextButton(
                    onClick = onDiscardDismissed,
                    modifier = Modifier.testTag(AddTaskTestTags.KEEP_EDITING_BUTTON),
                ) {
                    Text(stringResource(R.string.keep_editing_button))
                }
            },
        )
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(stringResource(R.string.add_task_title)) },
                navigationIcon = {
                    IconButton(
                        onClick = {
                            if (uiState.isDirty) {
                                onDiscardRequested()
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
            val titleErrorText =
                when (uiState.titleError) {
                    TitleValidationError.BLANK -> stringResource(R.string.error_title_blank)
                    null -> null
                }
            OutlinedTextField(
                value = uiState.title,
                onValueChange = onTitleChanged,
                label = { Text(stringResource(R.string.title_label)) },
                isError = uiState.titleError != null,
                supportingText =
                    if (titleErrorText != null) {
                        {
                            Text(
                                text = titleErrorText,
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
                onValueChange = onDescriptionChanged,
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
                    onCheckedChange = { onImportantToggled() },
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
                    onCheckedChange = { onUrgentToggled() },
                    modifier = Modifier.testTag(AddTaskTestTags.URGENT_TOGGLE),
                )
            }

            Button(
                onClick = onSave,
                enabled = !uiState.isSaving,
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

@Preview(showBackground = true)
@Composable
private fun AddTaskScreenPreview() {
    AddTaskScreenContent(
        uiState = AddTaskUiState(),
        onNavigateBack = {},
        onTitleChanged = {},
        onDescriptionChanged = {},
        onImportantToggled = {},
        onUrgentToggled = {},
        onSave = {},
        onDiscardRequested = {},
        onDiscardConfirmed = {},
        onDiscardDismissed = {},
        focusRequester = remember { FocusRequester() },
    )
}
