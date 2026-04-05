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

**Responsibilities**: Collect `AddTaskUiState` from ViewModel; render the form; delegate all events to ViewModel; call `onNavigateBack` when ViewModel signals navigation.

**Navigation contract**: `onNavigateBack` is called by the ViewModel after a successful save or confirmed discard. The caller (`AppNavGraph`) wires this to `backStack.removeLastOrNull()`.

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
| Description changed | `onDescriptionChanged(value: String)` | Update `description`, set `isDirty = true` |
| Important toggled | `onImportantToggled()` | Flip `isImportant`, set `isDirty = true` |
| Urgent toggled | `onUrgentToggled()` | Flip `isUrgent`, set `isDirty = true` |
| Save tapped | `onSave()` | Validate → if invalid set `titleError`; if valid invoke `AddTaskUseCase` then signal `onNavigateBack` |
| Discard confirmed | `onDiscardConfirmed()` | Signal `onNavigateBack` without saving |

**Event signal**: The ViewModel exposes a `SharedFlow<AddTaskEvent>` (see below). `AddTaskScreen` collects it in a `LaunchedEffect` and dispatches each event to the appropriate callback or local UI action.

### `AddTaskEvent` — `presentation/addtask/AddTaskEvent.kt`

```kotlin
sealed interface AddTaskEvent {
    data object NavigateBack : AddTaskEvent
}
```

`NavigateBack` is emitted after a successful save or a confirmed discard. `AddTaskScreen` calls `onNavigateBack()` upon receiving it. Additional events (e.g. `ShowSnackbar`) can be added here without touching `AddTaskUiState`.

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
| `DISCARD_DIALOG` | `"add_task_discard_dialog"` | Discard `AlertDialog` |
| `DISCARD_CONFIRM` | `"add_task_discard_confirm"` | "Discard" button inside dialog |

---

## AppNavGraph Contract

```kotlin
@Composable
fun AppNavGraph()
```

No parameters. Owns the back stack. Wires `onAddTask` and `onNavigateBack` lambdas.
Hosted directly by `MainActivity.setContent { Theme { AppNavGraph() } }`.
