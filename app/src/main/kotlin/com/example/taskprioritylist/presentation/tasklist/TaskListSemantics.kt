package com.example.taskprioritylist.presentation.tasklist

import androidx.compose.ui.semantics.SemanticsPropertyKey
import androidx.compose.ui.semantics.SemanticsPropertyReceiver
import com.example.taskprioritylist.domain.model.Task

val PriorityTierKey = SemanticsPropertyKey<String>("PriorityTier")
var SemanticsPropertyReceiver.priorityTier by PriorityTierKey

object PriorityTier {
    const val IMPORTANT_AND_URGENT = "important_and_urgent"
    const val IMPORTANT = "important"
    const val URGENT = "urgent"
    const val NONE = "none"
}

fun Task.toPriorityTier(): String =
    when {
        isImportant && isUrgent -> PriorityTier.IMPORTANT_AND_URGENT
        isImportant -> PriorityTier.IMPORTANT
        isUrgent -> PriorityTier.URGENT
        else -> PriorityTier.NONE
    }
