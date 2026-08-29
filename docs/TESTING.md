# FPS Tune testing

## Fast deterministic checks

Run the complete local checklist from the repository root for every supported target:

```sh
for target in 1.21.11 26.2; do
  ./gradlew clean build -Pmc_target="$target"
  ./scripts/audit-client-only.sh "$target"
done
./scripts/audit-repository.sh
```

The Gradle build compiles the selected target and runs the committed unit tests. Current tests cover configuration recovery, atomic writes, legacy defaults, enabled/disabled behavior, independent controller gates, fixed and nearby-priority budget boundaries, adaptive budget streaks/cooldowns, current-tick diagnostics metrics, and a 100,000-particle admission simulation. The hosted CI matrix repeats this build with Java 21 for 1.21.11 and Java 25 for 26.2.

The compile also verifies the optional Mod Menu API integration. The settings screen uses a copied configuration, so its Done, Cancel, and Escape paths should be checked as separate UI behaviors.

## Mixin verification

Mixin changes require more than unit tests:

1. Inspect the target Minecraft bytecode.
2. Confirm the expected `ParticleEngine.add`, `ParticleEngine.tick`, and `LevelRenderer.addWeatherPass` method shapes and render boundaries for the selected target.
3. Update the mixin and tests together.
4. Run graphical `runClient` smoke tests for the selected target with FPS Tune disabled, particle admission enabled, weather rendering disabled, diagnostics enabled, and Adaptive mode enabled as separate configuration cases.
5. Record any compatibility change in `CHANGELOG.md` and `docs/MAINTENANCE.md`.

## Mod Menu settings screen

When Mod Menu is present, repeat the click-through on each supported target's matching Mod Menu version:

1. Open the Mods screen, select FPS Tune, and open Configure.
2. Confirm the FPS Tune details pane shows a wrapped long-form overview with clear sections for behavior, boundaries, setup, and the intentional visual trade-off.
3. Confirm the main screen shows the master switch, performance profile, rain/snow, performance overlay, and Advanced settings controls.
4. Open Advanced settings and confirm the individual particle and automatic-adjustment controls reflect `config/fpstune.properties`.
5. Change values, return with Back, close the main screen with Cancel or Escape, and confirm the file and runtime settings are unchanged.
6. Change values, return with Back, close the main screen with Done, and confirm the file is updated and the new values apply without restarting the client.

Cycle through each performance profile and confirm that the displayed settings take effect only after Done. Use Reset all settings in Advanced settings, return with Back, and confirm that the defaults are visible in the draft without saving until Done. With diagnostics enabled, enter a world and confirm that the overlay is hidden on menus, remains local, reports current-tick accepted/rejected counts, and shows `Nearby` as off when nearby priority is disabled. In a controlled particle storm, compare nearby and distant particles while changing the protection value; the total admitted count must never exceed the configured limit.

With Adaptive mode enabled, use a repeatable particle storm to confirm that the budget starts from the fixed setting, decreases after sustained slow frames, increases only after sustained healthy frames, respects the configured minimum and maximum, and does not rapidly oscillate around the target. Confirm that opening a menu or leaving the world resets the frame-time baseline without changing the saved settings.

Also launch the built FPS Tune JAR without Mod Menu to confirm the optional entrypoint does not affect normal client startup.

If bytecode structure changes, stop and redesign the injection rather than forcing a stale hook. Never gate a worker queue in a way that can leave pending render work permanently unscheduled.

## Repository audits

`scripts/audit-client-only.sh` checks that source and mixin configuration stay within the client-only boundary. `scripts/audit-repository.sh` checks the working tree for common credentials, private paths, and accidental runtime artifacts. Both scripts must pass before review.

## Hosted CI

`.github/workflows/ci.yml` repeats the build and audits on GitHub Actions and uploads the built artifact. The workflow is the clean-checkout verification signal; local results and hosted results should be reported separately.

## Release verification

`.github/workflows/release.yml` repeats the checks for a version tag, confirms the tag matches the project version, and publishes the GitHub Release artifact. Use the verified output from that workflow for later manual distribution submissions.
