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

At source commit f2cc37df19fc21791f4ccda132fc5d7dab53007a, the four-target build, tests, audits, package checks, and bytecode inspection passed in [CI run 35840744457](https://github.com/imCinq/fps-tune/actions/runs/35840744457). Fabric 26.3 and NeoForge 26.3 startup smoke passed in [run 35840744415](https://github.com/imCinq/fps-tune/actions/runs/35840744415); Fabric 1.21.11 and NeoForge 1.21.11 startup smoke passed in [run 35840744435](https://github.com/imCinq/fps-tune/actions/runs/35840744435). Those checks used hosted development launchers and do not replace manual verification of the exact packaged JARs.

Manual client checks have been reported good for Fabric 26.3, NeoForge 26.3, and Fabric 1.21.11. NeoForge 1.21.11 passed hosted startup and mod-registration smoke; a manual exact-JAR/native-settings-screen result has not been reported.

## Remaining release gates

- Confirm the release workflow and documentation changes pass on the updated PR head.
- Record the exact-JAR NeoForge 1.21.11 check, including its native settings screen.
- Re-check the official NeoForge 26.3 version at release-candidate time. The branch builds against 26.3.0.7-beta; the manual Prism test used 26.3.0.8-beta. If that target still requires a beta loader, confirm whether it belongs in a stable FPS Tune release.
- Merge and tag only after review. The tag workflow stages a draft, non-latest GitHub Release for manual review; public publication remains separate.

The final v1.3.0 release must contain one primary JAR per target in the table, plus source JARs and checksums. It must not contain Quilt, 1.21.1, or 26.2 artifacts.
