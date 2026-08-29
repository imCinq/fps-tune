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
- Can skip the local rain/snow render pass when explicitly enabled.
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

The default particle budget is 300 admitted particles per client tick. Weather rendering remains enabled unless you explicitly disable it in the settings.

## Configuration

Configuration is stored locally in `config/fpstune.properties`.

The complete setting reference, defaults, file format, migration behavior, and UI semantics are in [docs/CONFIGURATION.md](docs/CONFIGURATION.md).

| Setting | Default | Purpose |
| --- | --- | --- |
| `enabled` | `false` | Master switch; disabled by default. |
| `particleAdmissionEnabled` | `true` | Enables the particle budget while the master switch is active. |
| `maxParticlesPerTick` | `300` | Particle admissions allowed per client tick, clamped to `0..10000`. |
| `weatherRenderingEnabled` | `true` | Keeps the vanilla weather render pass enabled. |

Mod Menu is optional. Done saves settings; Cancel and Escape discard edits. `F6` toggles only the master switch and saves immediately.

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

```sh
for target in 1.21.11 26.2; do
  ./gradlew clean build -Pmc_target="$target"
  ./scripts/audit-client-only.sh "$target"
done
./scripts/audit-repository.sh
```

The build output is written to `build/libs/`; the selected target controls the artifact suffix. Tests cover configuration recovery, atomic writes, copy-on-edit settings behavior, budget boundaries, disabled behavior, independent render controllers, and a 100,000-particle admission simulation. The CI matrix runs the same build on Java 21 for 1.21.11 and Java 25 for 26.2.

For the full development and release procedures, see [docs/TESTING.md](docs/TESTING.md), [docs/MAINTENANCE.md](docs/MAINTENANCE.md), [docs/DISTRIBUTION.md](docs/DISTRIBUTION.md), and [REPOSITORY_SETUP.md](REPOSITORY_SETUP.md).

## Updating FPS Tune

FPS Tune does not silently update itself or contact an update server. A maintainer updates dependencies and Minecraft compatibility in source, runs the verification checklist, and publishes a tagged GitHub release. Players then replace the older JAR in their `mods` folder and keep only one FPS Tune version installed.
