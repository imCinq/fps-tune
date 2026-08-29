# CoreTune

CoreTune is a narrow, client-only Fabric performance mod for Minecraft 26.2. It limits how many new particles can enter Minecraft's particle engine during a single client tick, reducing rendering spikes during extreme particle storms.

## Safety boundary

- Client-side only
- No packet or custom network handling
- No combat, movement, inventory, targeting, rotation, or click automation
- No telemetry, analytics, update checker, or remote configuration
- Disabled by default
- Changes only local particle rendering

CoreTune cannot guarantee approval by any multiplayer server or anti-cheat. Check the server's current rules before enabling it.

## Install

Use Java 25, Fabric Loader 0.19.3 or newer, Fabric API, and Minecraft 26.2.

1. Put `core-tune-1.0.0.jar` in the instance's `mods` folder.
2. Start Minecraft and press F6 to enable or disable CoreTune.
3. The default limit is 300 admitted particles per client tick.

Configuration is stored locally in `config/coretune.properties`.

## Build and test

```sh
./gradlew clean build
./scripts/audit-client-only.sh
./scripts/audit-repository.sh
```

The build output is written to `build/libs/`. Tests cover configuration recovery, atomic writes, budget boundaries, disabled behavior, and a 100,000-particle admission simulation.

## Updating CoreTune

CoreTune does not silently update itself or contact an update server. A maintainer updates dependencies and Minecraft compatibility in source, runs the full verification checklist, and publishes a tagged GitHub release. Players then replace the old JAR in their `mods` folder with the new release.

See [AGENTS.md](AGENTS.md) for future coding-agent rules, [docs/MAINTENANCE.md](docs/MAINTENANCE.md) for the update process, [docs/DISTRIBUTION.md](docs/DISTRIBUTION.md) for publishing rules, and [REPOSITORY_SETUP.md](REPOSITORY_SETUP.md) for GitHub setup.
