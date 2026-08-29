# FPS Tune

FPS Tune is a client-only Fabric FPS and frame-time stability toolkit for Minecraft 26.2. It applies opt-in controls to optional local rendering workloads, currently limiting particle admission and allowing weather rendering to be disabled during extreme visual scenes.

## Safety boundary

- Client-side only
- No packet or custom network handling
- No combat, movement, inventory, targeting, rotation, or click automation
- No telemetry, analytics, update checker, or remote configuration
- Disabled by default
- Changes only local rendering and render scheduling

FPS Tune cannot guarantee approval by any multiplayer server or anti-cheat. Check the server's current rules before enabling it.

## Install

Use Java 25, Fabric Loader 0.19.3 or newer, Fabric API, and Minecraft 26.2.

1. Put `fps-tune-1.0.0.jar` in the instance's `mods` folder.
2. Start Minecraft and press F6 to enable or disable the configured FPS Tune render controls. If Mod Menu is installed, open Mods, select FPS Tune, and choose Configure to edit the same settings in-game.
3. The default particle limit is 300 admitted particles per client tick. Weather rendering remains enabled unless you explicitly set `weatherRenderingEnabled=false` in the local configuration.

Configuration is stored locally in `config/fpstune.properties`.

When upgrading from an earlier private build, FPS Tune also imports `config/coretune.properties` into the new file without deleting the original.

The master switch is disabled by default. Individual controllers can be configured with `particleAdmissionEnabled` and `weatherRenderingEnabled`; changing the weather setting only takes effect while FPS Tune is enabled.
Mod Menu is an optional settings integration; FPS Tune still runs without Mod Menu. The settings screen uses native Minecraft widgets, and Done saves the local configuration while Cancel and Escape discard edits.

## Build and test

```sh
./gradlew clean build
./scripts/audit-client-only.sh
./scripts/audit-repository.sh
```

The build output is written to `build/libs/`. Tests cover configuration recovery, atomic writes, copy-on-edit settings behavior, budget boundaries, disabled behavior, independent render controllers, and a 100,000-particle admission simulation.

## Updating FPS Tune

FPS Tune does not silently update itself or contact an update server. A maintainer updates dependencies and Minecraft compatibility in source, runs the full verification checklist, and publishes a tagged GitHub release. Players then replace the old JAR in their `mods` folder with the new release.

See [AGENTS.md](AGENTS.md) for future coding-agent rules, [docs/MAINTENANCE.md](docs/MAINTENANCE.md) for the update process, [docs/DISTRIBUTION.md](docs/DISTRIBUTION.md) for publishing rules, and [REPOSITORY_SETUP.md](REPOSITORY_SETUP.md) for GitHub setup.
