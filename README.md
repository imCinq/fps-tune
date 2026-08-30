# FPS Tune

<p align="center">
  <img src="src/main/resources/assets/fpstune/icon.png" alt="FPS Tune logo" width="192">
</p>

FPS Tune is a client-side Fabric frame-time stability toolkit for Minecraft 1.21.11 and 26.2. It applies opt-in controls to optional local rendering workloads when visual scenes become unusually heavy.

It is designed to protect the floor of the frame-time graph during particle storms and weather-heavy scenes, not to promise a universal FPS increase or replace broad rendering optimizers.

## At a glance

| Item | Value |
| --- | --- |
| Minecraft | 1.21.11 and 26.2; separate JAR per version |
| Loader | 1.21.11: 0.18.6+; 26.2: 0.19.3+ |
| Environment | Client only |
| Java | 21+ for 1.21.11; 25+ for 26.2 |
| Required dependency | Fabric API |
| Optional integration | Mod Menu |
| License | MIT |

## What it changes

- Limits local particle admission during a client tick when enabled.
- Can reserve part of that existing budget for particles near the player, so distant cosmetic particles are rejected first during a storm.
- Can skip the local rain/snow render pass when explicitly enabled.
- Can show an opt-in local diagnostics HUD with current-tick admission counters.
- Starts disabled and changes nothing until you opt in with `F6` or the settings screen.

## What it never changes

- No packets, custom networking, telemetry, analytics, update checker, or remote configuration.
- No movement, combat, inventory, targeting, rotation, click automation, or gameplay logic.
- No server simulation, world state, entity behavior, farms, or anti-cheat behavior.

FPS Tune cannot guarantee approval by any multiplayer server or anti-cheat. Check the server's current rules before enabling it.

## Install

1. Install either Minecraft 1.21.11 with Fabric Loader 0.18.6 or newer, or Minecraft 26.2 with Fabric Loader 0.19.3 or newer.
2. Install the Fabric API build matching that Minecraft version.
3. Download the matching JAR from the [GitHub Releases page](https://github.com/imCinq/fps-tune/releases):

   | Minecraft | Matching artifact |
   | --- | --- |
   | 1.21.11 | `fps-tune-mc1.21.11-<version>.jar` |
   | 26.2 | `fps-tune-<version>.jar` |

   Do not install both target JARs in one instance.
4. Put the JAR in the instance's `mods` folder and start Minecraft.
5. Press `F6` to toggle the master switch, or install the matching Mod Menu version and choose FPS Tune → Configure.

The default particle budget is 300 admitted particles per client tick. Nearby-particle prioritization reserves 100 of those admissions for particles within 16 blocks of the player. Weather rendering remains enabled unless you explicitly disable it in the settings, and the diagnostics HUD remains off by default.

## Configuration

Configuration is stored locally in `config/fpstune.properties`.

The complete setting reference, defaults, file format, migration behavior, and UI semantics are in [docs/CONFIGURATION.md](docs/CONFIGURATION.md).

| Setting | Default | Purpose |
| --- | --- | --- |
| `enabled` | `false` | Master switch; disabled by default. |
| `particleAdmissionEnabled` | `true` | Enables the particle budget while the master switch is active. |
| `maxParticlesPerTick` | `300` | Particle admissions allowed per client tick, clamped to `0..10000`. |
| `prioritizeNearbyParticles` | `true` | Protects nearby particles with a reserved part of the existing budget. |
| `nearbyParticleReserve` | `100` | Admissions reserved for nearby particles per client tick, capped by the total budget. |
| `nearbyParticleDistance` | `16` | Distance in blocks used to classify a particle as nearby. |
| `diagnosticsHudEnabled` | `false` | Shows local current-tick admission counters in the HUD. |
| `adaptiveParticleBudgetEnabled` | `false` | Slowly adjusts the particle budget toward the target FPS. |
| `adaptiveTargetFps` | `120` | Target FPS used by Adaptive mode, clamped to `30..360`. |
| `adaptiveMinParticlesPerTick` | `100` | Lowest Adaptive-mode particle budget. |
| `adaptiveMaxParticlesPerTick` | `2000` | Highest Adaptive-mode particle budget. |
| `weatherRenderingEnabled` | `true` | Keeps the vanilla weather render pass enabled. |

Mod Menu is optional. The FPS Tune details pane provides a long-form, plain-language overview of what it changes, what it leaves untouched, how to get started, and the intentional visual trade-off. The Configure screen uses performance profiles and keeps only the most useful visual options in view; individual particle and automatic-adjustment controls are available under Advanced settings. Done on the main screen saves settings; Back returns from Advanced settings, while Cancel and Escape on the main screen discard edits. `F6` toggles only the master switch and saves immediately.

Adaptive mode is off by default. When enabled, it starts from the fixed particle budget, lowers that budget after sustained slow frames, and raises it slowly after sustained healthy frames. It never changes the FPS cap, render distance, weather setting, world simulation, or server behavior.

## Compatibility and server rules

See [docs/COMPATIBILITY.md](docs/COMPATIBILITY.md) for the verified baseline, companion-mod guidance, troubleshooting, and the client-only trust boundary.

FPS Tune is not affiliated with Mojang, Microsoft, or any server. Client-only does not mean server-approved; keep it disabled where current rules prohibit client modifications.

## Performance methodology

FPS Tune should be evaluated using repeatable frame-time measurements, not a single FPS screenshot. [docs/BENCHMARKING.md](docs/BENCHMARKING.md) defines fair comparisons using average FPS, 1% lows, long-tail frame time, hitch counts, and explicit visual trade-offs.

In a real local stress test on an Apple M2 (macOS 26.6.2, Java 25, no shaders/resource packs/companion mods, Fancy graphics, render distance 16, simulation distance 12, VSync off, 240 FPS cap), average FPS and p95 frame time changed like this:

| Version | Avg FPS before | Avg FPS after | FPS change | p95 frame time before | p95 frame time after | p95 change |
| --- | ---: | ---: | ---: | ---: | ---: | ---: |
| 1.21.11 | 210.10 FPS | 215.17 FPS | +2.4% | 6.65 ms | 5.63 ms | -15.3% |
| 26.2 | 78.69 FPS | 102.78 FPS | +30.6% | 17.61 ms | 14.39 ms | -18.3% |

Before means FPS Tune was off; after means it was on. The lower p95 frame time is the part players are most likely to notice as smoother motion: the longer frames in the measured tail were less severe, even when the average-FPS gain was small. The test used four alternating phases in one client process, 120 warm-up frames and 600 measured render-loop intervals per phase, with the enabled phases capped at the default 300 particles per client tick. These are machine-specific extreme-workload observations—not a universal FPS guarantee—and the enabled case intentionally renders fewer particles.

A separate manual smoke test on Minecraft 26.2 used a repeating command block to emit 4,000 `minecraft:flame` particles per activation on an Apple M2 with Java 25.0.1. In the paired captures, the disabled case showed 38 FPS with a dense flame field, while the enabled case showed 58 FPS with materially fewer visible particles:

| FPS Tune state | F3 reading | Visual result |
| --- | ---: | --- |
| Disabled | 38 FPS | Dense flame field |
| Enabled | 58 FPS | Fewer admitted particles |

That is an approximately 53% higher instantaneous FPS reading in the enabled capture, but it is not an average-FPS or p95 measurement. The two captures are useful confirmation that the controller visibly reduces this extreme local particle workload; they should not be treated as a universal performance guarantee. See [docs/BENCHMARKING.md](docs/BENCHMARKING.md) for the full methodology and limitations.

## Build and test

Use GitHub Actions for the complete verification checklist. Open a pull request or trigger `.github/workflows/ci.yml` with `workflow_dispatch`; the hosted matrix builds both targets, runs tests and audits, and uploads artifacts. Do not install or run Java, JDKs, Gradle, the Gradle Wrapper, or project dependencies on the owner's device. Release artifacts are produced by the hosted release workflow.

## Updating FPS Tune

FPS Tune does not silently update itself or contact an update server. A maintainer updates dependencies and Minecraft compatibility in source, runs the verification checklist, and publishes a tagged GitHub release. Players then replace the older JAR in their `mods` folder and keep only one FPS Tune version installed.
