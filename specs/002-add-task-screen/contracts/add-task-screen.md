# UI Contract: Add Task Screen

**Branch**: `002-add-task-screen` | **Date**: 2026-04-05

## Screen Composables

### `AddTaskScreen`

```kotlin
@Composable
fun AddTaskScreen(
    onNavigateBack: () -> Unit,
    viewModel: AddTaskViewModel = hiltViewModel(),
)
```

**Responsibilities**: Collect `AddTaskUiState` from ViewModel; render the form; delegate all events to ViewModel; call `onNavigateBack` when `uiState.shouldNavigateBack` is `true`.

**Navigation contract**: The composable observes `uiState` via a `LaunchedEffect`, filters for `shouldNavigateBack == true`, and calls `onNavigateBack()`. No events flow or sealed interface is involved. The caller (`AppNavGraph`) wires this to `backStack.removeLastOrNull()`.

---

### `TaskListScreen` (modified)

```kotlin
@Composable
fun TaskListScreen(
    onAddTask: () -> Unit,
    viewModel: TaskListViewModel = hiltViewModel(),
)
```

**Change**: adds `onAddTask: () -> Unit` parameter. The FAB calls `onAddTask()`. The caller (`AppNavGraph`) wires this to `backStack.add(AddTask)`.

---

## ViewModel Interface

### `AddTaskViewModel`

| Event | Method | Effect |
|-------|--------|--------|
| Title field changed | `onTitleChanged(value: String)` | Update `title`, set `isDirty = true`, clear `titleError` |
| Description changed | `onDescriptionChanged(value: String)` | Update `description` (capped at 140 chars), set `isDirty = true` |
| Important toggled | `onImportantToggled()` | Flip `isImportant`, set `isDirty = true` |
| Urgent toggled | `onUrgentToggled()` | Flip `isUrgent`, set `isDirty = true` |
| Save tapped | `onSave()` | Validate → if invalid set `titleError`; if valid set `isSaving = true`, invoke `AddTaskUseCase`, then set `shouldNavigateBack = true` |
| Back/Cancel pressed (dirty) | `onDiscardRequested()` | Set `showDiscardDialog = true` |
| Dialog dismissed | `onDiscardDismissed()` | Set `showDiscardDialog = false` |
| Discard confirmed | `onDiscardConfirmed()` | Set `shouldNavigateBack = true`, clear `showDiscardDialog` |

**Navigation signal**: Navigation is driven entirely by `shouldNavigateBack: Boolean` in `AddTaskUiState`. There is no events flow or sealed interface — the composable observes state directly.

---

## UI Behaviour

### Form Fields

| Element | Type | Default | Behaviour |
|---------|------|---------|-----------|
| Title | `OutlinedTextField` | "" | Auto-focused; keyboard opens on entry. Shows `titleError` below when non-null. |
| Description | `OutlinedTextField` | "" | Optional; no validation error. |
| Important | `Switch` or `Checkbox` | off | Toggles `isImportant` on tap. |
| Urgent | `Switch` or `Checkbox` | off | Toggles `isUrgent` on tap. |
| Save | `Button` | enabled | Calls `onSave()`. |
| Cancel / Up arrow | `IconButton` in TopAppBar | — | Calls back handler logic (discard dialog if dirty). |

### Back / Discard Flow

```
User presses back / Cancel
    └─► isDirty?
         ├─ No  → navigate back immediately (no dialog)
         └─ Yes → show "Discard changes?" AlertDialog
                       ├─ "Discard"      → onDiscardConfirmed()
                       └─ "Keep Editing" → dismiss dialog, stay on screen
```

`BackHandler(enabled = uiState.isDirty)` drives the system back gesture. The Cancel button in the TopAppBar runs the same logic.

---

## Test Tags

Defined in `presentation/addtask/AddTaskTestTags.kt`:

| Constant | Tag value | Used on |
|----------|-----------|---------|
| `TITLE_FIELD` | `"add_task_title"` | Title `OutlinedTextField` |
| `DESCRIPTION_FIELD` | `"add_task_description"` | Description `OutlinedTextField` |
| `IMPORTANT_TOGGLE` | `"add_task_important"` | Important toggle |
| `URGENT_TOGGLE` | `"add_task_urgent"` | Urgent toggle |
| `SAVE_BUTTON` | `"add_task_save"` | Save `Button` |
| `CANCEL_BUTTON` | `"add_task_cancel"` | Cancel `IconButton` in `TopAppBar` |
| `DISCARD_DIALOG` | `"add_task_discard_dialog"` | Discard `AlertDialog` |
| `DISCARD_CONFIRM` | `"add_task_discard_confirm"` | "Discard" button inside dialog |
| `KEEP_EDITING_BUTTON` | `"add_task_keep_editing"` | "Keep Editing" button inside dialog |
| `TITLE_ERROR` | `"add_task_title_error"` | Title error `Text` below field |

---

## AppNavGraph Contract

```kotlin
@Composable
fun AppNavGraph()
```

No parameters. Owns the back stack. Wires `onAddTask` and `onNavigateBack` lambdas.
Hosted directly by `MainActivity.setContent { Theme { AppNavGraph() } }`.
