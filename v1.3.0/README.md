# FPS Tune v1.3.0

This folder tracks the v1.3.0 release workstream.

## Confirmed target set

The v1.3.0 release is planned for exactly these four loader/version targets:

- Minecraft 26.3 Fabric
- Minecraft 26.3 NeoForge
- Minecraft 1.21.11 Fabric
- Minecraft 1.21.11 NeoForge

Implementation work for the first three targets is present in the current PR; release artifacts still need to be built and verified. NeoForge 26.3 is the next implementation target. As of 2026-09-22, the available NeoForge 26.3 development baseline is `26.3.0.1-beta`; re-check the official distribution and pin the appropriate version when adding its isolated project and again for the release candidate. If the loader is still beta at v1.3.0 release time, confirm that release choice before publishing a stable FPS Tune release.

## Deferred targets

- Minecraft 26.3 Quilt is deferred to a later release and is not part of v1.3.0.
- Minecraft 1.21.1 and 26.2 remain available in historical v1.2.x releases, but are not v1.3.0 targets; no further development for them is planned as part of this release transition.

## Planned settings-screen update

Redesign FPS Tune's existing Mod Menu configuration screen, not the global Mods list:

- Group the master switch and profile under Performance; group rain/snow and the performance overlay under Visual options.
- Add small, native-style bitmap glyphs for the profile, precipitation, and overlay labels.
- Keep the existing options, Advanced settings, and draft-only-until-Done save behavior.

## Release rule

Do not publish a stable v1.3.0 tag until all four planned target artifacts pass hosted build, test, audit, packaging, bytecode, and required client smoke verification. Visually review the redesigned settings screen in-game on the applicable loader/version targets.

The build version remains unchanged until the complete four-target v1.3.0 release candidate is ready. This folder is planning and release-tracking material, not a separate mod artifact.

## Artifact plan

| Minecraft | Loader | Planned artifact |
| --- | --- | --- |
| 26.3 | Fabric | fps-tune-mc26.3-<version>.jar |
| 26.3 | NeoForge | fps-tune-neoforge-26.3-<version>.jar |
| 1.21.11 | Fabric | fps-tune-mc1.21.11-<version>.jar |
| 1.21.11 | NeoForge | fps-tune-neoforge-1.21.11-<version>.jar |

The final v1.3.0 release must contain exactly one matching JAR for each of these four targets. It must not include Quilt, 1.21.1, or 26.2 artifacts.