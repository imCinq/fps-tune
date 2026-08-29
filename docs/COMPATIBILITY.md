# FPS Tune compatibility

This document separates verified combinations from combinations that are plausible but not yet tested by the project. Do not treat a successful compile as proof of compatibility.

## Supported baseline

| Component | Supported baseline |
| --- | --- |
| Minecraft | 1.21.11 and 26.2; use the matching target JAR |
| Environment | Client only |
| Fabric Loader | 1.21.11: 0.18.6 or newer; 26.2: 0.19.3 or newer |
| Fabric API | Required; `0.141.6+1.21.11` or `0.158.0+26.2` in CI |
| Java | 21 or newer for 1.21.11; 25 or newer for 26.2 |
| Mod Menu | Optional; `17.0.0` for 1.21.11 or `20.0.0-beta.4` for 26.2 |

## Target profiles

| Minecraft | Fabric API | Mod Menu | Loom mode | Artifact |
| --- | --- | --- | --- | --- |
| 1.21.11 | `0.141.6+1.21.11` | `17.0.0` | Remapping | `fps-tune-mc1.21.11-<version>.jar` |
| 26.2 | `0.158.0+26.2` | `20.0.0-beta.4` | Non-remapping | `fps-tune-<version>.jar` |

FPS Tune is not a server mod and does not need to be installed on a server. It still may be disallowed by a server's rules because it is a client modification. Check the current rules before enabling it. No server, network, or anti-cheat approval is implied.

## Verified combinations

| Combination | Status | Notes |
| --- | --- | --- |
| FPS Tune with Fabric API on 1.21.11 | Graphical smoke verified | Required dependency; covered by the Java 21 CI build, unit tests, and a local 1.21.11 client startup smoke test. |
| FPS Tune with Fabric API on 26.2 | Graphical smoke verified | Required dependency; covered by the Java 25 CI build, unit tests, and a local 26.2 client startup smoke test. |
| FPS Tune without Mod Menu | Verified | The optional Mod Menu entrypoint is not required for startup. |
| FPS Tune with Mod Menu on 1.21.11 | Build verified | The optional entrypoint and native settings screen compile against Mod Menu 17.0.0; perform the manual click-through before reporting a UI compatibility result. |
| FPS Tune with Mod Menu on 26.2 | Build verified | The optional entrypoint and native settings screen are compiled and included; perform the manual click-through before reporting a UI compatibility result. |
| FPS Tune disabled | Verified | This is the default and leaves the render controllers inactive. |
| FPS Tune with particle admission enabled | Verified | Covered by deterministic budget tests and client smoke coverage. |
| FPS Tune with weather rendering disabled | Verified | The controller is independently gated by the master switch. |
| FPS Tune with Adaptive mode enabled | Unit-tested; graphical verification required | Uses the target-specific HUD bridge for local frame-time sampling and changes only the particle budget. |

## Companion mods

FPS Tune has not yet established a project-owned compatibility matrix for Sodium, Iris, ImmediatelyFast, Entity Culling, MoreCulling, or other rendering mods. They may coexist because FPS Tune targets narrow local render boundaries, but that is not a guarantee.

If a rendering combination causes a crash, visual regression, or unexpected frame pacing:

1. Reproduce with FPS Tune disabled using `F6`.
2. Retest with the smallest mod set that still shows the problem.
3. Keep the exact Minecraft target, matching FPS Tune artifact, loader, Fabric API, Java, FPS Tune, and companion-mod versions.
4. Report the relevant log excerpt and configuration, removing private paths, server addresses, coordinates, and personal information.

## Trust boundary

FPS Tune does not modify:

- packets or custom networking;
- server-side simulation or world state;
- movement, combat, inventory, targeting, rotation, or clicks;
- entity behavior, farms, or automation;
- anti-cheat checks or client-integrity reporting.

Its supported changes are local particle admission, optional Adaptive particle budgeting, and the optional local weather render pass. Visual trade-offs are opt-in through the master switch and settings.
