# FPS Tune compatibility

This document separates verified combinations from combinations that are plausible but not yet tested by the project. Do not treat a successful compile as proof of compatibility.

## Supported baseline

| Component | Supported baseline |
| --- | --- |
| Minecraft | 26.2 |
| Environment | Client only |
| Fabric Loader | 0.19.3 or newer, with 0.19.3 used in CI and graphical smoke tests |
| Fabric API | Required; CI currently builds against `0.158.0+26.2` |
| Java | 25 or newer, with Java 25 used in CI |
| Mod Menu | Optional; `20.0.0-beta.4` is used for the settings-screen integration test |

FPS Tune is not a server mod and does not need to be installed on a server. It still may be disallowed by a server's rules because it is a client modification. Check the current rules before enabling it. No server, network, or anti-cheat approval is implied.

## Verified combinations

| Combination | Status | Notes |
| --- | --- | --- |
| FPS Tune with Fabric API | Verified | Required dependency; covered by build and client startup checks. |
| FPS Tune without Mod Menu | Verified | The optional Mod Menu entrypoint is not required for startup. |
| FPS Tune with Mod Menu | Build verified | The optional entrypoint and native settings screen are compiled and included; perform the manual click-through before reporting a UI compatibility result. |
| FPS Tune disabled | Verified | This is the default and leaves the render controllers inactive. |
| FPS Tune with particle admission enabled | Verified | Covered by deterministic budget tests and client smoke coverage. |
| FPS Tune with weather rendering disabled | Verified | The controller is independently gated by the master switch. |

## Companion mods

FPS Tune has not yet established a project-owned compatibility matrix for Sodium, Iris, ImmediatelyFast, Entity Culling, MoreCulling, or other rendering mods. They may coexist because FPS Tune targets narrow local render boundaries, but that is not a guarantee.

If a rendering combination causes a crash, visual regression, or unexpected frame pacing:

1. Reproduce with FPS Tune disabled using `F6`.
2. Retest with the smallest mod set that still shows the problem.
3. Keep the exact Minecraft, loader, Fabric API, Java, FPS Tune, and companion-mod versions.
4. Report the relevant log excerpt and configuration, removing private paths, server addresses, coordinates, and personal information.

## Trust boundary

FPS Tune does not modify:

- packets or custom networking;
- server-side simulation or world state;
- movement, combat, inventory, targeting, rotation, or clicks;
- entity behavior, farms, or automation;
- anti-cheat checks or client-integrity reporting.

Its supported changes are local particle admission and the optional local weather render pass. Visual trade-offs are opt-in through the master switch and settings.
