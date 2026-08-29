# FPS Tune

FPS Tune is a focused, client-side Fabric performance mod for Minecraft 26.2. Its runtime behavior is intentionally bounded: it applies opt-in controls to optional local rendering workloads so unusually dense visual scenes produce fewer frame-time spikes. The current controllers cover particle admission and weather rendering.

## Platform posture

FPS Tune targets Minecraft 26.2 with Java 25, Fabric Loader 0.19.3 or newer, and Fabric API `0.158.0+26.2`. The compatibility baseline is declared in `gradle.properties` and `src/main/resources/fabric.mod.json`.

- Prefer narrow, measurable changes over broad rendering rewrites.
- Keep the mod client-only and disabled by default.
- Keep the runtime path local to optional rendering workloads and render scheduling.
- Treat Minecraft internals and mixin targets as version-specific implementation details.
- Preserve the existing configuration, audit, and release boundaries when extending the project.

## Read it before you

| Read it before you | Source |
| --- | --- |
| change runtime wiring, configuration, render controllers, mixins, or packaging | [docs/ARCHITECTURE.md](docs/ARCHITECTURE.md) |
| build, test, package, or verify the mod | [docs/TESTING.md](docs/TESTING.md) and [README.md](README.md) |
| change a Minecraft, Fabric, Java, Gradle, or Action version | [docs/MAINTENANCE.md](docs/MAINTENANCE.md) and [gradle.properties](gradle.properties) |
| change the distribution or release surface | [docs/DISTRIBUTION.md](docs/DISTRIBUTION.md) and [REPOSITORY_SETUP.md](REPOSITORY_SETUP.md) |
| add or change regression coverage | [src/test](src/test) and [docs/TESTING.md](docs/TESTING.md) |
| work on a mixin target | [src/main/java/dev/fpstune/mixin](src/main/java/dev/fpstune/mixin) and [src/main/resources/fpstune.mixins.json](src/main/resources/fpstune.mixins.json) |

## Further information

- Inspect current source, tests, configuration, and Git state before relying on documentation or assumptions.
- Keep documentation canonical. Update the existing source of truth instead of duplicating compatibility, architecture, testing, or release details.
- Preserve the separation between the client entrypoint, configuration store, render policies, controllers, and version-specific mixin bridges described in [docs/ARCHITECTURE.md](docs/ARCHITECTURE.md).
- Prefer deterministic behavior and explicit failure handling. Configuration recovery and file writes should remain safe under malformed or interrupted input.
- A mixin change should be supported by target bytecode inspection and an appropriate client smoke test; injection points should not be widened speculatively.
- Keep dependency and GitHub Action changes reviewable, pinned where the repository already requires pinning, and documented when they affect compatibility.
- Keep release artifacts reproducible from a clean checkout and use the same verified build output across release destinations.

## Tests

Committed tests should cover critical behavior, compatibility-sensitive logic, and high-value regressions. Current coverage includes admission-budget boundaries, independently disabled controllers, configuration recovery, atomic writes, legacy configuration defaults, render-policy behavior, and a large-particle admission simulation.

Use the repository's standard checks for every meaningful change:

```sh
./gradlew clean build
./scripts/audit-client-only.sh
./scripts/audit-repository.sh
```

Temporary experiments and graphical smoke-test artifacts belong outside the committed source tree unless they become a deliberate, maintainable part of the project.

## Before you finish

Follow the relevant validation defined by the linked sources. Report only checks actually performed, and distinguish compilation, unit tests, packaging, bytecode inspection, graphical client testing, hosted CI, and release verification.
