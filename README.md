# FPS Tune

FPS Tune is a client-side Fabric frame-time stability toolkit for Minecraft 26.2. It applies opt-in controls to optional local rendering workloads when visual scenes become unusually heavy.

It is designed to protect the floor of the frame-time graph during particle storms and weather-heavy scenes, not to promise a universal FPS increase or replace broad rendering optimizers.

## At a glance

| Item | Value |
| --- | --- |
| Minecraft | 26.2 |
| Loader | Fabric Loader 0.19.3+ |
| Environment | Client only |
| Java | 25+ |
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

1. Install Minecraft 26.2 with Fabric Loader 0.19.3 or newer.
2. Install Fabric API for Minecraft 26.2.
3. Download `fps-tune-1.0.0.jar` from the [GitHub Releases page](https://github.com/imCinq/fps-tune/releases).
4. Put the JAR in the instance's `mods` folder and start Minecraft.
5. Press `F6` to toggle the master switch, or install Mod Menu and choose FPS Tune → Configure.

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

## Build and test

```sh
./gradlew clean build
./scripts/audit-client-only.sh
./scripts/audit-repository.sh
```

The build output is written to `build/libs/`. Tests cover configuration recovery, atomic writes, copy-on-edit settings behavior, budget boundaries, disabled behavior, independent render controllers, and a 100,000-particle admission simulation.

For the full development and release procedures, see [docs/TESTING.md](docs/TESTING.md), [docs/MAINTENANCE.md](docs/MAINTENANCE.md), [docs/DISTRIBUTION.md](docs/DISTRIBUTION.md), and [REPOSITORY_SETUP.md](REPOSITORY_SETUP.md).

## Updating FPS Tune

FPS Tune does not silently update itself or contact an update server. A maintainer updates dependencies and Minecraft compatibility in source, runs the verification checklist, and publishes a tagged GitHub release. Players then replace the older JAR in their `mods` folder and keep only one FPS Tune version installed.
