# FPS Tune distribution

## Distribution policy

GitHub Releases is the canonical source. The hosted release workflow builds one verified artifact for each supported target and publishes SHA-256 checksums for the same 1.2.3 release. Modrinth and CurseForge uploads must use those exact verified JARs; do not rebuild or rename them for platform submission.

## Canonical listing information

- Name: FPS Tune
- Author: Cinq
- Type: Mod
- Category: Optimization
- License: MIT
- Environment: Client only
- Minecraft targets: 1.21.1 Fabric, 1.21.11 Fabric, 26.2 Fabric, and 26.2 NeoForge
- Java: 21 for 1.21.1/1.21.11 Fabric; 25 for 26.2 Fabric and NeoForge
- Required dependency: matching Fabric API for Fabric targets; NeoForge loader for the NeoForge target
- Source and issue tracker: the public GitHub repository

Use the copy-ready listing in [docs/MODRINTH_DESCRIPTION.md](MODRINTH_DESCRIPTION.md). It must identify the exact loader and Minecraft target for every file.

## GitHub Release v1.2.3

The release contains these four primary files:

| Minecraft | Loader | Primary file |
| --- | --- | --- |
| 1.21.1 | Fabric | `fps-tune-mc1.21.1-1.2.3.jar` |
| 1.21.11 | Fabric | `fps-tune-mc1.21.11-1.2.3.jar` |
| 26.2 | Fabric | `fps-tune-1.2.3.jar` |
| 26.2 | NeoForge | `fps-tune-neoforge-26.2-1.2.3.jar` |

Keep v1.1 historical. The four 1.2.3 artifacts are built from the same release tag and accompanied by checksums. Publish only after hosted CI, audits, packaged metadata inspection, and the required graphical smoke tests pass.

## Modrinth and CurseForge upload plan

Create one platform file/version for each exact target. Mark the matching loader and Minecraft version only, mark the matching loader dependency as required, and keep Mod Menu optional for Fabric. On NeoForge, use the native Mods screen and do not list Fabric API as a dependency. Upload only the tested primary JAR; keep source JARs and checksums as GitHub release support files.

- Modrinth: use the exact four files above and target-specific version identifiers such as `1.2.3+mc1.21.1-fabric` and `1.2.3+mc26.2-neoforge`.
- CurseForge: tag 1.21.1 Fabric, 1.21.11 Fabric, 26.2 Fabric, and 26.2 NeoForge separately, then upload the corresponding exact file.
- Never claim universal FPS gains or server approval. Explain the intentional visual trade-off and client-only boundary.

Do not automate Modrinth or CurseForge publishing until the owner explicitly approves the platform account, project ID, and required repository secrets. Never commit publishing tokens.