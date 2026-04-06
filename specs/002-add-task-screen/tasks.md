---

description: "Task list for Android Add Task Screen feature"
---

# Tasks: Android Add Task Screen

**Input**: Design documents from `/specs/002-add-task-screen/`
**Prerequisites**: plan.md ✅, spec.md ✅, research.md ✅, data-model.md ✅, contracts/ ✅

**Tests**: MANDATORY per constitution Principle II (Test-First) and Testing Standards.
All test tasks MUST be written and verified to fail before their corresponding implementation.

**Organization**: Tasks are grouped by phase. Only User Story 1 is in scope per spec.md.

## Format: `[ID] [P?] [Story?] Description`

- **[P]**: Can run in parallel (different files, no dependencies)
- **[Story]**: Which user story this task belongs to (US1)
- Exact file paths included in all implementation tasks

## Path Conventions

All source paths are relative to the repository root.

- Main source: `app/src/main/kotlin/com/example/taskprioritylist/`
- Unit tests: `app/src/test/kotlin/com/example/taskprioritylist/`
- UI tests: `app/src/androidTest/kotlin/com/example/taskprioritylist/`

---

## Phase 1: Setup

**Purpose**: Build configuration changes required before any code can compile.

- [x] T001 Update `gradle/libs.versions.toml` — add `navigation3 = "1.0.1"` and `kotlinxSerialization = "1.8.0"` under `[versions]`; add `navigation3-runtime`, `navigation3-ui`, `kotlinx-serialization-json` library aliases under `[libraries]`; add `kotlinx-serialization = { id = "org.jetbrains.kotlin.plugin.serialization", version.ref = "kotlin" }` under `[plugins]`
- [x] T002 Update `app/build.gradle.kts` — apply `alias(libs.plugins.kotlinx.serialization)` in the plugins block; add `implementation(libs.navigation3.runtime)`, `implementation(libs.navigation3.ui)`, `implementation(libs.kotlinx.serialization.json)` in dependencies (depends on T001)
- [x] T003 [P] Create package directory trees under main source: `presentation/navigation/`, `presentation/addtask/`; under unit tests: `presentation/addtask/`; under UI tests: `presentation/addtask/robots/`

---

## Phase 2: Foundational (Blocking Prerequisites)

**Purpose**: Core infrastructure that MUST be complete before User Story 1 can be implemented.

**⚠️ CRITICAL**: No user story work can begin until this phase is complete.

- [x] T004 [P] Update `app/src/main/kotlin/com/example/taskprioritylist/data/local/TaskDao.kt` — add `@Insert(onConflict = OnConflictStrategy.ABORT) suspend fun insertTask(entity: TaskEntity)`
- [x] T005 [P] Update `app/src/main/kotlin/com/example/taskprioritylist/domain/repository/TaskRepository.kt` — add `suspend fun addTask(task: Task)`
- [x] T006 Update `app/src/main/kotlin/com/example/taskprioritylist/data/repository/TaskRepositoryImpl.kt` — implement `override suspend fun addTask(task: Task) { taskDao.insertTask(task.toEntity()) }` (depends on T004, T005)
- [x] T007 [P] Create `app/src/main/kotlin/com/example/taskprioritylist/presentation/addtask/AddTaskUiState.kt` — `data class AddTaskUiState(val title: String = "", val description: String = "", val isImportant: Boolean = false, val isUrgent: Boolean = false, val titleError: TitleValidationError? = null, val isDirty: Boolean = false, val isSaving: Boolean = false, val showDiscardDialog: Boolean = false, val shouldNavigateBack: Boolean = false)`. Also create `TitleValidationError.kt` — `enum class TitleValidationError { BLANK }`.
- [x] T008 [P] ~~Create `AddTaskEvent.kt`~~ — superseded during implementation; navigation is driven by `shouldNavigateBack: Boolean` in `AddTaskUiState` instead of a `SharedFlow<AddTaskEvent>`.
- [x] T009 [P] Create `app/src/main/kotlin/com/example/taskprioritylist/presentation/navigation/AppDestinations.kt` — `@Serializable data object TaskList` and `@Serializable data object AddTask`
- [x] T010 Update `app/src/androidTest/kotlin/com/example/taskprioritylist/fake/FakeTaskRepository.kt` — add `override suspend fun addTask(task: Task) { }` stub (and any error-injection variant needed by tests) (depends on T005)

**Checkpoint**: Foundation ready — User Story 1 implementation can now begin.

---

## Phase 3: User Story 1 — Add a New Task (Priority: P1) 🎯 MVP

**Goal**: Allow a user to navigate from the task list to a dedicated Add Task screen via a FAB, fill in title/description/toggles, save the task (persisted via Room), and return to the list. Navigating away with unsaved input triggers a discard confirmation dialog.

**Independent Test**: Launch the app, tap the FAB, fill in a title, tap Save, and verify the new task appears in the list. Also verify: blank-title validation error, discard dialog on back with dirty form, and immediate back with clean form.

### Tests for User Story 1 ⚠️ WRITE FIRST — MUST FAIL BEFORE IMPLEMENTATION

> **MANDATORY per Constitution Principle II**: Write all tests below and confirm they
> fail (red) before writing any implementation code.

- [x] T011 [P] [US1] Write failing `AddTaskUseCaseTest.kt` in `app/src/test/kotlin/com/example/taskprioritylist/domain/usecase/` — verify: `invoke()` calls `repository.addTask()` with title trimmed; blank description coerced to null; `isImportant`/`isUrgent` passed through; `createdAt` is non-zero; use MockK to mock `TaskRepository`
- [x] T012 [P] [US1] Write failing `AddTaskViewModelTest.kt` in `app/src/test/kotlin/com/example/taskprioritylist/presentation/addtask/` — verify: `onTitleChanged` updates `title` and sets `isDirty = true`; `onDescriptionChanged` truncates at `MAX_DESCRIPTION_LENGTH`; `onSave()` with blank/whitespace title sets `titleError = TitleValidationError.BLANK` and does not call use case; `onSave()` with valid title calls use case and sets `shouldNavigateBack = true`; use case failure resets `isSaving` without setting `shouldNavigateBack`; `onDiscardRequested/Dismissed` toggles `showDiscardDialog`; `onDiscardConfirmed` sets `shouldNavigateBack = true` without calling use case; use MockK for `AddTaskUseCase`, `MainDispatcherExtension` for coroutine dispatcher
- [x] T013 [P] [US1] Extend `app/src/test/kotlin/com/example/taskprioritylist/data/repository/TaskRepositoryImplTest.kt` — add test cases verifying that `addTask()` inserts the task into the Room in-memory database and it subsequently appears in `getTasks()` emission; use existing Room in-memory DB setup
- [x] T014 [P] [US1] Create `AddTaskRobot.kt` in `app/src/androidTest/kotlin/com/example/taskprioritylist/presentation/addtask/robots/` — Robot class encapsulating all Add Task screen interactions and assertions: `enterTitle(text: String)`, `enterDescription(text: String)`, `toggleImportant()`, `toggleUrgent()`, `tapSave()`, `tapCancel()`, `tapDiscardInDialog()`, `tapKeepEditingInDialog()`, `assertTitleError()`, `assertDiscardDialogVisible()`, `assertFormIntact(title: String)` (asserts title field still contains given text after dialog dismiss), `assertNotVisible()`
- [x] T015 [US1] Write failing `AddTaskScreenTest.kt` in `app/src/androidTest/kotlin/com/example/taskprioritylist/presentation/addtask/` — `@HiltAndroidTest`; use `ComposeTestRule`; inject `FakeTaskRepository`; test cases: (1) save with valid title → task saved + screen closed, (2) save with blank title → title error shown, (3) back with dirty form → discard dialog shown, (4) back with clean form → no dialog, (5) save with Important=on + Urgent=off → after navigation back, task appears in tier-2 (Important-only) using `TaskListRobot.assertTasksInOrder()`, (6) back with dirty form → tap "Keep Editing" → dialog dismissed + `AddTaskRobot.assertFormIntact(title)`, (7) scroll task list to bottom → navigate to AddTask → save → `TaskListRobot` asserts scroll position is unchanged; delegate all interactions to `AddTaskRobot` and `TaskListRobot` (depends on T014)
- [x] T016 [P] [US1] Update `TaskListRobot.kt` in `app/src/androidTest/kotlin/com/example/taskprioritylist/presentation/tasklist/robots/` — add `assertFabVisible()` method using the FAB's test tag
- [x] T017 [US1] Update `TaskListScreenTest.kt` in `app/src/androidTest/kotlin/com/example/taskprioritylist/presentation/tasklist/` — add test case asserting the FAB is visible on the task list screen; delegate to `TaskListRobot.assertFabVisible()` (depends on T016)

### Implementation for User Story 1

- [x] T018 [P] [US1] Create `AddTaskTestTags.kt` in `app/src/main/kotlin/com/example/taskprioritylist/presentation/addtask/` — object with string constants: `TITLE_FIELD = "add_task_title"`, `DESCRIPTION_FIELD = "add_task_description"`, `IMPORTANT_TOGGLE = "add_task_important"`, `URGENT_TOGGLE = "add_task_urgent"`, `SAVE_BUTTON = "add_task_save"`, `DISCARD_DIALOG = "add_task_discard_dialog"`, `DISCARD_CONFIRM = "add_task_discard_confirm"`
- [x] T019 [P] [US1] Implement `AddTaskUseCase.kt` in `app/src/main/kotlin/com/example/taskprioritylist/domain/usecase/` — `@Inject constructor(private val repository: TaskRepository)`; `suspend operator fun invoke(title: String, description: String?, isImportant: Boolean, isUrgent: Boolean)` calls `repository.addTask(Task(id = 0, title = title.trim(), description = description?.trim()?.ifBlank { null }, isImportant = isImportant, isUrgent = isUrgent, createdAt = System.currentTimeMillis()))`
- [x] T020 [US1] Implement `AddTaskViewModel.kt` in `app/src/main/kotlin/com/example/taskprioritylist/presentation/addtask/` — `@HiltViewModel @Inject constructor(private val addTaskUseCase: AddTaskUseCase)`; `private val _uiState: MutableStateFlow<AddTaskUiState>`; `val uiState: StateFlow<AddTaskUiState>`; implement `onTitleChanged` (clears titleError), `onDescriptionChanged` (caps at 140 chars), `onImportantToggled`, `onUrgentToggled`, `onSave` (validates title via `TitleValidationError`, invokes use case in `viewModelScope.launch` with `runCatching`, sets `shouldNavigateBack = true` on success), `onDiscardRequested` (sets `showDiscardDialog = true`), `onDiscardDismissed` (sets `showDiscardDialog = false`), `onDiscardConfirmed` (sets `shouldNavigateBack = true`) (depends on T007, T019)
- [x] T021 [US1] Implement `AddTaskScreen.kt` in `app/src/main/kotlin/com/example/taskprioritylist/presentation/addtask/` — `@Composable fun AddTaskScreen(onNavigateBack: () -> Unit, viewModel: AddTaskViewModel = hiltViewModel())`; collect `uiState`; `LaunchedEffect(lifecycle)` filtering `uiState` for `shouldNavigateBack == true` to call `onNavigateBack()`; `BackHandler(enabled = uiState.isDirty)` calling `viewModel.onDiscardRequested()`; delegates all visual rendering to private `AddTaskScreenContent` composable (enabling `@Preview`); content includes `Scaffold` with `TopAppBar` (Cancel icon button), title `OutlinedTextField` with `FocusRequester` + `LaunchedEffect(Unit)` for auto-focus, description `OutlinedTextField`, Important/Urgent `Switch`es, Save `Button`, and `AlertDialog` for discard confirmation; apply `AddTaskTestTags` to all interactive elements (depends on T018, T020)
- [x] T022 [US1] Update `TaskListScreen.kt` in `app/src/main/kotlin/com/example/taskprioritylist/presentation/tasklist/` — add `onAddTask: () -> Unit` parameter; declare `val listState = rememberLazyListState()` and pass it to `LazyColumn(state = listState)` so scroll position is preserved across back-stack transitions; wrap in a `Scaffold`; add `FloatingActionButton` to `Scaffold.floatingActionButton` slot calling `onAddTask()`; apply FAB test tag from `TaskListTestTags` (add `FAB = "task_list_fab"` constant to `TaskListTestTags.kt`) (depends on T018)
- [x] T023 [US1] Implement `AppNavGraph.kt` in `app/src/main/kotlin/com/example/taskprioritylist/presentation/navigation/` — `@Composable fun AppNavGraph()`; `val backStack = remember { mutableStateListOf<Any>(TaskList) }`; `NavDisplay(backStack = backStack, onBack = { backStack.removeLastOrNull() }, entryProvider = entryProvider { entry<TaskList> { TaskListScreen(onAddTask = { backStack.add(AddTask) }) }; entry<AddTask> { AddTaskScreen(onNavigateBack = { backStack.removeLastOrNull() }) } })` — no Theme wrapper here; Theme is owned by MainActivity (depends on T009, T021, T022)
- [x] T024 [US1] Update `app/src/main/kotlin/com/example/taskprioritylist/MainActivity.kt` — replace `setContent { TaskListScreen() }` with `setContent { Theme { AppNavGraph() } }` — MainActivity owns the Theme wrapper (depends on T023)

**Checkpoint**: User Story 1 is fully functional and independently testable. All unit and UI tests must pass (green).

---

## Phase 4: Polish & Cross-Cutting Concerns

**Purpose**: Code quality and final validation across the feature.

- [x] T025 [P] Run `./gradlew ktlintCheck` and fix all violations across all new and modified source files
- [x] T026 Run `./gradlew test` — verify all JUnit 5 unit tests pass (AddTaskUseCaseTest, AddTaskViewModelTest, TaskRepositoryImplTest including new insert cases)
- [x] T027 Run `./gradlew connectedAndroidTest` — verify all Compose UI tests pass (AddTaskScreenTest, TaskListScreenTest including FAB test)

---

## Dependencies & Execution Order

### Phase Dependencies

- **Setup (Phase 1)**: No dependencies — start immediately
- **Foundational (Phase 2)**: Depends on Phase 1 completion — BLOCKS User Story 1
- **User Story 1 (Phase 3)**: Depends on Phase 2 completion
  - Tests (T011–T017): Can run in parallel after Phase 2
  - Implementation (T018–T024): Start after tests are confirmed failing
- **Polish (Phase 4)**: Depends on all Phase 3 tasks complete

### Within User Story 1

- All test tasks (T011–T014, T016) can run in parallel (different files)
- T015 depends on T014 (Robot must exist before test file that uses it)
- T017 depends on T016 (updated Robot)
- T018 and T019 can run in parallel (different files)
- T020 depends on T007, T008 (foundational), T019
- T021 depends on T018, T020
- T022 depends on T018
- T023 depends on T009 (foundational), T021, T022
- T024 depends on T023

### Parallel Opportunities

```bash
# Phase 2 — run together:
Task: "Update TaskDao insertTask"              # T004
Task: "Update TaskRepository addTask"          # T005
Task: "Create AddTaskUiState"                  # T007
Task: "Create AddTaskEvent"                    # T008
Task: "Create AppDestinations"                 # T009

# Phase 3 tests — run together:
Task: "Write failing AddTaskUseCaseTest"        # T011
Task: "Write failing AddTaskViewModelTest"      # T012
Task: "Write failing TaskRepositoryImplTest"    # T013
Task: "Create AddTaskRobot"                     # T014
Task: "Update TaskListRobot assertFabVisible"   # T016

# Phase 3 implementation — run together after T018/T019 done:
Task: "Implement AddTaskScreen"                 # T021
Task: "Update TaskListScreen + FAB"             # T022
```

---

## Implementation Strategy

### MVP (User Story 1 Only)

1. Complete Phase 1: Setup
2. Complete Phase 2: Foundational (CRITICAL — blocks everything)
3. Write all Phase 3 tests → confirm all fail (red)
4. Implement Phase 3 → make all tests pass (green)
5. **STOP and VALIDATE**: Run full test suite, verify independently
6. Complete Phase 4: Polish

### Task Counts

| Phase | Tasks | Parallel Opportunities |
|-------|-------|----------------------|
| Phase 1: Setup | 3 | T003 |
| Phase 2: Foundational | 7 | T004, T005, T007, T008, T009 |
| Phase 3: US1 Tests | 7 | T011, T012, T013, T014, T016 |
| Phase 3: US1 Impl | 7 | T018, T019; T021+T022 after T020 |
| Phase 4: Polish | 3 | T025 |
| **Total** | **27** | |

---

## Notes

- [P] tasks = different files, no dependencies — safe to parallelize
- [US1] label maps every task to User Story 1 for traceability
- Tests MUST fail before implementation — this is non-negotiable per constitution
- `AddTaskRobot` encapsulates ALL UI interactions; `AddTaskScreenTest` contains ONLY robot calls and test case structure
- `TaskRepositoryImplTest` MUST use Room in-memory database — do NOT mock Room internals
- `AddTaskViewModel` MUST be tested with a mock `AddTaskUseCase`, NOT `AddTaskUseCaseTest`
- `AppNavGraph` is not directly unit-tested — covered by `AddTaskScreenTest` and `TaskListScreenTest` end-to-end
- Commit after each phase checkpoint
