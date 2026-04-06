# Feature Specification: Add Task Screen

**Feature Branch**: `002-add-task-screen`
**Created**: 2026-04-05
**Status**: Ready for Implementation
**Input**: User description: "Add a different screen so users can add new tasks"

## User Scenarios & Testing *(mandatory)*

### User Story 1 - Add a New Task (Priority: P1)

A user taps a button on the task list screen and is taken to a dedicated screen where they
can fill in a task title, an optional description, and toggle whether the task is important
and/or urgent. After submitting, the task is saved and the user is returned to the task list,
where the new task appears in the correct priority position.

**Why this priority**: Adding tasks is the fundamental write operation of the app. Without it,
users must rely on pre-seeded data and cannot use the app for real personal productivity.

**Independent Test**: Can be fully tested by navigating to the Add Task screen, filling in
the form fields, tapping Save, and confirming the new task appears in the task list in the
correct priority tier.

**Acceptance Scenarios**:

1. **Given** the user is on the task list screen, **When** they tap the Add Task button,
   **Then** they are navigated to the Add Task screen.
2. **Given** the user is on the Add Task screen, **When** they enter a title and tap Save,
   **Then** the task is persisted and the user is navigated back to the task list.
3. **Given** the user is on the Add Task screen, **When** they tap Save without entering
   a title, **Then** a validation error is shown and the task is NOT saved.
4. **Given** the user saves a task with "Important" toggled on and "Urgent" toggled off,
   **When** returned to the task list, **Then** the new task appears in the Important-only
   tier (second tier).
5. **Given** the user is on the Add Task screen with no input entered, **When** they tap
   the back/cancel button, **Then** they are navigated back to the task list immediately
   with no dialog.
6. **Given** the user is on the Add Task screen with at least one field modified, **When**
   they tap the back/cancel button or use the system back gesture, **Then** a "Discard
   changes?" dialog is shown with "Discard" and "Keep Editing" actions.
7. **Given** the discard dialog is shown, **When** the user taps "Discard", **Then** they
   are navigated back to the task list and no task is saved.
8. **Given** the discard dialog is shown, **When** the user taps "Keep Editing", **Then**
   the dialog is dismissed and the form remains open with all input intact.

### Edge Cases

- What if the title is only whitespace? Treat as empty — show validation error, do not save.
- What if the user navigates back via the system back gesture with unsaved input? Treated the same as tapping Cancel — show the "Discard changes?" confirmation dialog.
- What if a task with the same title already exists? Duplicate titles are allowed — save without error.
- What does "field modified" mean for the discard dialog trigger (`isDirty`)? `isDirty` is one-way: set to `true` the moment any field value changes for the first time, and never reset to `false` during the session. Toggling Important on and then back off still counts as dirty.

## Requirements *(mandatory)*

### Functional Requirements

- **FR-001**: The task list screen MUST display a Floating Action Button (FAB) that
  navigates the user to the Add Task screen.
- **FR-002**: The Add Task screen MUST contain: a required title text field (auto-focused
  on screen entry with keyboard open), an optional description text field, an "Important"
  toggle (default: off), an "Urgent" toggle (default: off), a Save button, and a
  Cancel/back navigation option.
- **FR-003**: Tapping Save with a blank or whitespace-only title MUST display an inline
  validation error message and prevent saving.
- **FR-004**: On successful save, the app MUST persist the task via Room and navigate back
  to the task list screen immediately — no loading indicator is shown during the insert.
- **FR-005**: The Add Task screen MUST NOT require the user to leave the app or open any
  external UI to complete task creation.
- **FR-007**: If the user attempts to leave the Add Task screen (back gesture or Cancel)
  with at least one field modified, the app MUST display a "Discard changes?" dialog with
  "Discard" and "Keep Editing" actions. If no fields have been modified, navigate back
  immediately without a dialog.
- **FR-006**: The `TaskDao` MUST expose an `insertTask(task: TaskEntity)` method so the
  Add Task flow can persist new tasks.

### Key Entities

- **Task** (existing): title (text, required), description (text, optional), isImportant
  (boolean), isUrgent (boolean), createdAt (timestamp). No new fields are added.
- **Navigation**: A Compose `NavHost` with two destinations: `taskList` (existing) and
  `addTask` (new).

## Success Criteria *(mandatory)*

### Measurable Outcomes

- **SC-001**: A task created via the Add Task screen appears in the task list in the correct
  priority tier immediately after saving (no app restart required). The list returns to its
  previous scroll position — it does NOT auto-scroll to the new task.
- **SC-002**: A task created via the Add Task screen persists after the app is fully closed
  and reopened.
- **SC-003**: 90% of first-time users can successfully add their first task without
  onboarding instructions.

## Clarifications

### Session 2026-04-05

- Q: Should the app allow saving a task with a title that already exists in the list? → A: Allow duplicates — titles do not need to be unique.
- Q: What should happen if the user presses back/Cancel mid-form with unsaved input? → A: Show a confirmation dialog ("Discard changes?" with Discard / Keep Editing).
- Q: After saving, where should the task list scroll position be? → A: Return to previous scroll position — no auto-scroll to the new task.
- Q: Should the Add Task screen show a loading indicator during save? → A: No — navigate back immediately, treat Room insert as instantaneous.
- Q: Should the keyboard auto-open on the Add Task screen? → A: Yes — title field is auto-focused and keyboard opens on screen entry.

## Assumptions

- Navigation uses Jetpack Navigation 3 (`androidx.navigation3:navigation3-runtime` +
  `navigation3-ui` 1.0.1) added to the existing dependency catalog.
- No editing of existing tasks is in scope — this feature covers creation only.
- The Add Task screen is modal-style (full screen), not a bottom sheet or dialog.
- `createdAt` is set automatically to `System.currentTimeMillis()` at save time; the
  user does not set it manually.
- No image, attachment, or due-date fields are in scope.
