# Maintenance and update process

## Routine dependency update

1. Review the upstream release notes and compatibility requirements.
2. Change only the relevant version in `gradle.properties`.
   For Gradle itself, regenerate the official Wrapper and verify both published checksums.
3. Run a clean build and all tests.
4. Run both repository audit scripts.
5. Review the produced JAR contents and dependency changes.
6. Record the change in `CHANGELOG.md`.
7. Merge only after CI passes.

Dependabot may open update pull requests, but it must not auto-merge them.

## Minecraft version update

Update Minecraft compatibility in a dedicated branch and pull request:

1. Update Minecraft, Fabric Loader, Fabric API, Loom, and Java only to versions documented as compatible by their official projects.
2. Regenerate mappings through a clean Loom build.
3. Compile before changing mixins so mapping or signature failures are visible.
4. Inspect `ParticleEngine.add` bytecode and confirm the exact vanilla queue insertion points.
5. Adapt the mixin only when the new bytecode requires it.
6. Add tests for any changed admission behavior.
7. Run a graphical client with CoreTune disabled and enabled.
8. Test a controlled particle storm and verify that normal particles, menus, world loading, disconnects, and shutdown remain stable.
9. Update `fabric.mod.json`, `README.md`, `AGENTS.md`, and `CHANGELOG.md`.

Do not claim support for a Minecraft version based only on compilation.

## Release process

1. Confirm the working tree contains no secrets or generated files.
2. Run `./gradlew clean build` and both audit scripts.
3. Confirm all tests pass and inspect `build/libs/core-tune-*.jar`.
4. Set the same version in `gradle.properties`, `CHANGELOG.md`, and the release tag.
5. Merge through a pull request with the required CI check.
6. Create an annotated `vX.Y.Z` tag and push it.
7. Let the release workflow rebuild from the tag and publish JARs plus SHA-256 checksums.
8. Download the release artifact, verify its checksum, and smoke-test that exact JAR before announcing it.

## Player update behavior

There is no in-mod updater. Updating means downloading the new GitHub Release JAR, removing the older CoreTune JAR from the instance, adding the new JAR, and keeping only one CoreTune version installed. Existing settings remain in `config/coretune.properties`; migration code and tests are required if the configuration format changes.
