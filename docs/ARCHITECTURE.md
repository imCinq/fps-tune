# CoreTune architecture

## Scope

CoreTune is a client-only Fabric mod. The runtime path is intentionally limited to local particle admission and rendering. It does not add networking, telemetry, update checks, gameplay automation, or server-side behavior.

## Runtime components

| Component | Responsibility |
| --- | --- |
| `CoreTuneClient` | Registers the client entrypoint, local toggle, and configuration lifecycle. |
| `CoreTuneConfig` | Defines the user-facing enable state and particle budget defaults. |
| `ConfigStore` | Loads, validates, and atomically persists local configuration. |
| `ParticleAdmissionBudget` | Applies the per-tick admission limit with deterministic boundaries and reset behavior. |
| `ParticleEngineMixin` | Connects the budget to the client particle engine at the version-checked injection points. |

## Runtime flow

1. Fabric invokes `CoreTuneClient` on the client.
2. CoreTune loads local configuration and initializes the admission budget.
3. The mixin observes particle-engine admission on the client thread.
4. The budget accepts or rejects new particles for the current tick.
5. Accepted particles continue through vanilla rendering; rejected particles do not enter the local queue.
6. Configuration changes are saved locally and apply to later client ticks.

## Configuration and persistence

Configuration lives in `config/coretune.properties`. `ConfigStore` treats malformed values as recoverable input, applies validated defaults, and uses an atomic replacement strategy for writes. Runtime configuration is local to the Minecraft instance.

## Mixin boundary

The mixin is client-only and explicitly listed in `src/main/resources/coretune.mixins.json`. Its target is version-sensitive. Before changing it, inspect the target Minecraft bytecode and verify the number and shape of queue insertion points. Prefer a narrow head cancellation and explicit queue-admission accounting over broad redirects or ordinal-only assumptions.

## Packaging

The Fabric metadata, icon, language resource, mixin configuration, compiled classes, and tests are built from the Gradle project. `gradle.properties` and `fabric.mod.json` remain the machine-readable compatibility sources.

## Change guidance

Keep each change close to the component it affects. Runtime behavior changes should update deterministic tests and, when the mixin boundary changes, the bytecode and graphical verification evidence described in `docs/TESTING.md`.
