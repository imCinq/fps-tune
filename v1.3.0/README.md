# FPS Tune v1.3.0

This folder tracks the stable v1.3.0 release. It is release-tracking material, not a separate mod artifact.

## Stable target set

| Minecraft | Loader | Primary JAR |
| --- | --- | --- |
| 26.3 | Fabric | fps-tune-mc26.3-1.3.0.jar |
| 1.21.11 | Fabric | fps-tune-mc1.21.11-1.3.0.jar |
| 1.21.11 | NeoForge | fps-tune-neoforge-1.21.11-1.3.0.jar |

NeoForge 26.3 remains a beta preview. Its implementation, hosted build, and startup-smoke coverage stay in the repository, but its JAR is deferred until the NeoForge loader reaches stable. Quilt 26.3 is deferred. Minecraft 1.21.1 and 26.2 remain historical v1.2.x targets and are excluded.

## What changed

- Added the 1.21.11 NeoForge target alongside the existing Fabric 26.3 and Fabric 1.21.11 targets.
- Refreshed the settings screen with grouped Performance and Visual controls, glyph-scoped icon fonts, and a centered Back button under Advanced settings.
- Kept client-side behavior opt-in and disabled by default.

## Hosted verification

The existing candidate CI and client-smoke runs passed, but the previous release assembly contained four targets. The revised three-target release workflow must pass on the updated PR head and verify exactly three runtime JARs, three source JARs, and SHA-256 checksums. The PR's current GitHub Actions runs are the source of truth for the final candidate.

Hosted checks cover startup and package structure; they do not replace visual confirmation of each exact packaged JAR.

## Manual status and remaining release gates

- Manual checks have been reported good for Fabric 26.3, NeoForge 26.3 beta preview, and Fabric 1.21.11.
- NeoForge 1.21.11 has a passing hosted startup and mod-registration smoke. A manual exact-JAR and native-settings-screen result has not been separately reported.
- The stable release must contain exactly the three target JARs listed above, their source JARs, and checksums. Release assets are individual JARs and a checksum file.
- Merge, tag creation, and public release still require explicit authorization. The tag workflow stages direct JAR assets in a non-latest draft release; it does not publish automatically.
