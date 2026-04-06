# Implementation Plan: Add Task Screen

**Branch**: `002-add-task-screen` | **Date**: 2026-04-05 | **Spec**: [spec.md](spec.md)
**Input**: Feature specification from `/specs/002-add-task-screen/spec.md`

## Summary

Add a dedicated Add Task screen to the existing Android task priority list app. Navigation is handled by Jetpack Navigation 3 (`NavDisplay` + developer-owned back stack) with `@Serializable` Kotlin objects as type-safe destinations. The screen is reached via a FAB on the task list. It presents an auto-focused title field, optional description (capped at 140 characters), Important/Urgent toggles, and Save/Cancel actions. Navigating away with modified input shows a "Discard changes?" dialog. Saving inserts via a new `AddTaskUseCase` → `TaskRepository.addTask()` → `TaskDao.insertTask()` (suspend). The ViewModel exposes `AddTaskUiState` for all form and navigation state; navigation back is triggered by `shouldNavigateBack: Boolean` observed directly in the composable — no events flow.

## Technical Context

**Language/Version**: Kotlin 2.1.0
**Primary Dependencies**: Jetpack Compose (Material3), Room 2.6.1, Hilt 2.53, Navigation 3 (`navigation3-runtime` + `navigation3-ui` 1.0.1), kotlinx-serialization-json 1.8.0, hilt-navigation-compose 1.2.0, Coroutines 1.9.0, ktlint
**Storage**: Room (SQLite, local device only)
**Testing**: JUnit 5 via android-junit5 plugin (unit), Jetpack Compose Testing + Robots pattern (UI)
**Target Platform**: Android (phone-sized screens, minSdk 26)
**Project Type**: Mobile app
**Performance Goals**: Room insert < 1ms — navigate back immediately, no loading indicator
**Constraints**: Offline-only; no network, no auth, single user
**Scale/Scope**: One new screen added to existing single-screen app; ≤50 tasks

## Constitution Check

*GATE: Must pass before Phase 0 research. Re-check after Phase 1 design.*

| Gate | Status | Notes |
|------|--------|-------|
| Constitution defined | ✅ Pass | Ratified v1.0.1 on 2026-04-04 |
| Spec exists (Principle I) | ✅ Pass | `specs/002-add-task-screen/spec.md` |
| Single user story scoped (Principle III) | ✅ Pass | US1 only — add a new task |
| No unresolved clarifications | ✅ Pass | All 5 clarification questions answered |
| Three-layer architecture maintained (Principle IV) | ✅ Pass | New files placed in correct presentation/domain/data packages; `AddTaskUiState` and `TitleValidationError` in presentation; `AddTaskUseCase` in domain |
| Technology stack matches standards | ✅ Pass | Kotlin, Compose, Room, Hilt, Coroutines, ktlint. Navigation 3 is an additive dependency not covered by the constitution's locked list — no amendment required. |
| Test layers all present (Testing Standards) | ✅ Pass | JUnit 5 unit tests, Compose Testing + Robots, ViewModel tests, Repository tests |

## Project Structure

### Documentation (this feature)

```text
specs/002-add-task-screen/
├── plan.md              # This file
├── research.md          # Phase 0 output
├── data-model.md        # Phase 1 output
├── quickstart.md        # Phase 1 output
├── contracts/
│   └── add-task-screen.md   # Phase 1 output
└── tasks.md             # Phase 2 output (/speckit-tasks — NOT created here)
```

### Source Code (repository root)

```text
app/
├── src/
│   ├── main/
│   │   ├── kotlin/com/example/taskprioritylist/
│   │   │   ├── MainActivity.kt                          # updated: host AppNavGraph()
│   │   │   ├── TaskPriorityApp.kt                       # unchanged
│   │   │   ├── presentation/
│   │   │   │   ├── navigation/
│   │   │   │   │   ├── AppDestinations.kt               # NEW: @Serializable destination objects
│   │   │   │   │   └── AppNavGraph.kt                   # NEW: NavDisplay + entryProvider
│   │   │   │   ├── tasklist/
│   │   │   │   │   ├── TaskListScreen.kt                # updated: add onAddTask param + FAB
│   │   │   │   │   ├── TaskItem.kt                      # unchanged
│   │   │   │   │   ├── TaskListViewModel.kt             # unchanged
│   │   │   │   │   ├── TaskListUiState.kt               # unchanged
│   │   │   │   │   ├── TaskListSemantics.kt             # unchanged
│   │   │   │   │   └── TaskListTestTags.kt              # updated: add FAB test tag constant
│   │   │   │   ├── addtask/
│   │   │   │   │   ├── AddTaskScreen.kt                 # NEW: form composable + stateless content + preview
│   │   │   │   │   ├── AddTaskViewModel.kt              # NEW: @HiltViewModel
│   │   │   │   │   ├── AddTaskUiState.kt                # NEW: form state data class
│   │   │   │   │   ├── TitleValidationError.kt          # NEW: enum for title validation errors
│   │   │   │   │   └── AddTaskTestTags.kt               # NEW: Compose test tag constants
│   │   │   │   └── theme/
│   │   │   │       └── Theme.kt                         # unchanged
│   │   │   ├── domain/
│   │   │   │   ├── model/
│   │   │   │   │   └── Task.kt                          # unchanged
│   │   │   │   ├── repository/
│   │   │   │   │   └── TaskRepository.kt                # updated: add addTask()
│   │   │   │   └── usecase/
│   │   │   │       ├── GetPrioritizedTasksUseCase.kt    # unchanged
│   │   │   │       └── AddTaskUseCase.kt                # NEW
│   │   │   └── data/
│   │   │       ├── local/
│   │   │       │   ├── TaskDatabase.kt                  # unchanged
│   │   │       │   ├── TaskDao.kt                       # updated: add insertTask()
│   │   │       │   └── TaskEntity.kt                    # unchanged
│   │   │       ├── mapper/
│   │   │       │   └── TaskMapper.kt                    # unchanged
│   │   │       ├── repository/
│   │   │       │   └── TaskRepositoryImpl.kt            # updated: implement addTask()
│   │   │       └── di/
│   │   │           └── DatabaseModule.kt                # unchanged
│   │   └── AndroidManifest.xml                          # unchanged
│   ├── test/
│   │   └── kotlin/com/example/taskprioritylist/
│   │       ├── presentation/tasklist/
│   │       │   └── TaskListViewModelTest.kt             # unchanged
│   │       ├── presentation/addtask/
│   │       │   └── AddTaskViewModelTest.kt              # NEW
│   │       ├── domain/usecase/
│   │       │   ├── GetPrioritizedTasksUseCaseTest.kt    # unchanged
│   │       │   └── AddTaskUseCaseTest.kt                # NEW
│   │       └── data/
│   │           ├── mapper/
│   │           │   └── TaskMapperTest.kt                # unchanged
│   │           └── repository/
│   │               └── TaskRepositoryImplTest.kt        # updated: add insert test cases
│   └── androidTest/
│       └── kotlin/com/example/taskprioritylist/
│           ├── HiltTestRunner.kt                        # unchanged
│           ├── di/
│           │   └── TestDatabaseModule.kt                # unchanged
│           ├── fake/
│           │   └── FakeTaskRepository.kt                # updated: add addTask() stub
│           └── presentation/
│               ├── tasklist/
│               │   ├── robots/
│               │   │   └── TaskListRobot.kt             # updated: add assertFabVisible()
│               │   └── TaskListScreenTest.kt            # updated: add FAB visibility test
│               └── addtask/
│                   ├── robots/
│                   │   └── AddTaskRobot.kt              # NEW
│                   └── AddTaskScreenTest.kt             # NEW
├── build.gradle.kts                                     # updated: add deps + serialization plugin
gradle/
└── libs.versions.toml                                   # updated: add navigation3, serialization
```

**Structure Decision**: Single Android project, clean architecture unchanged. Navigation 3 lives in a new `presentation/navigation/` package. Add Task presentation files live in `presentation/addtask/`.

## Complexity Tracking

> **Fill ONLY if Constitution Check has violations that must be justified**

No violations — all principles satisfied.
