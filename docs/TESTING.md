# FPS Tune testing

## Hosted deterministic checks

Use GitHub Actions for the complete verification checklist. Open a pull request or trigger the CI workflow with `workflow_dispatch`; the hosted matrix builds each supported target, runs the committed tests and audits, and uploads the verified artifact. Do not invoke Java, any JDK, Gradle, the Gradle Wrapper, or project dependencies on the owner's device.

The hosted Gradle build compiles the selected target and runs the committed unit tests. Current tests cover configuration recovery, atomic writes, legacy defaults and Auto-target migration, enabled/disabled behavior, independent controller gates, fixed, snapshot-backed, and nearby-priority budget boundaries, dynamic nearby reserves, pressure-gated Adaptive budget streaks/cooldowns, bounded emergency reductions, changing effective targets, current-tick diagnostics metrics, scoped Advanced settings reset behavior, and a 100,000-particle admission simulation. The target builds also compile the cross-version proximity bridge and its allocation-free-equivalent bounding-box math. The hosted CI matrix runs with Java 21 for 1.21.1 and 1.21.11, and Java 25 for 26.2.

The compile also verifies the optional Mod Menu API integration. The settings screen uses a copied configuration, so its Done, Cancel, and Escape paths should be checked as separate UI behaviors.

## Mixin verification

Mixin changes require more than hosted unit tests and must be verified in a GitHub-hosted or other owner-approved remote client environment:

1. Inspect the target Minecraft bytecode.
2. Confirm the expected `ParticleEngine.add` and `ParticleEngine.tick` shapes, plus `LevelRenderer.renderSnowAndRain` on 1.21.1 or `LevelRenderer.addWeatherPass` on 1.21.11/26.2, and their render boundaries for the selected target.
3. Update the mixin and tests together.
4. Run graphical `runClient` smoke tests for the selected target with FPS Tune disabled, particle admission enabled, weather rendering disabled, diagnostics enabled, and Adaptive mode enabled as separate configuration cases.
5. Record any compatibility change in `CHANGELOG.md` and `docs/MAINTENANCE.md`.

## Mod Menu settings screen

When Mod Menu is present, repeat the click-through on each supported target's matching Mod Menu version:

1. Open the Mods screen, select FPS Tune, and open Configure.
2. Confirm the FPS Tune details pane shows a wrapped long-form overview with clear sections for behavior, boundaries, setup, and the intentional visual trade-off.
3. Confirm the main screen shows the master switch, performance profile, rain/snow, performance overlay, and Advanced settings controls.
4. Open Advanced settings and confirm the grouped particle and Adaptive controls reflect `config/fpstune.properties`; confirm nearby controls dim when nearby priority is off, and target/min/max controls dim when Adaptive is off. Confirm the target selector shows Auto or a numeric target.
5. Change values, return with Back, close the main screen with Cancel or Escape, and confirm the file and runtime settings are unchanged.
6. Change values, return with Back, close the main screen with Done, and confirm the file is updated and the new values apply without restarting the client.

Cycle through each performance profile and confirm that the displayed settings take effect only after Done. Use Reset advanced settings in Advanced settings, return with Back, and confirm that particle and Adaptive defaults are visible in the draft while the master switch, weather setting, and diagnostics choice remain unchanged; confirm that nothing is saved until Done. With diagnostics enabled, enter a world and confirm that the overlay is hidden on menus, remains local, reports current-tick accepted/rejected counts, and shows `Nearby` as off when nearby priority is disabled. In a controlled particle storm, compare nearby and distant particles while changing the protection value; the total admitted count must never exceed the configured limit.

With Adaptive mode enabled, use a repeatable particle storm to confirm that the budget starts from the fixed setting, decreases only after sustained slow frames with particle pressure, holds during slow frames with little particle pressure, increases only after sustained healthy frames, respects the configured minimum and maximum, does not rapidly oscillate around the target, and applies the bounded emergency reduction only after severe pressured frames while holding during severe frames without pressure. Confirm that opening a menu or leaving the world resets the frame-time baseline without changing the saved settings.

Also launch the built FPS Tune JAR without Mod Menu to confirm the optional entrypoint does not affect normal client startup.

If bytecode structure changes, stop and redesign the injection rather than forcing a stale hook. Never gate a worker queue in a way that can leave pending render work permanently unscheduled.

## Repository audits

`scripts/audit-client-only.sh` checks that source and mixin configuration stay within the client-only boundary. `scripts/audit-repository.sh` checks the working tree for common credentials, private paths, and accidental runtime artifacts. Both scripts must pass before review.

## Hosted CI

`.github/workflows/ci.yml` repeats the build and audits on GitHub Actions and uploads the built artifact. The workflow is the clean-checkout verification signal; report its hosted results in the pull request.

## Release verification

`.github/workflows/release.yml` repeats the checks for the full-release tag, while `.github/workflows/release-1.21.1.yml` performs the same hosted verification for `vX.Y.Z-mc1.21.1`; both verify annotated-tag provenance, a signed target commit reachable from `main`, the project version, and matching GitHub Release artifacts. Its manual promotion mode verifies the published 1.21.1 checksums and attaches those artifacts to the existing `v1.1.1` release. Use the verified output from that workflow for later manual distribution submissions.
