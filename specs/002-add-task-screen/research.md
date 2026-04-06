# Research: Add Task Screen

**Branch**: `002-add-task-screen` | **Date**: 2026-04-05 | **Plan**: [plan.md](plan.md)

## Navigation Library

**Decision**: Use **Navigation 3** (`androidx.navigation3`) version **1.0.1** (latest stable)
**Artifacts**:
- `androidx.navigation3:navigation3-runtime:1.0.1`
- `androidx.navigation3:navigation3-ui:1.0.1`

**Rationale**: User requirement. Navigation 3 is a ground-up redesign of Jetpack Navigation with first-class Compose support, type-safe destinations via `@Serializable` classes, and a developer-owned back stack — eliminating the opaque `NavController`. Compatible with Kotlin 2.1.0 and Compose BOM 2024.12.01.

**Alternatives considered**: Navigation 2 (`androidx.navigation:navigation-compose 2.8.x`) — rejected per user preference for Navigation 3.

## kotlinx.serialization

**Decision**: `org.jetbrains.kotlinx:kotlinx-serialization-json:1.8.0` + plugin `org.jetbrains.kotlin.plugin.serialization:2.1.0`
**Rationale**: 1.8.0 is the latest stable release explicitly targeting Kotlin 2.1.0. Plugin version must match Kotlin version exactly.
**Alternatives considered**: 1.7.x — targets older Kotlin; rejected.

## Navigation 3 Architecture

Navigation 3 replaces `NavController` / `NavHost` with:

| Concept | Navigation 2 | Navigation 3 |
|---------|-------------|-------------|
| Back stack | Library-managed | Developer-owned `SnapshotStateList<Any>` |
| Container | `NavHost` | `NavDisplay` |
| Navigation | `navController.navigate(route)` | `backStack.add(Destination)` |
| Pop | `navController.popBackStack()` | `backStack.removeLastOrNull()` |
| Content mapping | `composable { }` blocks | `entryProvider` lambda: key → `NavEntry` |
| System back | Handled by library | `onBack = { backStack.removeLastOrNull() }` |

**Entry point**: `NavDisplay(backStack, onBack, entryProvider)` in `MainActivity`.

## Type-Safe Destinations

`@Serializable` Kotlin objects/classes serve as destination keys. No string routes.

```kotlin
@Serializable data object TaskList
@Serializable data object AddTask
```

The back stack holds these objects: `mutableStateListOf<Any>(TaskList)`.
`NavDisplay` maps each key to a `NavEntry` via the `entryProvider` lambda using a `when` expression.

## Hilt ViewModel Scoping with Navigation 3

**Decision**: Use `hiltViewModel()` inside each `NavEntry` content block.
**Rationale**: Each `NavEntry` provides its own `ViewModelStoreOwner` via `LocalNavEntryOwner`. `hiltViewModel()` from `hilt-navigation-compose 1.2.0` resolves the store from `LocalViewModelStoreOwner`, which Navigation 3's `NavEntry` correctly sets. ViewModels are therefore scoped per entry — equivalent to Navigation 2's per-back-stack-entry scoping.
**Note**: The Hilt 2.53 + Kotlin 2.1.0 combination is already proven working in this project (feature 001 passes all tests). No action required.

## Discard Dialog

**Decision**: `BackHandler` from `androidx.activity.compose` + Material3 `AlertDialog`
**Rationale**: `BackHandler` intercepts both system back gesture and hardware back. `activity-compose` is already declared. `AlertDialog` is available via existing `material3` dep. ViewModel tracks `isDirty: Boolean` to conditionally enable the handler.

## Room Insert

**Decision**: `@Insert suspend fun insertTask(entity: TaskEntity)` in DAO; `suspend fun addTask(task: Task)` in `TaskRepository`; `viewModelScope.launch { }` in `AddTaskViewModel`.
**Rationale**: `room-ktx` (already declared) provides coroutine extensions. No new dependencies needed.

## Auto-Focus & Keyboard

**Decision**: `FocusRequester` + `LaunchedEffect(Unit)` + `LocalSoftwareKeyboardController`
**Rationale**: Idiomatic Compose pattern; covered entirely by existing `activity-compose` dep.

## Dependency Changes

| Change | File | Detail |
|--------|------|--------|
| Add `navigation3` version | `gradle/libs.versions.toml` | `navigation3 = "1.0.1"` |
| Add `kotlinxSerialization` version | `gradle/libs.versions.toml` | `kotlinxSerialization = "1.8.0"` |
| Add `navigation3-runtime` library | `gradle/libs.versions.toml` | alias `navigation3-runtime` |
| Add `navigation3-ui` library | `gradle/libs.versions.toml` | alias `navigation3-ui` |
| Add `kotlinx-serialization-json` library | `gradle/libs.versions.toml` | alias `kotlinx-serialization-json` |
| Add serialization plugin | `gradle/libs.versions.toml` | `kotlinx-serialization` plugin alias |
| Apply serialization plugin | `app/build.gradle.kts` | `alias(libs.plugins.kotlinx.serialization)` |
| Add runtime dep | `app/build.gradle.kts` | `implementation(libs.navigation3.runtime)` |
| Add UI dep | `app/build.gradle.kts` | `implementation(libs.navigation3.ui)` |
| Add serialization dep | `app/build.gradle.kts` | `implementation(libs.kotlinx.serialization.json)` |
