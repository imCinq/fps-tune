# FPS Tune

FPS Tune is a focused, client-side Fabric and NeoForge performance mod for Minecraft 1.21.1 Fabric and NeoForge, 1.21.11 Fabric, 26.2 Fabric and NeoForge, and 26.3 Fabric. Its runtime behavior is intentionally bounded: it applies opt-in controls to optional local rendering workloads so unusually dense visual scenes produce fewer frame-time spikes. The current controllers cover particle admission and weather rendering.

## Remote-only development

All future repository work must be performed remotely through GitHub.

- Use GitHub branches and pull requests, GitHub Actions, hosted artifacts/releases, and other remote GitHub tooling for editing, building, testing, packaging, and verification.
- Do not edit, build, test, or package this project in a local checkout on the owner's device.
- Do not install, download, or run Java, any JDK, Gradle, the Gradle Wrapper distribution, or project dependencies on the owner's device.
- If a required task cannot be completed with GitHub-hosted tooling, stop and ask the owner before doing anything locally.

## Platform posture

FPS Tune has versioned targets rather than one universal JAR:

- Minecraft 1.21.1 Fabric uses Java 21, Fabric Loader 0.16.14, Fabric API `0.116.15+1.21.1`, and the remapping Loom plugin.
- Minecraft 1.21.1 NeoForge uses Java 21, NeoForge `21.1.250`, and the isolated ModDevGradle project.
- Minecraft 1.21.11 uses Java 21, Fabric Loader 0.18.6, Fabric API `0.141.6+1.21.11`, and the remapping Loom plugin.
- Minecraft 26.2 uses Java 25, Fabric Loader 0.19.3, Fabric API `0.158.0+26.2`, and the non-remapping Loom plugin.
- Minecraft 26.3 Fabric uses Java 25, Fabric Loader 0.19.5, Fabric API `0.160.6+26.3`, and the non-remapping Loom plugin. Mod Menu `21.0.0-beta.1` is optional and not required. No NeoForge 26.3 target is included.

The compatibility profiles are declared in `gradle/versions/`, with matching Fabric metadata in `src/1.21.1/resources/`, `src/1.21.11/resources/`, `src/26.2/resources/`, and `src/26.3/resources/`, plus NeoForge metadata in `src/1.21.1-neoforge/resources/` and `src/26.2-neoforge/resources/`.

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
| change a Minecraft, Fabric, Java, Gradle, or Action version | [docs/MAINTENANCE.md](docs/MAINTENANCE.md) and the matching profile in [gradle/versions](gradle/versions) |
| change the distribution or release surface | [docs/DISTRIBUTION.md](docs/DISTRIBUTION.md) and [docs/REPOSITORY_SETUP.md](docs/REPOSITORY_SETUP.md) |
| add or change regression coverage | [src/test](src/test) and [docs/TESTING.md](docs/TESTING.md) |
| work on a mixin target | [src/main/java/dev/fpstune/mixin](src/main/java/dev/fpstune/mixin) and the matching file in `src/<minecraft-version>/resources/` |

## Further information

- Inspect current source, tests, configuration, and Git state before relying on documentation or assumptions.
- Keep documentation canonical. Update the existing source of truth instead of duplicating compatibility, architecture, testing, or release details.
- Preserve the separation between the client entrypoint, configuration store, render policies, controllers, and version-specific mixin bridges described in [docs/ARCHITECTURE.md](docs/ARCHITECTURE.md).
- Prefer deterministic behavior and explicit failure handling. Configuration recovery and file writes should remain safe under malformed or interrupted input.
- A mixin change should be supported by target bytecode inspection and an appropriate client smoke test; injection points should not be widened speculatively.
- Keep dependency and GitHub Action changes reviewable, pinned where the repository already requires pinning, and documented when they affect compatibility.
- Keep release artifacts reproducible from a clean checkout and use the same verified build output across release destinations.

## Changelog style

Use the project's simple patch-notes style for future release entries:

- Start with a clear `## <Project> v<version>` heading.
- Use a short, easy-to-scan bullet list; emoji are welcome, and bold key Minecraft targets or major features when useful.
- Cover user-visible additions, improvements, experimental or visual trade-offs, removals, compatibility, and fixes as applicable.
- Keep claims tied to verified behavior and avoid universal performance promises.
## Tests

Committed tests should cover critical behavior, compatibility-sensitive logic, and high-value regressions. Current coverage includes admission-budget boundaries, nearby-priority behavior, bounded Adaptive streaks and cooldowns, diagnostics metrics, independently disabled controllers, configuration recovery, atomic writes, legacy configuration defaults, render-policy behavior, and a large-particle admission simulation.

Use the repository's GitHub Actions workflows for every meaningful change. Open a pull request or run the CI workflow manually; all Java/Gradle builds, tests, audits, and artifact verification must execute on GitHub-hosted runners, not on the owner's device.

Temporary experiments and graphical smoke-test artifacts belong outside the committed source tree unless they become a deliberate, maintainable part of the project.

## Before you finish

Follow the relevant validation defined by the linked sources. Report only checks actually performed, and distinguish compilation, unit tests, packaging, bytecode inspection, graphical client testing, hosted CI, and release verification.

The six supported target configurations are 1.21.1 Fabric, 1.21.1 NeoForge, 1.21.11 Fabric, 26.2 Fabric, 26.2 NeoForge, and 26.3 Fabric. The main `v1.2.5` release contains the five baseline artifacts; Fabric 26.3 is published in the target-specific `v1.2.5-mc26.3` release. Retain the default `mc_target=26.2` and all existing artifact names and releases.

The separate Fabric 26.3 release uses internal version `1.2.5`, annotated tag `v1.2.5-mc26.3`, and `fps-tune-mc26.3-1.2.5.jar`. `.github/workflows/release-26.3.yml` validates and stages a target-specific draft with binary/sources JARs and checksums; it does not itself enforce graphical testing. The current exact asset was reviewed through the hosted client smoke process and then published as the non-draft [`v1.2.5-mc26.3`](https://github.com/imCinq/fps-tune/releases/tag/v1.2.5-mc26.3) release. Do not promote these assets into the root `v1.2.5` release. See `docs/TESTING.md` and `docs/DISTRIBUTION.md`.

Hosted target commands are:

```sh
./gradlew :clean :build -Pmc_target=1.21.1
./gradlew :clean :build -Pmc_target=1.21.11
./gradlew :clean :build -Pmc_target=26.2
./gradlew :clean :build -Pmc_target=26.3
./gradlew :neoforge-1.21.1:clean :neoforge-1.21.1:build
./gradlew :neoforge-26.2:clean :neoforge-26.2:build
```
