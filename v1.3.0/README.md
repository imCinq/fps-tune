# FPS Tune v1.3.0

This folder tracks the v1.3.0 release workstream.

## Target sequence

The first implementation stage prepares:

- Minecraft 26.3 Fabric
- Minecraft 1.21.11 Fabric
- Minecraft 1.21.11 NeoForge

The later stage will add:

- Minecraft 26.3 NeoForge, after an official NeoForge 26.3 distribution is available
- Minecraft 26.3 Quilt, after stable Quilt 26.3 metadata and compatible QFAPI/QSL releases are available

Minecraft 1.21.1 and 26.2 remain historical v1.2.x targets during this staged migration. They are not included in the v1.3.0 release plan.

## Release rule

Do not publish a stable v1.3.0 tag until all five planned target artifacts pass hosted build, test, audit, packaging, bytecode, and required client smoke verification.

The build version remains unchanged until the complete v1.3.0 release candidate is ready. This folder is planning and release-tracking material, not a separate mod artifact.

## Artifact plan

| Minecraft | Loader | Planned artifact |
| --- | --- | --- |
| 26.3 | Fabric | fps-tune-mc26.3-<version>.jar |
| 1.21.11 | Fabric | fps-tune-mc1.21.11-<version>.jar |
| 1.21.11 | NeoForge | fps-tune-neoforge-1.21.11-<version>.jar |
| 26.3 | NeoForge | fps-tune-neoforge-26.3-<version>.jar |
| 26.3 | Quilt | fps-tune-quilt-26.3-<version>.jar |

The final release must contain exactly one matching JAR per supported loader/version target.