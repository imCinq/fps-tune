# Maintenance and update process

## Remote-only execution

All builds, tests, audits, mapping generation, bytecode inspection, graphical smoke tests, and artifact inspection must run through GitHub Actions or another owner-approved remote environment. Do not install, download, or run Java, any JDK, Gradle, the Gradle Wrapper distribution, or project dependencies on the owner's device.

## Routine dependency update

1. Review the upstream release notes and compatibility requirements.
2. Change only the relevant value in `gradle/versions/<minecraft-version>.properties`. The pinned Mod Menu version must remain compatible with that target and is reviewed like every other dependency; do not auto-merge its Dependabot update.
   For Gradle itself, regenerate the official Wrapper and verify both published checksums.
3. Trigger the hosted GitHub Actions build and wait for all tests to pass.
4. Confirm the hosted workflow runs both repository audit scripts.
5. Review the produced hosted artifacts and dependency changes.
6. Record the change in `CHANGELOG.md`.
7. Merge only after CI passes.

Dependabot may open update pull requests, but it must not auto-merge them.

## Minecraft version update

Update Minecraft compatibility in a dedicated branch and pull request:

1. Add or update `gradle/versions/<minecraft-version>.properties` with Minecraft, Fabric Loader, Fabric API, Loom, Java, mappings, and artifact settings documented as compatible by their official projects.
2. Select the correct Loom plugin: remapping Loom for Minecraft 1.21.11 and older, non-remapping Loom for Minecraft 26.1 and newer.
3. Regenerate mappings in a GitHub-hosted build for the selected target.
4. Compile in the hosted workflow before changing mixins so mapping or signature failures are visible.
5. Inspect the affected client render bytecode, including `ParticleEngine.add`, `ParticleEngine.tick`, and `WeatherEffectRenderer.render` inside `LevelRenderer.addWeatherPass` for 1.21.11/26.2 or `LevelRenderer.renderSnowAndRain` for 1.21.1 when applicable.
6. Adapt a mixin only when the new bytecode requires it, and avoid hooks that can stall render-worker queues.
7. Add tests for any changed admission behavior and build every supported target.
8. Run a graphical client in a GitHub-hosted or other owner-approved remote environment with FPS Tune disabled and enabled for the new target.
9. In that remote environment, test a controlled particle storm, diagnostics overlay, Adaptive budget behavior, and optional weather-render reduction, then verify that normal particles, menus, world loading, disconnects, and shutdown remain stable.
10. Update the target `fabric.mod.json`, `README.md`, `AGENTS.md`, `docs/COMPATIBILITY.md`, `docs/DISTRIBUTION.md`, and `CHANGELOG.md`. Recheck the optional Mod Menu settings screen when its API or Minecraft compatibility changes.

Do not claim support for a Minecraft version based only on compilation.

## Release process

1. Confirm the working tree contains no secrets or generated files.
2. Trigger and confirm the hosted release workflow runs the selected-target build and both audit scripts.
3. Confirm all tests pass and inspect the hosted `fps-tune-*.jar` artifacts.
4. For a full release, set the same version in `gradle.properties`, `CHANGELOG.md`, and the release tag. For a target-specific patch, keep the internal version and use the documented target tag.
5. Merge through a pull request with the required CI check.
6. Create an annotated `vX.Y.Z` tag for a full release, or the documented target-specific tag such as `v1.1.1-mc1.21.1`, and push it.
7. Let the matching release workflow rebuild from the annotated tag, verify its signed target commit and `main` ancestry, and publish JARs plus SHA-256 checksums. For the 1.21.1 patch, run the hosted promotion mode in `release-1.21.1.yml` to attach the already verified target JARs to the existing `v1.1.1` release.
8. Download the release artifact, verify its checksum, and smoke-test that exact JAR before announcing it.

## Player update behavior

There is no in-mod updater. Updating means downloading the new GitHub Release JAR, removing the older FPS Tune JAR from the instance, adding the new JAR, and keeping only one FPS Tune version installed. Existing settings remain in `config/fpstune.properties`. The renamed release reads an existing `config/coretune.properties` only when the new file is absent, writes the migrated values to `config/fpstune.properties`, and leaves the original file untouched. Migration code and tests are required if the configuration format changes.
