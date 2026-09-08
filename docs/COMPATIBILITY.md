# FPS Tune compatibility

This document separates hosted-build verification from graphical compatibility. A successful compile is not, by itself, proof that a client target is fully supported.

## Supported target baseline

| Minecraft | Loader | Java | Required dependency | Settings entry |
| --- | --- | --- | --- | --- |
| 1.21.1 | Fabric Loader 0.16.14+ | 21+ | Fabric API 0.116.15+1.21.1 | Optional Mod Menu 11.0.4 |
| 1.21.11 | Fabric Loader 0.18.6+ | 21+ | Fabric API 0.141.6+1.21.11 | Optional Mod Menu 17.0.0 |
| 26.2 | Fabric Loader 0.19.3+ | 25+ | Fabric API 0.158.0+26.2 | Optional Mod Menu 20.0.0-beta.4 |
| 26.2 | NeoForge 26.2.0.77+ | 25+ | NeoForge loader | Native NeoForge Mods screen |

Use exactly one matching FPS Tune JAR per instance. Do not install the Fabric 26.2 and NeoForge 26.2 JARs together.

## Artifact profiles

| Target | Build profile/project | Artifact |
| --- | --- | --- |
| 1.21.1 Fabric | `gradle/versions/1.21.1.properties` | `fps-tune-mc1.21.1-<version>.jar` |
| 1.21.11 Fabric | `gradle/versions/1.21.11.properties` | `fps-tune-mc1.21.11-<version>.jar` |
| 26.2 Fabric | `gradle/versions/26.2.properties` | `fps-tune-<version>.jar` |
| 26.2 NeoForge | `gradle/versions/26.2-neoforge.properties` and `neoforge-26.2` | `fps-tune-neoforge-26.2-<version>.jar` |

## Verification status

| Combination | Status | Notes |
| --- | --- | --- |
| All four target builds | Hosted build verified | CI compiles and tests the three Fabric targets and the isolated NeoForge target. |
| All four client-only audits | Hosted audit verified | Source, metadata, mixin, and repository-boundary checks pass in CI. |
| 26.2 NeoForge graphical client | Graphical verification required | The native settings screen, F6 toggle, diagnostics layer, particle control, and precipitation-only weather gate still need a client smoke test before claiming full graphical compatibility. |
| FPS Tune without optional settings integration | Verified by build path | The optional Fabric Mod Menu path is not required, and NeoForge uses its native Mods screen. |

## Runtime boundary

FPS Tune is client-only and disabled by default. It limits optional local particle admission and can suppress only the local rain/snow precipitation render pass. It does not change packets, world simulation, server state, entity behavior, automation, telemetry, or anti-cheat behavior.

Client-only does not mean server-approved. Follow the current rules of every multiplayer server before enabling any client modification.

## Troubleshooting

1. Confirm the exact Minecraft target and matching loader.
2. Keep only one FPS Tune JAR in the instance.
3. Reproduce with FPS Tune disabled using F6.
4. Report the exact target, loader, Java, FPS Tune, settings integration, and companion-mod versions with a sanitized log.