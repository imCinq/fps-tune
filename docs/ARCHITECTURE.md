# FPS Tune architecture

## Scope

FPS Tune is a client-only Fabric mod. The runtime path is intentionally limited to optional local rendering workloads and render scheduling. It does not add networking, telemetry, update checks, gameplay automation, or server-side behavior.

## Runtime components

| Component | Responsibility |
| --- | --- |
| `FPSTuneClient` | Registers the client entrypoint, local toggle, and configuration lifecycle. |
| `FPSTuneConfig` | Defines the master switch, module switches, configuration version, and particle budget defaults. |
| `ConfigStore` | Loads, validates, and atomically persists local configuration. |
| `FPSTuneRenderPolicy` | Applies the master switch and independent controller gates without touching Minecraft state. |
| `ParticleAdmissionBudget` | Applies the per-tick admission limit with deterministic boundaries and reset behavior. |
| `ParticleEngineMixin` | Connects the particle controller to the client particle engine at the version-checked injection points. |
| `LevelRendererMixin` | Connects the weather controller to the client weather render pass at its version-checked boundary. |
| `ModMenuIntegration` | Exposes the optional in-game configuration entrypoint without adding runtime behavior outside the client. |
| `FPSTuneConfigScreen` | Edits a copied configuration with native Minecraft widgets and persists it only after an explicit Done action. |

## Versioned build layout

The configuration, policy, admission-budget classes, and API-compatible Minecraft classes live in `src/main/java` and are shared by every target. Minecraft-facing code that cannot be compiled safely across mapping eras lives under `src/<minecraft-version>/java`; the 1.21.11 target currently provides its older keybinding/chat bridge, while 26.2 uses the newer client APIs. Each target also owns its `fabric.mod.json` and `fpstune.mixins.json` under `src/<minecraft-version>/resources`.

`gradle/versions/<minecraft-version>.properties` is the build profile source of truth. The selected `mc_target` chooses the Minecraft dependency, loader, Fabric API, Java release level, Loom plugin, source directory, resource directory, and artifact suffix. A release therefore contains separate JARs; a player must install the JAR matching their Minecraft version.

## Runtime flow

1. Fabric invokes `FPSTuneClient` on the client.
2. FPS Tune loads local configuration and initializes the render policies.
3. The particle mixin observes particle-engine admission on the client thread.
4. The weather mixin observes the client weather render pass.
5. Mod Menu, when installed, can open a native settings screen; the screen edits a draft copy and saves only on confirmation.
6. Enabled controllers apply deterministic local limits; disabled controllers pass through vanilla behavior.
7. Configuration changes are saved locally and apply to later client ticks and render passes.

## Configuration and persistence

Configuration lives in `config/fpstune.properties`. `ConfigStore` writes `configVersion` and treats missing or legacy module settings as safe defaults. During the project rename, it reads an existing `config/coretune.properties` only when the new file is absent, copies the validated values to the new path, and leaves the original untouched. It treats malformed values as recoverable input, applies validated defaults, and uses an atomic replacement strategy for writes. Runtime configuration is local to the Minecraft instance.

## Optional Mod Menu integration

Mod Menu is an optional client-side integration. Its entrypoint opens `FPSTuneConfigScreen`, which uses vanilla `Checkbox`, `CycleButton`, and `Button` widgets. FPS Tune does not require Mod Menu for startup, and the screen does not introduce networking, telemetry, automation, gameplay behavior, or server interaction.

## Mixin boundary

The mixins are client-only and explicitly listed in the selected target's `src/<minecraft-version>/resources/fpstune.mixins.json`. Their targets are version-sensitive. Before changing them, inspect the target Minecraft bytecode and verify the exact shape of `ParticleEngine.add`, `ParticleEngine.tick`, and `LevelRenderer.addWeatherPass`. Prefer narrow head cancellation and explicit admission accounting over broad redirects or ordinal-only assumptions. A controller must not stall worker queues or alter world simulation.

## Packaging

The Fabric metadata, icon, language resource, mixin configuration, compiled classes, and tests are built from the Gradle project. The selected profile in `gradle/versions/` and its target metadata are the machine-readable compatibility sources.

## Change guidance

Keep each change close to the component it affects. Runtime behavior changes should update deterministic tests and, when the mixin boundary changes, the bytecode and graphical verification evidence described in `docs/TESTING.md`.
