# Feature Specification: Android Task Priority List

**Feature Branch**: `001-task-priority-list`
**Created**: 2026-04-04
**Status**: Implemented
**Input**: User description: "Build an android application that shows a to do list. I want to have simple list that orders based on two fields: If the task is important or if the task is urgent"

## User Scenarios & Testing *(mandatory)*

### User Story 1 - View Prioritized Task List (Priority: P1)

A user opens the app and immediately sees all their tasks ordered by priority. Tasks that
are both important and urgent appear at the top. Tasks that are neither appear at the bottom.
The ordering helps users instantly know what to work on next without manual scanning.

**Why this priority**: This is the core value of the app — the prioritized view is what
differentiates it from a plain list and is the primary reason a user would open it.

**Independent Test**: Can be fully tested by launching the app with pre-seeded tasks that
have varying importance/urgency combinations and verifying they appear in the correct order.

**Acceptance Scenarios**:

1. **Given** the app has tasks with different importance/urgency combinations, **When** the
   user opens the app, **Then** tasks are displayed in descending priority order:
   (1) Important + Urgent → (2) Important only → (3) Urgent only → (4) Neither.
2. **Given** two tasks in the same priority tier, **When** displayed, **Then** they appear
   ordered alphabetically by title.
3. **Given** the task list is empty, **When** the user opens the app, **Then** an empty
   state message is shown prompting the user to add their first task.

### Edge Cases

- What happens if all tasks are in the same priority tier? Tasks display alphabetically by title.

## Requirements *(mandatory)*

### Functional Requirements

- **FR-001**: The app MUST display all tasks in a single list ordered by priority tier:
  Important+Urgent first, then Important-only, then Urgent-only, then neither important
  nor urgent.
- **FR-002**: Each task MUST have two independent boolean flags: "Important" and "Urgent".
- **FR-003**: The app MUST persist all tasks across app restarts on the same device.
- **FR-004**: The app MUST display an empty-state message when no tasks exist.
- **FR-005**: Within the same priority tier, tasks MUST be ordered alphabetically by title.

### Key Entities

- **Task**: Represents a single to-do item. Attributes: title (text), description (text,
  optional), isImportant (boolean), isUrgent (boolean), createdAt (timestamp).

## Success Criteria *(mandatory)*

### Measurable Outcomes

- **SC-001**: All tasks remain visible after the app is fully closed and reopened.
- **SC-002**: 90% of first-time users can identify the highest-priority item in the list
  without any onboarding instructions.
- **SC-003**: The app functions correctly with at least 50 tasks in the list without
  noticeable lag during scrolling.

## Assumptions

- The app is a single-user, personal productivity tool — no account or login required.
- Task data is stored locally on the device only; no cloud sync or sharing is in scope.
- The four-tier priority ordering (Important+Urgent → Important → Urgent → Neither) is
  fixed and not user-configurable.
- Within the same priority tier, tasks display alphabetically by title.
- The app targets phone-sized Android screens; tablet layout optimization is out of scope.
