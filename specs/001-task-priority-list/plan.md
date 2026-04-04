# Implementation Plan: Android Task Priority List

**Branch**: `001-task-priority-list` | **Date**: 2026-04-04 | **Spec**: [spec.md](spec.md)
**Input**: Feature specification from `/specs/001-task-priority-list/spec.md`

## Summary

Build an Android mobile app that displays a to-do list ordered by a four-tier priority
matrix derived from two boolean flags per task: "Important" and "Urgent". Tasks are sorted
tier-first (Important+Urgent → Important → Urgent → Neither), then alphabetically by title
within each tier. The app uses MVVM with clean architecture across three packages
(presentation, domain, data), Room for local persistence, Hilt for DI, Jetpack Compose
for UI, and Coroutines for async work.

## Technical Context

**Language/Version**: Kotlin 2.x (latest stable)
**Primary Dependencies**: Jetpack Compose (Material3), Room, Hilt, Coroutines, ktlint
**Storage**: Room (SQLite, local device only)
**Testing**: JUnit 5 via `android-junit5` plugin (unit), Jetpack Compose Testing (UI)
**Target Platform**: Android (phone-sized screens, minSdk 26)
**Project Type**: Mobile app
**Performance Goals**: Smooth scroll and instant re-sort with up to 50 tasks
**Constraints**: Offline-only; no network, no auth, single user
**Scale/Scope**: Single screen, single user, ≤50 tasks

## Constitution Check

*GATE: Must pass before Phase 0 research. Re-check after Phase 1 design.*

| Gate | Status | Notes |
|------|--------|-------|
| Constitution defined | ✅ Pass | Ratified v1.0.0 on 2026-04-04 |
| Spec exists (Principle I) | ✅ Pass | `specs/001-task-priority-list/spec.md` |
| Single user story scoped (Principle III) | ✅ Pass | US1 only — view prioritized list |
| No unresolved clarifications | ✅ Pass | All tech choices supplied by user |
| Three-layer architecture defined (Principle IV) | ✅ Pass | presentation / domain / data packages |
| Technology stack matches standards | ✅ Pass | Kotlin, Compose, Room, Hilt, Coroutines, ktlint |
| Test layers all present (Testing Standards) | ✅ Pass | JUnit 5, Compose Testing + Robots, ViewModel tests, Repository tests |

## Project Structure

### Documentation (this feature)

```text
specs/001-task-priority-list/
├── plan.md              # This file
├── research.md          # Phase 0 output
├── data-model.md        # Phase 1 output
├── quickstart.md        # Phase 1 output
├── contracts/           # Phase 1 output
│   └── task-list-screen.md
└── tasks.md             # Phase 2 output (/speckit-tasks — NOT created here)
```

### Source Code (repository root)

```text
app/
├── src/
│   ├── main/
│   │   ├── kotlin/com/example/taskprioritylist/
│   │   │   ├── MainActivity.kt
│   │   │   ├── TaskPriorityApp.kt              # @HiltAndroidApp Application
│   │   │   ├── presentation/
│   │   │   │   ├── tasklist/
│   │   │   │   │   ├── TaskListScreen.kt       # Composable — root screen
│   │   │   │   │   ├── TaskListViewModel.kt    # Hilt ViewModel, exposes StateFlow
│   │   │   │   │   └── TaskListUiState.kt      # Sealed UI state type
│   │   │   │   └── theme/
│   │   │   │       └── Theme.kt                # Material3 theme
│   │   │   ├── domain/
│   │   │   │   ├── model/
│   │   │   │   │   └── Task.kt                 # Pure domain model (no Android deps)
│   │   │   │   ├── repository/
│   │   │   │   │   └── TaskRepository.kt       # Interface
│   │   │   │   └── usecase/
│   │   │   │       └── GetPrioritizedTasksUseCase.kt  # Sorting logic
│   │   │   └── data/
│   │   │       ├── local/
│   │   │       │   ├── TaskDatabase.kt         # Room database
│   │   │       │   ├── TaskDao.kt              # DAO — returns Flow<List<TaskEntity>>
│   │   │       │   └── TaskEntity.kt           # Room-annotated entity
│   │   │       ├── mapper/
│   │   │       │   └── TaskMapper.kt           # TaskEntity ↔ Task
│   │   │       ├── repository/
│   │   │       │   └── TaskRepositoryImpl.kt   # Hilt-bound impl of TaskRepository
│   │   │       └── di/
│   │   │           └── DatabaseModule.kt       # @Module providing Room + DAO
│   │   └── AndroidManifest.xml
│   ├── test/                                   # JUnit 5 unit tests
│   │   └── kotlin/com/example/taskprioritylist/
│   │       ├── presentation/
│   │       │   └── tasklist/
│   │       │       └── TaskListViewModelTest.kt
│   │       ├── domain/
│   │       │   └── usecase/
│   │       │       └── GetPrioritizedTasksUseCaseTest.kt
│   │       └── data/
│   │           ├── mapper/
│   │           │   └── TaskMapperTest.kt
│   │           └── repository/
│   │               └── TaskRepositoryImplTest.kt
│   └── androidTest/                            # Compose UI tests (Robots pattern)
│       └── kotlin/com/example/taskprioritylist/
│           └── presentation/
│               └── tasklist/
│                   ├── robots/
│                   │   └── TaskListRobot.kt    # Encapsulates UI interactions & assertions
│                   └── TaskListScreenTest.kt   # Test cases — delegates to TaskListRobot
├── build.gradle.kts
└── proguard-rules.pro

build.gradle.kts   # project-level
settings.gradle.kts
```

**Structure Decision**: Single Android project (no backend/API needed — local storage only).
Three-package clean architecture as specified: `presentation` (Compose + ViewModel),
`domain` (pure Kotlin models, repository interface, use case), `data` (Room, mapper,
repository impl, Hilt DI module).

## Complexity Tracking

> **Fill ONLY if Constitution Check has violations that must be justified**

No violations — all principles satisfied.
