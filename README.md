# Task Priority List

An Android app for managing tasks using the **Eisenhower Matrix** — tasks are categorized by importance and urgency into four priority tiers.

> **Note:** This project is primarily a spike for evaluating [spec-kit](https://github.com/anthropics/claude-code) as a development workflow tool. The app itself serves as a realistic Android codebase to exercise spec-kit's capabilities across feature planning, implementation, code review, and testing.

## Features

- **Task list** — displays tasks sorted by priority tier, then alphabetically within each tier
- **Add task** — create tasks with a title, optional description, and importance/urgency toggles
- **Priority tiers** — Important & Urgent → Important → Urgent → No priority
- **Discard confirmation** — back navigation prompts a discard dialog when the form has unsaved changes

## Tech stack

| Category | Library | Version |
|---|---|---|
| Language | Kotlin | 2.1.0 |
| UI | Jetpack Compose (Material3) | BOM 2024.12.01 |
| Navigation | Navigation 3 | 1.0.1 |
| Dependency injection | Hilt | 2.53 |
| Database | Room | 2.6.1 |
| Coroutines | kotlinx-coroutines | 1.9.0 |
| Serialization | kotlinx-serialization-json | 1.8.0 |
| Lifecycle / ViewModel | Lifecycle | 2.10.0 |
| Min SDK | — | 26 (Android 8.0) |
| Compile SDK | — | 36 |

## Architecture

Clean Architecture with MVVM presentation layer:

```
domain/
  model/         — Task data model
  repository/    — Repository interface
  usecase/       — Business logic (prioritization, add task)

data/
  local/         — Room database, DAO, entities
  repository/    — Repository implementation
  mapper/        — Entity ↔ domain model mapping
  di/            — Hilt database module

presentation/
  tasklist/      — Task list screen, ViewModel, UI state
  addtask/       — Add task screen, ViewModel, UI state
  navigation/    — Navigation graph (Navigation 3)
  theme/         — Material3 theming
```

## Running the app

```bash
./gradlew installDebug
```

## Testing

```bash
# Unit tests
./gradlew testDebugUnitTest

# Instrumented tests (requires connected device or emulator)
./gradlew connectedAndroidTest

# Lint
./gradlew ktlintCheck

# Auto-fix lint
./gradlew ktlintFormat
```

## Project conventions

- ViewModels expose a single `uiState: StateFlow<UiState>` — no separate event streams
- Navigation is triggered from the composable via `snapshotFlow` observing a `shouldNavigateBack` flag in UiState
- Instrumented tests use the **Robot pattern** with a `FakeTaskRepository` injected via Hilt
- System under test variable in unit tests is always named `sut`
