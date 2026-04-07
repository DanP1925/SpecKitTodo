<!--
## Sync Impact Report

**Version Change**: 1.0.1 → 1.0.1 (no change)
**Bump Rationale**: No amendments. Validation run on 2026-04-08 confirmed constitution
is fully populated, all templates are consistent, and no placeholders remain.

### Modified Principles
- None

### Added Sections
- None

### Removed Sections
- None

### Templates Requiring Updates
- `.specify/templates/plan-template.md` ✅ — Constitution Check gate and Complexity
  Tracking table present; fully consistent with Principles I–V.
- `.specify/templates/spec-template.md` ✅ — User stories with priorities, acceptance
  scenarios, requirements, and success criteria align with Principles I and III.
- `.specify/templates/tasks-template.md` ✅ — TDD-first language enforced; test tasks
  precede implementation tasks per Principle II.
- `.specify/templates/agent-file-template.md` ✅ — Generic template; no
  constitution-specific alignment required.
- `.specify/templates/checklist-template.md` ✅ — Generic template; no
  constitution-specific alignment required.

### Deferred TODOs
- None.
-->

# SpecKit Spike Constitution

## Core Principles

### I. Spec-First Development

All features MUST begin with a completed specification (`spec.md`) before any planning or
implementation work starts. The spec MUST define prioritized user stories with acceptance
scenarios, functional requirements, and measurable success criteria. No implementation
or planning work may begin without a ratified spec.

**Rationale**: Prevents scope creep and misalignment by anchoring every technical decision
to agreed-upon user outcomes before any code is written.

### II. Test-First Development (NON-NEGOTIABLE)

TDD is mandatory. Tests MUST be written and verified to fail before any implementation
code is produced. The Red-Green-Refactor cycle MUST be enforced for all layers:
use cases, ViewModels, repositories, and UI. Skipping this cycle requires explicit
documented justification in the plan's Complexity Tracking table.

**Rationale**: Failing tests before implementation prove the test exercises the intended
behavior, preventing false-positive test suites and production regressions.

### III. Independent User Story Delivery

Each user story MUST be independently testable, deployable, and demonstrable as a viable
MVP increment without requiring other stories to be complete. Stories MUST be prioritized
P1 → Pn and delivered in that order, with an explicit checkpoint validation after each.

**Rationale**: Enables incremental value delivery and reduces integration risk by ensuring
each increment can be verified in isolation.

### IV. Clean Architecture (Three-Layer)

All source code MUST be organized across exactly three packages: `presentation`, `domain`,
and `data`. Dependency direction MUST flow inward only: `presentation` → `domain` ←
`data`. The `domain` layer MUST contain no Android framework dependencies. ViewModels
MUST reside in `presentation`; repository interfaces and use cases in `domain`;
Room entities, DAOs, and repository implementations in `data`.

**Rationale**: Enforces separation of concerns, makes the domain layer independently unit-
testable without Android instrumentation, and prevents framework lock-in at the business
logic layer.

### V. Simplicity (YAGNI)

Features MUST be implemented as the minimum viable solution for the current requirement.
Abstractions, helpers, and patterns are only permitted when concretely needed by two or
more existing use cases. Complexity violations MUST be documented in the plan's Complexity
Tracking table with rationale and rejected simpler alternatives.

**Rationale**: Premature abstraction creates maintenance burden without current value.
Every layer of complexity must justify its existence against the current spec.

## Technology Standards

The following technology choices are locked for this project. Deviations require a
constitution amendment.

- **Language**: Kotlin 2.x (latest stable)
- **UI**: Jetpack Compose with Material3
- **Architecture**: MVVM — ViewModels expose `StateFlow`; Composables collect state
- **Storage**: Room (SQLite, local device only — no cloud sync)
- **Dependency Injection**: Hilt (`@HiltAndroidApp`, `@AndroidEntryPoint`, `@HiltViewModel`)
- **Async**: Kotlin Coroutines + `Flow`; no RxJava, no `LiveData` for new code
- **Code Quality**: ktlint MUST pass on all committed code; no suppressions without comment
- **Min SDK**: 26

## Testing Standards

All test layers are MANDATORY for every feature. Tests MUST be written before implementation
(see Principle II).

- **Unit tests** (`test/`): JUnit 5 via the `android-junit5` Gradle plugin. Required for:
  use cases, ViewModels, repository implementations, and mappers.
- **UI tests** (`androidTest/`): Jetpack Compose Testing library. MUST follow the
  Robots pattern — a `robots/` subdirectory per screen containing a Robot class that
  encapsulates all UI interactions and assertions. Test files delegate entirely to robots.
- **ViewModel tests**: MUST mock the repository interface and verify `StateFlow` emissions
  for all defined UI states.
- **Repository tests**: MUST use a Room in-memory database to verify DAO queries and
  mapper round-trips without mocking Room internals.

## Governance

This constitution supersedes all other development practices and guidelines within this
project. All PRs and code reviews MUST verify compliance with active principles before
approval. Exceptions MUST be justified in the relevant plan's Complexity Tracking table.

**Amendment Procedure**: Amendments are executed via the `/speckit-constitution` command.
Each amendment MUST: (a) increment the version per the policy below, (b) update the
`Last Amended` date to the amendment date, (c) produce a Sync Impact Report, and
(d) propagate structural changes to dependent templates and the active plan's
Constitution Check section.

**Versioning Policy**:
- MAJOR: Backward-incompatible change — principle removal, redefinition, or mandatory
  workflow restructuring.
- MINOR: Additive change — new principle, new section, or materially expanded guidance.
- PATCH: Non-semantic refinement — wording clarification, typo fix, formatting.

**Compliance Review**: Constitution compliance MUST be verified at the Constitution Check
gate in each `plan.md` before Phase 0 research and re-verified after Phase 1 design.

**Version**: 1.0.1 | **Ratified**: 2026-04-04 | **Last Amended**: 2026-04-04
