# FPS Tune distribution

## Distribution policy

GitHub Releases is the canonical source. The hosted release workflow builds one verified artifact for each supported target and publishes SHA-256 checksums from the same immutable release tag. Modrinth and CurseForge uploads must use those exact verified JARs; do not rebuild or rename them for platform submission.

## Canonical listing information

- Name: FPS Tune
- Author: Cinq
- Type: Mod
- Category: Optimization
- License: MIT
- Environment: Client only
- Minecraft targets: 1.21.1 Fabric and NeoForge, 1.21.11 Fabric, and 26.2 Fabric and NeoForge
- Java: 21 for 1.21.1 Fabric/NeoForge and 1.21.11 Fabric; 25 for 26.2 Fabric/NeoForge
- Required dependency: matching Fabric API for Fabric targets; NeoForge loader for NeoForge targets
- Source and issue tracker: the public GitHub repository

Use the copy-ready listing in [docs/MODRINTH_DESCRIPTION.md](MODRINTH_DESCRIPTION.md). It must identify the exact loader and Minecraft target for every file.

## GitHub Release v1.2.3

The release contains these five primary files:

| Minecraft | Loader | Primary file |
| --- | --- | --- |
| 1.21.1 | Fabric | `fps-tune-mc1.21.1-1.2.3.jar` |
| 1.21.11 | Fabric | `fps-tune-mc1.21.11-1.2.3.jar` |
| 26.2 | Fabric | `fps-tune-1.2.3.jar` |
| 26.2 | NeoForge | `fps-tune-neoforge-26.2-1.2.3.jar` |
| 26.3 | Fabric | `fps-tune-mc26.3-<version>.jar` |

Keep v1.1 historical. The five release artifacts are built from their release tags and accompanied by checksums. Publish only after hosted CI, audits, packaged metadata inspection, and the required graphical smoke tests pass.

## Fabric 26.3 separate release (provisional, unreleased)

The Fabric 26.3 port is not yet release-verified. Keep `mod_version=1.2.4` and use the separate annotated tag `v1.2.4-mc26.3` only after the release PR is merged and the required checks are reviewed. `.github/workflows/release-26.3.yml` builds only this Fabric target and creates a **draft**, non-latest GitHub Release containing:

- `fps-tune-mc26.3-1.2.4.jar` — the primary Fabric 26.3 binary.
- `fps-tune-mc26.3-1.2.4-sources.jar` — source support file.
- `SHA256SUMS.txt` — checksums for both JARs.

The target requires Java 25 and matching Fabric Loader/Fabric API dependencies from `gradle/versions/26.3.properties`. Optional Mod Menu compatibility with final Minecraft 26.3 remains provisional; see [COMPATIBILITY.md](COMPATIBILITY.md). There is no NeoForge 26.3 artifact. Keep the default `mc_target=26.2`, existing artifact names, and existing releases unchanged; do not promote or attach these files to an older release.

The new release workflow does **not** itself enforce graphical testing. Before public publication, download the draft assets in an approved remote environment, verify `SHA256SUMS.txt`, inspect the packaged metadata, and complete the required graphical smoke tests using that exact binary, with and without Mod Menu. Record the tested checksum and evidence, and obtain maintainer approval before publishing the draft. Compilation or draft creation alone is not a support claim. Distribute only those same validated bytes; do not rebuild or rename them for later platform uploads.

## Modrinth and CurseForge upload plan

Create one platform file/version for each exact target. Mark the matching loader and Minecraft version only, mark the matching loader dependency as required, and keep Mod Menu optional for Fabric. On NeoForge, use the native Mods screen and do not list Fabric API as a dependency. Upload only the tested primary JAR; keep source JARs and checksums as GitHub release support files.

- Modrinth: use the exact five files above and target-specific version identifiers such as `1.2.3+mc1.21.1-fabric` and `1.2.3+mc26.2-neoforge`.
- CurseForge: tag 1.21.1 Fabric, 1.21.11 Fabric, 26.2 Fabric, and 26.2 NeoForge separately, then upload the corresponding exact file.
- Never claim universal FPS gains or server approval. Explain the intentional visual trade-off and client-only boundary.

Do not automate Modrinth or CurseForge publishing until the owner explicitly approves the platform account, project ID, and required repository secrets. Never commit publishing tokens.