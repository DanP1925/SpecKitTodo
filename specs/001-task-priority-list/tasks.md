---

description: "Task list for Android Task Priority List feature"
---

# Tasks: Android Task Priority List

**Input**: Design documents from `/specs/001-task-priority-list/`
**Prerequisites**: plan.md ✅, spec.md ✅

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

**Purpose**: Project initialization and Gradle configuration.

- [x] T001 Create Android project skeleton — `build.gradle.kts` (project-level), `settings.gradle.kts`, and `gradle/libs.versions.toml` version catalog
- [x] T002 Configure `app/build.gradle.kts` — add plugins and dependencies: Jetpack Compose + Material3, Room + KSP, Hilt + KAPT, Kotlin Coroutines, android-junit5 plugin + JUnit 5 runtime, Jetpack Compose Testing, ktlint Gradle plugin
- [x] T003 [P] Create package directory tree under `app/src/main/kotlin/com/example/taskprioritylist/`: `presentation/tasklist/`, `presentation/theme/`, `domain/model/`, `domain/repository/`, `domain/usecase/`, `data/local/`, `data/mapper/`, `data/repository/`, `data/di/`

---

## Phase 2: Foundational (Blocking Prerequisites)

**Purpose**: Core infrastructure that MUST be complete before User Story 1 can be implemented.

**⚠️ CRITICAL**: No user story work can begin until this phase is complete.

- [x] T004 [P] Create `app/src/main/kotlin/com/example/taskprioritylist/TaskPriorityApp.kt` — `@HiltAndroidApp` Application subclass
- [x] T005 [P] Create `app/src/main/kotlin/com/example/taskprioritylist/domain/model/Task.kt` — pure Kotlin data class: `id: Long`, `title: String`, `description: String?`, `isImportant: Boolean`, `isUrgent: Boolean`, `createdAt: Long`
- [x] T006 [P] Create `app/src/main/kotlin/com/example/taskprioritylist/data/local/TaskEntity.kt` — Room `@Entity(tableName = "tasks")` with same fields as `Task` domain model
- [x] T007 [P] Create `app/src/main/kotlin/com/example/taskprioritylist/presentation/theme/Theme.kt` — Material3 `MaterialTheme` composable wrapper
- [x] T008 Create `app/src/main/kotlin/com/example/taskprioritylist/data/local/TaskDao.kt` — Room `@Dao` with `fun getAllTasks(): Flow<List<TaskEntity>>` (depends on T006)
- [x] T009 Create `app/src/main/kotlin/com/example/taskprioritylist/data/local/TaskDatabase.kt` — Room `@Database(entities = [TaskEntity::class], version = 1)` (depends on T006, T008)
- [x] T010 Create `app/src/main/kotlin/com/example/taskprioritylist/data/di/DatabaseModule.kt` — Hilt `@Module @InstallIn(SingletonComponent::class)` providing `TaskDatabase` and `TaskDao` (depends on T009)
- [x] T011 Create `app/src/main/kotlin/com/example/taskprioritylist/domain/repository/TaskRepository.kt` — interface with `fun getTasks(): Flow<List<Task>>` (depends on T005)
- [x] T012 Update `app/src/main/AndroidManifest.xml` — set `android:name=".TaskPriorityApp"` on `<application>` and declare `MainActivity`
- [x] T013 Create `app/src/main/kotlin/com/example/taskprioritylist/MainActivity.kt` — `@AndroidEntryPoint Activity`, sets Compose content root wrapped in `Theme`

**Checkpoint**: Foundation ready — User Story 1 implementation can now begin.

---

## Phase 3: User Story 1 — View Prioritized Task List (Priority: P1) 🎯 MVP

**Goal**: Display all tasks ordered by priority tier (Important+Urgent → Important → Urgent → Neither), then alphabetically by title within each tier. Show empty-state message when no tasks exist.

**Independent Test**: Launch the app with pre-seeded tasks covering all four priority combinations and verify correct ordering. Verify empty state when list is empty.

### Tests for User Story 1 ⚠️ WRITE FIRST — MUST FAIL BEFORE IMPLEMENTATION

> **MANDATORY per Constitution Principle II**: Write all tests below and confirm they
> fail (red) before writing any implementation code.

- [x] T014 [P] [US1] Write failing `GetPrioritizedTasksUseCaseTest.kt` in `app/src/test/kotlin/com/example/taskprioritylist/domain/usecase/` — test four-tier priority sort and alphabetical-by-title tiebreaker within each tier using a fixed list of `Task` objects
- [x] T015 [P] [US1] Write failing `TaskMapperTest.kt` in `app/src/test/kotlin/com/example/taskprioritylist/data/mapper/` — test `TaskEntity → Task` and `Task → TaskEntity` field mapping including nullable `description`
- [x] T016 [P] [US1] Write failing `TaskRepositoryImplTest.kt` in `app/src/test/kotlin/com/example/taskprioritylist/data/repository/` — use Room in-memory database to verify `getTasks()` emits correct `Flow<List<Task>>` for inserted entities
- [x] T017 [P] [US1] Write failing `TaskListViewModelTest.kt` in `app/src/test/kotlin/com/example/taskprioritylist/presentation/tasklist/` — mock `TaskRepository`, verify `StateFlow` emits `Loading` then `Loaded` with sorted tasks, and `Empty` when list is empty; use `kotlinx-coroutines-test` and `TestCoroutineDispatcher`
- [x] T018 [P] [US1] Create `TaskListRobot.kt` in `app/src/androidTest/kotlin/com/example/taskprioritylist/presentation/tasklist/robots/` — Robot class encapsulating all Compose UI interactions and assertions: `assertTasksInOrder(vararg titles)`, `assertEmptyState()`, `assertTaskVisible(title)`, `assertTaskShowsImportantBadge(title)`, `assertTaskShowsUrgentBadge(title)`
- [x] T019 [US1] Write failing `TaskListScreenTest.kt` in `app/src/androidTest/kotlin/com/example/taskprioritylist/presentation/tasklist/` — use `ComposeTestRule`, inject pre-seeded tasks via fake repository, delegate all assertions to `TaskListRobot` (depends on T018)

### Implementation for User Story 1

- [x] T020 [P] [US1] Implement `app/src/main/kotlin/com/example/taskprioritylist/data/mapper/TaskMapper.kt` — extension functions `TaskEntity.toDomain(): Task` and `Task.toEntity(): TaskEntity`
- [x] T021 [P] [US1] Implement `app/src/main/kotlin/com/example/taskprioritylist/domain/usecase/GetPrioritizedTasksUseCase.kt` — inject `TaskRepository`, return `Flow<List<Task>>` sorted by: tier (isImportant+isUrgent=0, isImportant=1, isUrgent=2, neither=3) then `title.lowercase()` ascending within tier
- [x] T022 [US1] Implement `app/src/main/kotlin/com/example/taskprioritylist/data/repository/TaskRepositoryImpl.kt` — Hilt-bound `@Singleton` impl of `TaskRepository`; maps `TaskDao.getAllTasks()` Flow through `TaskMapper.toDomain()` (depends on T020, T011)
- [x] T023 [US1] Create `app/src/main/kotlin/com/example/taskprioritylist/presentation/tasklist/TaskListUiState.kt` — sealed class: `Loading`, `Empty`, `Loaded(tasks: List<Task>)`
- [x] T024 [US1] Implement `app/src/main/kotlin/com/example/taskprioritylist/presentation/tasklist/TaskListViewModel.kt` — `@HiltViewModel`; collects `GetPrioritizedTasksUseCase` Flow; exposes `StateFlow<TaskListUiState>` (Loading → Empty or Loaded) (depends on T021, T022, T023)
- [x] T025 [US1] Implement `app/src/main/kotlin/com/example/taskprioritylist/presentation/tasklist/TaskListScreen.kt` — Composable collecting `TaskListUiState` from ViewModel; renders `LazyColumn` of task items with title, optional description, and importance/urgency indicators; renders empty-state message for `Empty` state (depends on T024)
- [x] T026 [US1] Update `app/src/main/kotlin/com/example/taskprioritylist/MainActivity.kt` to set `TaskListScreen()` as Compose content inside `Theme` wrapper (depends on T025)

**Checkpoint**: User Story 1 is fully functional and independently testable. All unit and UI tests must pass (green).

---

## Phase 4: Polish & Cross-Cutting Concerns

**Purpose**: Code quality and final validation across the feature.

- [x] T027 [P] Run `./gradlew ktlintCheck` and fix all violations across all source files
- [x] T028 Run `./gradlew test` — verify all JUnit 5 unit tests pass (GetPrioritizedTasksUseCaseTest, TaskMapperTest, TaskRepositoryImplTest, TaskListViewModelTest)
- [x] T029 Run `./gradlew connectedAndroidTest` — verify all Compose UI tests pass (TaskListScreenTest)

---

## Dependencies & Execution Order

### Phase Dependencies

- **Setup (Phase 1)**: No dependencies — start immediately
- **Foundational (Phase 2)**: Depends on Phase 1 completion — BLOCKS User Story 1
- **User Story 1 (Phase 3)**: Depends on Phase 2 completion
  - Tests (T014–T019): Can run in parallel after Phase 2
  - Implementation (T020–T026): Sequential within story; start after tests are confirmed failing
- **Polish (Phase 4)**: Depends on all Phase 3 tasks complete

### Within User Story 1

- All test tasks (T014–T018) can run in parallel (different files)
- T019 depends on T018 (Robot must exist before test file that uses it)
- T020 and T021 can run in parallel (different files)
- T022 depends on T020 (mapper) and T011 (interface from foundational)
- T023 can run in parallel with T020/T021
- T024 depends on T021, T022, T023
- T025 depends on T024
- T026 depends on T025

### Parallel Opportunities

```bash
# Phase 2 — run together:
Task: "Create TaskPriorityApp.kt"                     # T004
Task: "Create Task.kt domain model"                   # T005
Task: "Create TaskEntity.kt"                          # T006
Task: "Create Theme.kt"                               # T007

# Phase 3 tests — run together:
Task: "Write failing GetPrioritizedTasksUseCaseTest"  # T014
Task: "Write failing TaskMapperTest"                  # T015
Task: "Write failing TaskRepositoryImplTest"          # T016
Task: "Write failing TaskListViewModelTest"           # T017
Task: "Create TaskListRobot"                          # T018

# Phase 3 implementation — run together:
Task: "Implement TaskMapper"                          # T020
Task: "Implement GetPrioritizedTasksUseCase"          # T021
Task: "Create TaskListUiState"                        # T023
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
| Phase 2: Foundational | 10 | T004, T005, T006, T007 |
| Phase 3: US1 Tests | 6 | T014, T015, T016, T017, T018 |
| Phase 3: US1 Impl | 7 | T020, T021, T023 |
| Phase 4: Polish | 3 | T027 |
| **Total** | **29** | |

---

## Notes

- [P] tasks = different files, no dependencies — safe to parallelize
- [US1] label maps every task to User Story 1 for traceability
- Tests MUST fail before implementation — this is non-negotiable per constitution
- `TaskListRobot` encapsulates ALL UI interactions; `TaskListScreenTest` contains ONLY robot calls and test case structure
- `TaskRepositoryImplTest` MUST use Room in-memory database — do NOT mock Room internals
- `TaskListViewModelTest` MUST mock `TaskRepository` interface, NOT `TaskRepositoryImpl`
- Commit after each phase checkpoint
