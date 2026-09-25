# FPS Tune distribution

## Distribution policy

GitHub Releases is the canonical source. The applicable hosted release workflow builds one verified artifact for each supported target included in that release and publishes SHA-256 checksums from the same immutable release tag. Modrinth and CurseForge uploads must use those exact verified JARs; do not rebuild or rename them for platform submission.

## Canonical listing information

- Name: FPS Tune
- Author: Cinq
- Type: Mod
- Category: Optimization
- License: MIT
- Environment: Client only
- Minecraft targets: 1.21.1 Fabric and NeoForge, 1.21.11 Fabric and NeoForge, 26.2 Fabric and NeoForge, and 26.3 Fabric (NeoForge 26.3 is a beta preview and is not distributed)
- Java: 21 for 1.21.1 Fabric/NeoForge and 1.21.11 Fabric/NeoForge; 25 for 26.2 and 26.3 Fabric/NeoForge
- Required dependency: matching Fabric API for Fabric targets; NeoForge loader for NeoForge targets
- Source and issue tracker: the public GitHub repository

Use the copy-ready listing in [docs/MODRINTH_DESCRIPTION.md](MODRINTH_DESCRIPTION.md). It must identify the exact loader and Minecraft target for every file.

## GitHub Release v1.2.5

The release contains these five primary files:

| Minecraft | Loader | Primary file |
| --- | --- | --- |
| 1.21.1 | Fabric | `fps-tune-mc1.21.1-1.2.5.jar` |
| 1.21.1 | NeoForge | `fps-tune-neoforge-1.21.1-1.2.5.jar` |
| 1.21.11 | Fabric | `fps-tune-mc1.21.11-1.2.5.jar` |
| 26.2 | Fabric | `fps-tune-1.2.5.jar` |
| 26.2 | NeoForge | `fps-tune-neoforge-26.2-1.2.5.jar` |

Keep v1.1 historical. The five release artifacts are built from their release tags and accompanied by checksums. Publish only after hosted CI, audits, packaged metadata inspection, and the required graphical smoke tests pass.

## GitHub Release v1.2.5-mc26.3

Fabric 26.3 is published as a separate, non-draft target-specific release because the root `v1.2.5` release remains the five-artifact baseline. It contains:

- `fps-tune-mc26.3-1.2.5.jar` — the primary Fabric 26.3 binary.
- `fps-tune-mc26.3-1.2.5-sources.jar` — source support file.
- `SHA256SUMS.txt` — checksums for both JARs.

The target requires Java 25, Fabric Loader 0.19.5+, and Fabric API `0.160.6+26.3`. Mod Menu `21.0.0-beta.1` is an optional beta integration and is not required; the hosted client smoke workflow covers both Mod Menu present and absent. The 26.3 release workflow stages a draft before the exact tagged assets are reviewed and published. There is no NeoForge 26.3 artifact. The repository also publishes `v1.2.5-mc1.21.1` as a target-specific 1.21.1 compatibility release; it does not add another supported target or change the mod version.

## GitHub Release v1.3.0

The current stable [`v1.3.0`](https://github.com/imCinq/fps-tune/releases/tag/v1.3.0) release was published on 2026-09-25 and contains exactly these three primary JARs:

| Minecraft | Loader | Primary file |
| --- | --- | --- |
| 26.3 | Fabric | fps-tune-mc26.3-1.3.0.jar |
| 1.21.11 | Fabric | fps-tune-mc1.21.11-1.3.0.jar |
| 1.21.11 | NeoForge | fps-tune-neoforge-1.21.11-1.3.0.jar |

The tagged `.github/workflows/release.yml` builds those three targets, checks packaged loader metadata and resources, runs the target audits, and stages exactly three runtime JARs, three source JARs, and SHA-256 checksums. On a tag it creates a **draft, non-latest GitHub Release**; an authorized maintainer must review and publish the draft manually. Release assets are individual JARs and a checksum file, not a ZIP download.

NeoForge 26.3 remains a beta preview using Java 25, ModDevGradle 2.0.147, and the NeoForge 26.3.0.7-beta build baseline; the preview was also manually tested on 26.3.0.8-beta. Its implementation, build, and startup smoke remain in the project, but its release artifact is deferred until the NeoForge loader reaches stable.

Minecraft 26.3 Quilt is deferred. Minecraft 1.21.1 and 26.2 remain historical v1.2.x downloads and are excluded from v1.3.0. Reuse only the verified bytes from the tagged build for every destination.

## Modrinth and CurseForge upload plan

Create one platform file/version for each exact target. Mark the matching loader and Minecraft version only, mark the matching loader dependency as required, and keep Mod Menu optional for Fabric. On NeoForge, use the native Mods screen and do not list Fabric API as a dependency. Upload only the tested primary JAR; keep source JARs and checksums as GitHub release support files.

- Modrinth: use the three exact target files from `v1.3.0` with target-specific version identifiers such as `1.3.0+mc26.3-fabric`, `1.3.0+mc1.21.11-fabric`, and `1.3.0+mc1.21.11-neoforge`. Keep the historical 1.21.1 and 26.2 files from `v1.2.5`, identified as `1.2.5+mc1.21.1-fabric`, `1.2.5+mc26.2-neoforge`, and so on.
- CurseForge: tag 26.3 Fabric, 1.21.11 Fabric, and 1.21.11 NeoForge separately for v1.3.0, and keep the historical 1.21.1 and 26.2 v1.2.5 files; upload only the corresponding exact file.
- Never claim universal FPS gains or server approval. Explain the intentional visual trade-off and client-only boundary.

Do not automate Modrinth or CurseForge publishing until the owner explicitly approves the platform account, project ID, and required repository secrets. Never commit publishing tokens.
