package com.example.taskprioritylist.presentation.tasklist

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.unit.dp
import com.example.taskprioritylist.R
import com.example.taskprioritylist.domain.model.Task

@Composable
fun TaskItem(
    task: Task,
    modifier: Modifier = Modifier,
) {
    val containerColor = taskCardColor(isImportant = task.isImportant, isUrgent = task.isUrgent)
    val tier = task.toPriorityTier()
    val priorityLabel = priorityLabel(isImportant = task.isImportant, isUrgent = task.isUrgent)
    val descriptionPart = if (!task.description.isNullOrBlank()) " ${task.description}." else ""
    Card(
        modifier =
            modifier
                .fillMaxWidth()
                .testTag(TaskListTestTags.taskItem(task.title))
                .semantics(mergeDescendants = true) {
                    priorityTier = tier
                    contentDescription = "${task.title}.$descriptionPart $priorityLabel"
                },
        colors = CardDefaults.cardColors(containerColor = containerColor),
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Text(
                text = task.title,
                style = MaterialTheme.typography.titleMedium,
            )
            if (!task.description.isNullOrBlank()) {
                Text(
                    text = task.description,
                    style = MaterialTheme.typography.bodyMedium,
                    modifier = Modifier.padding(top = 4.dp),
                )
            }
        }
    }
}

@Composable
private fun taskCardColor(
    isImportant: Boolean,
    isUrgent: Boolean,
): Color =
    when {
        isImportant && isUrgent -> MaterialTheme.colorScheme.errorContainer
        isImportant -> MaterialTheme.colorScheme.tertiaryContainer
        isUrgent -> MaterialTheme.colorScheme.secondaryContainer
        else -> MaterialTheme.colorScheme.surfaceVariant
    }

@Composable
private fun priorityLabel(
    isImportant: Boolean,
    isUrgent: Boolean,
): String =
    when {
        isImportant && isUrgent -> stringResource(R.string.priority_important_and_urgent)
        isImportant -> stringResource(R.string.priority_important)
        isUrgent -> stringResource(R.string.priority_urgent)
        else -> stringResource(R.string.priority_none)
    }
