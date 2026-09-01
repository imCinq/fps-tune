# FPS Tune architecture

## Scope

FPS Tune is a client-only Fabric mod. The runtime path is intentionally limited to optional local rendering workloads and render scheduling. It does not add networking, telemetry, update checks, gameplay automation, or server-side behavior.

## Runtime components

| Component | Responsibility |
| --- | --- |
| `FPSTuneClient` | Registers the client entrypoint, local toggle, configuration lifecycle, and target-specific Adaptive FPS-cap resolution. |
| `FPSTuneConfig` | Defines the master switch, module switches, configuration version, and particle-policy defaults. |
| `ConfigStore` | Loads, validates, and atomically persists local configuration. |
| `FPSTuneRenderPolicy` | Applies the master switch and independent controller gates without touching Minecraft state. |
| `ParticleAdmissionBudget` | Applies the total per-tick limit and optional nearby-particle reserve, and provides an immutable per-tick runtime snapshot for the particle mixin. |
| `ParticleAdmissionMetrics` | Holds non-persistent, client-thread current-tick admission counters for diagnostics. |
| `AdaptiveParticleBudgetController` | Adjusts the total particle budget from recent local frame times and particle pressure with bounded steps, hysteresis, and cooldowns. |
| `ParticleEngineMixin` | Connects the particle controller to the client particle engine at the version-checked injection points. |
| `LevelRendererMixin` / `WeatherEffectRendererMixin` | Connects the weather controller to the version-specific precipitation boundary: `LevelRenderer.renderSnowAndRain` on 1.21.1 and `WeatherEffectRenderer.render` on 1.21.11/26.2, without suppressing world-border geometry. |
| `ModMenuIntegration` | Exposes the optional in-game configuration entrypoint without adding runtime behavior outside the client. |
| `FPSTuneConfigScreen` | Provides the simple profile-first settings screen, edits a copied configuration, and persists it only after an explicit Done action. |
| `FPSTuneAdvancedConfigScreen` | Provides optional individual particle and automatic-adjustment controls without crowding the main settings screen. |
| `FPSTuneHud` | Renders the optional local diagnostics overlay through the target version's Fabric HUD API. |

## Versioned build layout

The configuration, policy, admission-budget classes, and API-compatible Minecraft classes live in `src/main/java` and are shared by every target. Minecraft-facing code that cannot be compiled safely across mapping eras lives under `src/<minecraft-version>/java`; 1.21.1 provides the older string-category keybinding, HUD callback, and weather bridge, 1.21.11 provides its older keybinding/chat bridge and MultiBufferSource weather bridge, and 26.2 uses the newer client APIs and weather-state bridge. Each target also owns its `fabric.mod.json` and `fpstune.mixins.json` under `src/<minecraft-version>/resources`.

Adaptive frame-time sampling and FPS-cap resolution stay in the target-specific `FPSTuneHud`/`FPSTuneClient` bridges because the HUD callbacks, drawing types, and client option mappings differ between supported Minecraft targets. The common adaptive controller receives only monotonic frame intervals, the resolved target, and pressure data; it never depends on Minecraft internals. A future patch that changes the HUD API should therefore require a narrow target-bridge update and target build, not a new renderer mixin.

`gradle/versions/<minecraft-version>.properties` is the build profile source of truth. The selected `mc_target` chooses the Minecraft dependency, loader, Fabric API, Java release level, Loom plugin, source directory, resource directory, and artifact suffix. A release therefore contains separate JARs; a player must install the JAR matching their Minecraft version.

## Runtime flow

1. Fabric invokes `FPSTuneClient` on the client.
2. FPS Tune loads local configuration and initializes the render policies.
3. At `ParticleEngine.tick` head, the particle mixin captures one client-thread admission snapshot. Each `add` call reuses that state, records lightweight pressure attempts for Adaptive mode, rejects particles immediately after the total budget is full, classifies nearby particles only when capacity remains, and updates detailed current-tick counters only when diagnostics are enabled.
4. The weather mixin gates only the version-specific precipitation renderer (`LevelRenderer.renderSnowAndRain` on 1.21.1; `WeatherEffectRenderer.render` on 1.21.11/26.2), so surrounding effects such as world-border rendering continue.
5. The target-specific HUD bridge resolves the configured or Auto FPS target, samples local render intervals and the current-tick pressure snapshot for Adaptive mode, then reads the detailed counters for the optional diagnostics overlay.
6. Mod Menu, when installed, can open a simple profile-first settings screen and an optional Advanced settings screen; both edit a draft copy and save only on confirmation from the main screen.
7. Enabled controllers apply deterministic local limits; disabled controllers pass through vanilla behavior.
8. Configuration changes are saved locally and apply to later client ticks and render passes.

## Configuration and persistence

Configuration lives in `config/fpstune.properties`. `ConfigStore` writes `configVersion` and treats missing or legacy module settings as safe defaults. During the project rename, it reads an existing `config/coretune.properties` only when the new file is absent, copies the validated values to the new path, and leaves the original untouched. It treats malformed values as recoverable input, applies validated defaults, and uses an atomic replacement strategy for writes. Runtime configuration is local to the Minecraft instance.

## Optional Mod Menu integration

Mod Menu is an optional client-side integration. The target metadata also provides the long-form, plain-language overview shown in the selected mod's details pane, with explicit sections for behavior, boundaries, setup, and the visual trade-off. Its entrypoint opens `FPSTuneConfigScreen`, which keeps the first screen focused on a master switch, plain-language performance profiles, and a few independent visual options. `FPSTuneAdvancedConfigScreen` exposes the individual particle and Adaptive controls only when requested, and dims controls whose parent feature is disabled. Both screens use native widgets, keep edits in a copied draft, and do not introduce networking, telemetry, automation, gameplay behavior, or server interaction.

## Mixin boundary

The mixins are client-only and explicitly listed in the selected target's `src/<minecraft-version>/resources/fpstune.mixins.json`. Their targets are version-sensitive. Before changing them, inspect the target Minecraft bytecode and verify the exact shape of `ParticleEngine.add` and `ParticleEngine.tick`, plus `LevelRenderer.renderSnowAndRain` on 1.21.1 or `WeatherEffectRenderer.render` reached from `LevelRenderer.addWeatherPass` on 1.21.11/26.2. Prefer narrow head cancellation at the precipitation method and explicit admission accounting over broad redirects or ordinal-only assumptions. A controller must not stall worker queues or alter world simulation.

## Packaging

The Fabric metadata, icon, language resource, mixin configuration, compiled classes, and tests are built from the Gradle project. The selected profile in `gradle/versions/` and its target metadata are the machine-readable compatibility sources.

## Change guidance

Keep each change close to the component it affects. Runtime behavior changes should update deterministic tests and, when the mixin boundary changes, the bytecode and graphical verification evidence described in `docs/TESTING.md`.
