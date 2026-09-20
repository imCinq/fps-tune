# FPS Tune testing

## Hosted deterministic checks

Use GitHub Actions for the complete verification checklist. Open a pull request or trigger the CI workflow with `workflow_dispatch`; the hosted matrix builds all six supported target configurations, runs the committed tests and audits, and uploads the verified artifact. Do not invoke Java, any JDK, Gradle, the Gradle Wrapper, or project dependencies on the owner's device.

The hosted Gradle build compiles the selected target and runs the committed unit tests. Current tests cover configuration recovery, atomic writes, legacy defaults and Auto-target migration, enabled/disabled behavior, independent controller gates, fixed, snapshot-backed, and nearby-priority budget boundaries, dynamic nearby reserves, pressure-gated Adaptive budget streaks/cooldowns, bounded emergency reductions, changing effective targets, current-tick diagnostics metrics, scoped Advanced settings reset behavior, and a 100,000-particle admission simulation. The target builds also compile the cross-version proximity bridge and its allocation-free-equivalent bounding-box math. The hosted CI matrix runs with Java 21 for Fabric and NeoForge 1.21.1 plus Fabric 1.21.11, and Java 25 for Fabric and NeoForge 26.2 plus Fabric 26.3.

The compile also verifies the optional Mod Menu API integration. The settings screen uses a copied configuration, so its Done, Cancel, and Escape paths should be checked as separate UI behaviors.

## Mixin verification

Mixin changes require more than hosted unit tests and must be verified in a GitHub-hosted or other owner-approved remote client environment:

1. Inspect the target Minecraft bytecode.
2. Confirm the expected `ParticleEngine.add` and `ParticleEngine.tick` shapes, plus `LevelRenderer.renderSnowAndRain` on 1.21.1 or `WeatherEffectRenderer.render` reached from `LevelRenderer.addWeatherPass` on 1.21.11/26.2, and their render boundaries for the selected target.
3. Update the mixin and tests together.
4. Run graphical `runClient` smoke tests for the selected target with FPS Tune disabled, particle admission enabled, weather rendering disabled, diagnostics enabled, and Adaptive mode enabled as separate configuration cases. When weather rendering is disabled, confirm the rain and snow streaks and their landing splash particles disappear while rain/snow sounds continue and Map/World Border effects remain visible.
5. Record any compatibility change in `CHANGELOG.md` and `docs/MAINTENANCE.md`.

## Mod Menu settings screen

For Fabric targets with Mod Menu, repeat the click-through on each matching Mod Menu version. For NeoForge 1.21.1 and 26.2, repeat the same settings checks from the native Mods screen extension:

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

`.github/workflows/ci.yml` repeats the build and audits on GitHub Actions and uploads the verified artifacts. The workflow is the clean-checkout verification signal; report its hosted results in the pull request.

The CI matrix builds each supported `mc_target`, including the `mc_target=26.3` Fabric target on Java 25. Real-client smoke coverage for that target is defined in `.github/workflows/client-26.3.yml` and runs with Mod Menu present and absent.

## Release verification

`.github/workflows/release.yml` repeats the checks for the full-release tag, while `.github/workflows/release-1.21.1.yml` performs the same hosted verification for `vX.Y.Z-mc1.21.1`; both verify annotated-tag provenance, a signed target commit reachable from `main`, the project version, and matching GitHub Release artifacts. Its manual promotion mode verifies the published 1.21.1 checksums and attaches those artifacts to the existing `v1.1.1` release. Use the verified output from that workflow for later manual distribution submissions.

## Fabric 26.3 target-specific release verification

The current [`v1.2.5-mc26.3`](https://github.com/imCinq/fps-tune/releases/tag/v1.2.5-mc26.3) release is published separately from the five-artifact root `v1.2.5` release. `.github/workflows/release-26.3.yml` handles only `vX.Y.Z-mc26.3` tags; the full-release workflow excludes these tags. The workflow verifies the annotated tag, its GitHub-verified signed target commit and `main` ancestry, matching tag/project/packaged versions, client-only Fabric 26.3 metadata, and both audit scripts. It builds only the 26.3 binary and sources JARs and stages a **draft**, non-latest release with SHA-256 checksums. Existing release assets remain unchanged.

This release workflow does **not** itself run or enforce graphical testing. Draft creation, compilation, unit tests, and bytecode evidence are separate from runtime verification. For a future 26.3 target-specific release, publish the draft only after:

1. In a GitHub-hosted or other owner-approved remote graphical environment, download the draft assets and verify `sha256sum -c SHA256SUMS.txt`.
2. Install that exact binary with the matching Minecraft 26.3/Fabric dependencies; a development `runClient` launch alone does not verify the packaged release JAR.
3. Repeat the particle, weather, diagnostics, Adaptive, and settings cases above, including F6 under SDL; precipitation with Improved Transparency enabled and disabled while retaining world-border effects; HUD cache invalidation on resource reload; and Adaptive sampling with diagnostics off. Verify menus, world loading, disconnects, and shutdown.
4. Verify startup both with and without matching Mod Menu. Mod Menu `21.0.0-beta.1` is optional and must not be treated as a required dependency.
5. Record the exact binary checksum, dependency versions, sanitized logs/screenshots, and results. A maintainer must approve publication after the required validation passes.

Do not infer a working graphical environment from `ubuntu-latest` or Xvfb alone: verify the display and rendering backend actually support the target client. No graphical testing or Java/Gradle execution may run on the owner's device.

## NeoForge targets

Each NeoForge target must pass its isolated build task (`:neoforge-1.21.1:build` or `:neoforge-26.2:build`), matching client-only audit, repository privacy audit, and packaged metadata checks. Confirm the JAR contains `META-INF/neoforge.mods.toml`, `assets/fpstune/icon.png`, and the NeoForge mixin configuration, and does not contain Fabric metadata.
