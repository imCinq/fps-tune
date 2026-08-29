# CoreTune testing

## Fast deterministic checks

Run the complete local checklist from the repository root:

```sh
./gradlew clean build
./scripts/audit-client-only.sh
./scripts/audit-repository.sh
```

The Gradle build compiles the mod and runs the committed unit tests. Current tests cover configuration recovery, atomic writes, enabled/disabled behavior, budget boundaries, and a 100,000-particle admission simulation.

## Mixin verification

Mixin changes require more than unit tests:

1. Inspect the target Minecraft bytecode.
2. Confirm the expected `ParticleEngine.add` method shape and queue insertion points.
3. Update the mixin and tests together.
4. Run a graphical `runClient` smoke test.
5. Record any compatibility change in `CHANGELOG.md` and `docs/MAINTENANCE.md`.

If bytecode structure changes, stop and redesign the injection rather than forcing a stale hook.

## Repository audits

`scripts/audit-client-only.sh` checks that source and mixin configuration stay within the client-only boundary. `scripts/audit-repository.sh` checks the working tree for common credentials, private paths, and accidental runtime artifacts. Both scripts must pass before review.

## Hosted CI

`.github/workflows/ci.yml` repeats the build and audits on GitHub Actions and uploads the built artifact. The workflow is the clean-checkout verification signal; local results and hosted results should be reported separately.

## Release verification

`.github/workflows/release.yml` repeats the checks for a version tag, confirms the tag matches the project version, and publishes the GitHub Release artifact. Use the verified output from that workflow for later manual distribution submissions.
