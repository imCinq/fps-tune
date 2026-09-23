# FPS Tune v1.3.0

This folder tracks the four-target release candidate. It is release-tracking material, not a separate mod artifact.

## Target set

| Minecraft | Loader | Primary JAR |
| --- | --- | --- |
| 26.3 | Fabric | fps-tune-mc26.3-1.3.0.jar |
| 26.3 | NeoForge | fps-tune-neoforge-26.3-1.3.0.jar |
| 1.21.11 | Fabric | fps-tune-mc1.21.11-1.3.0.jar |
| 1.21.11 | NeoForge | fps-tune-neoforge-1.21.11-1.3.0.jar |

Quilt 26.3 is deferred. Minecraft 1.21.1 and 26.2 remain historical v1.2.x targets and are excluded.

## What changed

- Added isolated NeoForge targets for Minecraft 26.3 and 1.21.11.
- Refreshed the settings screen with grouped Performance and Visual controls, glyph-scoped icon fonts, and a centered Back button under Advanced settings.
- Kept client-side behavior opt-in and disabled by default.

## Hosted verification

The PR candidate at commit b8e855333bf507cd2beda9c7acf6c0def7dcccc9 passed:

- [CI run 35849111354](https://github.com/imCinq/fps-tune/actions/runs/35849111354): build and tests across repository profiles, bytecode inspection, audits, and artifact checks.
- [Four-target release assembly run 35849111388](https://github.com/imCinq/fps-tune/actions/runs/35849111388): built all four targets, checked primary and source JAR metadata, verified exactly four runtime JARs plus four source JARs, and checked SHA-256 sums.
- [26.3 client smoke run 35849111372](https://github.com/imCinq/fps-tune/actions/runs/35849111372): Fabric and NeoForge startup smoke passed.
- [1.21.11 client smoke run 35849111341](https://github.com/imCinq/fps-tune/actions/runs/35849111341): Fabric and NeoForge startup smoke passed.

These hosted checks cover launch and package structure; they do not replace visual confirmation of each exact packaged JAR.

## Manual status and remaining release decisions

- Manual checks have been reported good for Fabric 26.3, NeoForge 26.3, and Fabric 1.21.11.
- NeoForge 1.21.11 has a passing hosted startup and mod-registration smoke. A manual exact-JAR/native-settings-screen result has not been separately reported.
- The NeoForge 26.3 profile currently requires 26.3.0.7-beta; the manual Prism test used 26.3.0.8-beta. Decide whether a beta-based target belongs in a stable FPS Tune v1.3.0 release.
- Merge, tag creation, and public release still require explicit authorization. The tag workflow stages direct JAR assets and checksums in a non-latest draft release; it does not publish automatically.

The final v1.3.0 release must contain one primary JAR per target in the table, plus source JARs and checksums. It must not contain Quilt, 1.21.1, or 26.2 artifacts.
