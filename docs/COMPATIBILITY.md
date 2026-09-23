# FPS Tune compatibility

This document separates hosted-build verification from graphical compatibility. A successful compile is not, by itself, proof that a client target is fully supported.

## Supported target baseline

| Minecraft | Loader | Java | Required dependency | Settings entry |
| --- | --- | --- | --- | --- |
| 1.21.1 | Fabric Loader 0.16.14+ | 21+ | Fabric API 0.116.15+1.21.1 | Optional Mod Menu 11.0.4 |
| 1.21.1 | NeoForge 21.1.250+ | 21+ | NeoForge loader | Native NeoForge Mods screen |
| 1.21.11 | Fabric Loader 0.18.6+ | 21+ | Fabric API 0.141.6+1.21.11 | Optional Mod Menu 17.0.0 |
| 1.21.11 | NeoForge 21.11.45+ | 21+ | NeoForge loader | Native NeoForge Mods screen |
| 26.2 | Fabric Loader 0.19.3+ | 25+ | Fabric API 0.158.0+26.2 | Optional Mod Menu 20.0.0-beta.4 |
| 26.2 | NeoForge 26.2.0.77+ | 25+ | NeoForge loader | Native NeoForge Mods screen |
| 26.3 | Fabric Loader 0.19.5+ | 25+ | Fabric API 0.160.6+26.3 | Optional Mod Menu 21.0.0-beta.1 |
| 26.3 | NeoForge 26.3.0.7-beta+ | 25+ | NeoForge loader | Native NeoForge Mods screen |

Use exactly one matching FPS Tune JAR per instance. Do not install multiple FPS Tune target JARs together.

## Artifact profiles

| Target | Build profile/project | Artifact |
| --- | --- | --- |
| 1.21.1 Fabric | `gradle/versions/1.21.1.properties` | `fps-tune-mc1.21.1-<version>.jar` |
| 1.21.1 NeoForge | `gradle/versions/1.21.1-neoforge.properties` and `neoforge-1.21.1` | `fps-tune-neoforge-1.21.1-<version>.jar` |
| 1.21.11 Fabric | `gradle/versions/1.21.11.properties` | `fps-tune-mc1.21.11-<version>.jar` |
| 1.21.11 NeoForge | `gradle/versions/1.21.11-neoforge.properties` and `neoforge-1.21.11` | `fps-tune-neoforge-1.21.11-<version>.jar` |
| 26.2 Fabric | `gradle/versions/26.2.properties` | `fps-tune-<version>.jar` |
| 26.2 NeoForge | `gradle/versions/26.2-neoforge.properties` and `neoforge-26.2` | `fps-tune-neoforge-26.2-<version>.jar` |
| 26.3 Fabric | `gradle/versions/26.3.properties` (non-remapping Loom 1.17.20) | `fps-tune-mc26.3-<version>.jar` |
| 26.3 NeoForge | `gradle/versions/26.3-neoforge.properties` and `neoforge-26.3` | `fps-tune-neoforge-26.3-<version>.jar` |

## Verification status

| Combination | Status | Notes |
| --- | --- | --- |
| All eight target builds and audits | Hosted build, tests, packaging, and audits passed on the v1.3.0 source commit | The four Fabric and four isolated NeoForge profiles passed in [CI run 35840744457](https://github.com/imCinq/fps-tune/actions/runs/35840744457). |
| 26.3 Fabric client | Hosted smoke passed with Mod Menu present and absent | See [26.3 client smoke run 35840744415](https://github.com/imCinq/fps-tune/actions/runs/35840744415). |
| 26.3 NeoForge client | Hosted startup smoke passed | NeoForge client registration completed in [26.3 client smoke run 35840744415](https://github.com/imCinq/fps-tune/actions/runs/35840744415). |
| 1.21.11 Fabric client | Hosted smoke passed with Mod Menu present and absent | See [1.21.11 client smoke run 35840744435](https://github.com/imCinq/fps-tune/actions/runs/35840744435). |
| 1.21.11 NeoForge client | Hosted startup smoke passed | NeoForge client registration completed in [1.21.11 client smoke run 35840744435](https://github.com/imCinq/fps-tune/actions/runs/35840744435). |
| Manual candidate checks | Good reported for Fabric 26.3, NeoForge 26.3, and Fabric 1.21.11 | A manual NeoForge 1.21.11 check has not been reported. |
| Historical 1.21.1 and 26.2 clients | Excluded from v1.3.0 | Preserve the existing v1.2.x release assets. |
| FPS Tune without optional settings integration | Verified by build path | Mod Menu is optional on Fabric; NeoForge uses its native Mods screen. |

## Runtime boundary

FPS Tune is client-only and disabled by default. It limits optional local particle admission and can suppress the local rain/snow streaks and the landing splash particles they spawn, while weather simulation and rain/snow sounds continue. It does not change packets, world simulation, server state, entity behavior, automation, telemetry, or anti-cheat behavior.

Client-only does not mean server-approved. Follow the current rules of every multiplayer server before enabling any client modification.

## Troubleshooting

1. Confirm the exact Minecraft target and matching loader.
2. Keep only one FPS Tune JAR in the instance.
3. Reproduce with FPS Tune disabled using F6.
4. Report the exact target, loader, Java, FPS Tune, settings integration, and companion-mod versions with a sanitized log.

## Fabric 26.3 release verification

The isolated `26.3` build profile ships on its own release train. The current [`v1.2.5-mc26.3`](https://github.com/imCinq/fps-tune/releases/tag/v1.2.5-mc26.3) release is published as a non-draft target-specific release. The hosted CI matrix verifies compilation, tests, audits, and 26.3 mixin evidence; the separate real-client workflow covers startup and settings integration with Mod Menu present and absent. The port preserves the common particle admission, nearby reserve, Adaptive controller, local configuration, and disabled-by-default behavior. Existing targets are unchanged.

The 26.3 release workflow validates the exact tagged JAR and checksums but does not itself run graphical client tests. For future 26.3 releases, repeat the weather-off, F6, settings, HUD cache, Adaptive, menu/disconnect, shutdown, and Mod Menu-present/absent checks in an approved remote client environment before publication. Mod Menu `21.0.0-beta.1` remains optional and is not required to run FPS Tune.
