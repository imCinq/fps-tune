# FPS Tune configuration

FPS Tune is disabled by default. Its settings control only local client rendering and are stored in the Minecraft instance, not on a server.

## Defaults

| Setting | Default | Meaning |
| --- | --- | --- |
| `enabled` | `false` | Master switch for FPS Tune's render controls. |
| `particleAdmissionEnabled` | `true` | Applies the per-tick particle admission budget while the master switch is enabled. |
| `maxParticlesPerTick` | `300` | Maximum number of particles admitted by the client particle engine in one client tick when limiting is active. |
| `prioritizeNearbyParticles` | `true` | Protects a reserved part of the existing particle budget for particles near the player. |
| `nearbyParticleReserve` | `100` | Configured upper bound for nearby admissions; the effective reserve is capped at half the current budget. |
| `nearbyParticleDistance` | `16` | Radius in blocks used to classify a particle as nearby. |
| `diagnosticsHudEnabled` | `false` | Shows local current-tick particle counters and controller status in the HUD. |
| `adaptiveParticleBudgetEnabled` | `false` | Adjusts the particle budget from recent local frame times when particle pressure is present. |
| `adaptiveTargetAuto` | `true` | Follows Minecraft's configured client FPS limit when Adaptive mode is enabled. |
| `adaptiveTargetFps` | `120` | Manual Adaptive target and fallback when Auto cannot read a finite client limit, clamped to `30..360`. |
| `adaptiveMinParticlesPerTick` | `100` | Lower bound for the Adaptive-mode budget, clamped to `0..10000`. |
| `adaptiveMaxParticlesPerTick` | `2000` | Upper bound for the Adaptive-mode budget, clamped to `0..10000`. |
| `weatherRenderingEnabled` | `true` | Keeps the vanilla weather render pass enabled while FPS Tune is active. |

The particle budget is clamped to `0..10000`. A value of `0` admits no new particles during a client tick; it does not remove existing particles. A value of `10000` is the highest accepted setting and is not an unlimited mode. The configured nearby reserve is clamped to `0..10000`, and its effective value is capped at half the current fixed or Adaptive budget using `min(configured reserve, floor(current budget / 2))`. At the default 300-particle budget, a configured reserve of 100 remains 100; at a 100-particle budget, it becomes 50. The nearby distance is clamped to `0..64` blocks. A zero reserve or a disabled nearby-priority switch restores the ordinary total-budget behavior. Adaptive minimum and maximum budgets use the same particle range, and the maximum is raised to the minimum when a malformed file reverses their order.

## In-game controls

With Mod Menu installed:

1. Open the Mods screen.
2. Select FPS Tune and choose Configure.
3. On the main screen, choose whether to enable FPS Tune, select a performance profile, and optionally show rain/snow or the performance overlay.
4. Open Advanced settings only if you need individual particle or Adaptive-budget controls; dependent options dim when their parent switch is off.
5. Choose Done on the main screen to apply and save the draft settings. Reset advanced settings only resets particle and Adaptive fields; it preserves the master switch, weather setting, and diagnostics choice in the draft.

Back or Escape from Advanced settings returns to the main screen with the draft changes. Cancel or Escape from the main screen discards the entire draft and leaves the active configuration unchanged. Mod Menu is optional; FPS Tune runs without it.

The default `F6` keybind toggles only the master `enabled` switch and saves that change immediately. It does not change the individual controller settings.

## Configuration file

The file is:

```text
config/fpstune.properties
```

A saved file has this shape:

```properties
configVersion=4
enabled=false
particleAdmissionEnabled=true
maxParticlesPerTick=300
prioritizeNearbyParticles=true
nearbyParticleReserve=100
nearbyParticleDistance=16
diagnosticsHudEnabled=false
adaptiveParticleBudgetEnabled=false
adaptiveTargetAuto=true
adaptiveTargetFps=120
adaptiveMinParticlesPerTick=100
adaptiveMaxParticlesPerTick=2000
weatherRenderingEnabled=true
```

The file is created when settings are saved. Writes use a temporary file and an atomic move when the filesystem supports it. A malformed or unreadable file falls back to safe defaults and must not prevent Minecraft from launching. Invalid boolean and integer values fall back to their previous defaults; particle budgets outside the allowed range are clamped.

## What the settings change

When `enabled=true` and `particleAdmissionEnabled=true`, FPS Tune counts successful particle admissions for the current client particle tick. Once the configured budget is reached, later particle additions for that tick are rejected by the local particle engine. This changes visual admission only; it does not change packets, world simulation, entity logic, or server state.

When `prioritizeNearbyParticles=true`, `nearbyParticleReserve` divides the current effective budget into two deterministic capacities. The effective reserve is capped at half that budget. General particles can consume the non-reserved portion. Particles whose bounding-box center is within `nearbyParticleDistance` blocks of the local player can use the reserved portion and any unused non-reserved capacity. The total number admitted still cannot exceed the current effective budget; this policy only changes which candidates are admitted first. If the nearby reserve fills up, additional nearby particles may still use remaining general capacity.

The nearby classification is calculated on the client from the local player position. It does not inspect server data, identify gameplay-important particles, or transmit any information. Disabling the priority switch or setting the reserve to zero keeps the limiter as a simple total admission budget.

When `diagnosticsHudEnabled=true`, the HUD shows whether FPS Tune is enabled, the current-tick admitted and rejected counts, the nearby admitted count and reserve, the current fixed/adaptive budget, and whether weather rendering is vanilla or suppressed. When the overlay is disabled, detailed admission counters are not updated in the particle hot path. It is hidden while a screen is open and is off by default. It does not show an active-particle count because the current controller intentionally limits admissions rather than owning particle lifetime/removal.

When `adaptiveParticleBudgetEnabled=true`, the controller starts at the fixed `maxParticlesPerTick` value clamped into the adaptive range. It tracks recent in-world render intervals and lightweight particle-pressure signals locally. A smoothed frame time more than 10% above the target lowers the budget only after 15 consecutive observations with particle pressure: at least 75% of the current budget was attempted, or the total budget was rejected. If the smoothed frame time exceeds 2x the target while pressure is present for 3 consecutive observations, Adaptive performs one bounded 25% emergency reduction. Slow frames with little particle pressure hold the budget. A smoothed frame time below 85% of the target for 60 consecutive observations raises it by about 10%. A 30-frame cooldown follows an adjustment. The value always stays between the configured adaptive minimum and maximum. Intervals longer than 250 ms are ignored so opening a menu or switching away from the client does not cause a sudden budget collapse. Pressure tracking remains active for Adaptive mode even when detailed diagnostics are disabled. Auto follows Minecraft's configured FPS limit on each render sample; changing the limit resets only the timing baseline and preserves the current particle budget. The numeric target is retained as the fallback when that client limit is unavailable.

When `enabled=true` and `weatherRenderingEnabled=false`, FPS Tune skips the local weather render pass. Rain and snow still exist in the world and are still simulated; only their client-side rendering is suppressed.

## Renamed-build migration

If `config/fpstune.properties` does not exist and the older private build's `config/coretune.properties` is present, FPS Tune imports the compatible values into the new file. The old file is left untouched. This migration path exists only for upgrading that earlier build and is not part of the public mod identity.
