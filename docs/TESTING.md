# FPS Tune testing

## Fast deterministic checks

Run the complete local checklist from the repository root:

```sh
./gradlew clean build
./scripts/audit-client-only.sh
./scripts/audit-repository.sh
```

The Gradle build compiles the mod and runs the committed unit tests. Current tests cover configuration recovery, atomic writes, legacy defaults, enabled/disabled behavior, independent controller gates, budget boundaries, and a 100,000-particle admission simulation.

The compile also verifies the optional Mod Menu API integration. The settings screen uses a copied configuration, so its Done, Cancel, and Escape paths should be checked as separate UI behaviors.

## Mixin verification

Mixin changes require more than unit tests:

1. Inspect the target Minecraft bytecode.
2. Confirm the expected `ParticleEngine.add`, `ParticleEngine.tick`, and `LevelRenderer.addWeatherPass` method shapes and render boundaries.
3. Update the mixin and tests together.
4. Run graphical `runClient` smoke tests for FPS Tune disabled, particle admission enabled, and weather rendering disabled as separate configuration cases.
5. Record any compatibility change in `CHANGELOG.md` and `docs/MAINTENANCE.md`.

## Mod Menu settings screen

When Mod Menu is present:

1. Open the Mods screen, select FPS Tune, and open Configure.
2. Confirm the master switch, particle admission switch, weather-rendering switch, and particle budget reflect `config/fpstune.properties`.
3. Change values, close with Cancel or Escape, and confirm the file and runtime settings are unchanged.
4. Change values, close with Done, and confirm the file is updated and the new values apply without restarting the client.

Also launch the built FPS Tune JAR without Mod Menu to confirm the optional entrypoint does not affect normal client startup.

If bytecode structure changes, stop and redesign the injection rather than forcing a stale hook. Never gate a worker queue in a way that can leave pending render work permanently unscheduled.

## Repository audits

`scripts/audit-client-only.sh` checks that source and mixin configuration stay within the client-only boundary. `scripts/audit-repository.sh` checks the working tree for common credentials, private paths, and accidental runtime artifacts. Both scripts must pass before review.

## Hosted CI

`.github/workflows/ci.yml` repeats the build and audits on GitHub Actions and uploads the built artifact. The workflow is the clean-checkout verification signal; local results and hosted results should be reported separately.

## Release verification

`.github/workflows/release.yml` repeats the checks for a version tag, confirms the tag matches the project version, and publishes the GitHub Release artifact. Use the verified output from that workflow for later manual distribution submissions.
