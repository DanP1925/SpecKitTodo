# Quickstart: Add Task Screen

**Branch**: `002-add-task-screen` | **Date**: 2026-04-05

## Prerequisites

- Android Studio Ladybug or later
- JDK 17+
- Android device or emulator (API 26+)

## Build

```bash
./gradlew assembleDebug
```

## Run Unit Tests

```bash
./gradlew test
```

Covers: `AddTaskUseCaseTest`, `AddTaskViewModelTest`, `TaskMapperTest` (existing), `TaskRepositoryImplTest` (extended with insert).

## Run UI Tests

```bash
./gradlew connectedAndroidTest
```

Covers: `AddTaskScreenTest` (new), `TaskListScreenTest` (existing — verifies FAB present).

## Run Lint

```bash
./gradlew ktlintCheck
```

Fix violations:

```bash
./gradlew ktlintFormat
```

## Key New Files

| File | Purpose |
|------|---------|
| `presentation/navigation/AppDestinations.kt` | `@Serializable` destination objects |
| `presentation/navigation/AppNavGraph.kt` | `NavDisplay` + `entryProvider` |
| `presentation/addtask/AddTaskScreen.kt` | Form composable |
| `presentation/addtask/AddTaskViewModel.kt` | Form ViewModel |
| `presentation/addtask/AddTaskUiState.kt` | Form state data class |
| `presentation/addtask/AddTaskEvent.kt` | Sealed interface for one-shot ViewModel events |
| `presentation/addtask/AddTaskTestTags.kt` | Compose test tag constants |
| `domain/usecase/AddTaskUseCase.kt` | Insert use case |
| `androidTest/.../addtask/robots/AddTaskRobot.kt` | UI test robot |
| `androidTest/.../addtask/AddTaskScreenTest.kt` | UI test cases |

## Key Modified Files

| File | Change |
|------|--------|
| `data/local/TaskDao.kt` | Add `insertTask(entity)` |
| `domain/repository/TaskRepository.kt` | Add `addTask(task)` |
| `data/repository/TaskRepositoryImpl.kt` | Implement `addTask` |
| `presentation/tasklist/TaskListScreen.kt` | Add `onAddTask` param + FAB |
| `MainActivity.kt` | Host `AppNavGraph()` instead of `TaskListScreen()` |
| `gradle/libs.versions.toml` | Add navigation3, kotlinx-serialization versions + aliases |
| `app/build.gradle.kts` | Add navigation3, serialization dependencies + plugin |
| `androidTest/.../fake/FakeTaskRepository.kt` | Add `addTask` stub |
