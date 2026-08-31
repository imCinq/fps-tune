# Distribution plan

![FPS Tune logo](../src/main/resources/assets/fpstune/icon.png)

## Recommended order

1. **GitHub Releases** — canonical source, audit history, annotated tags with verified target commits, source JARs, binary JARs, and SHA-256 checksums.
2. **Modrinth** — primary Minecraft discovery and launcher installation after the exact release JAR passes a graphical test.
3. **CurseForge** — optional additional reach after the Modrinth listing is approved and the project has a unique icon.

Do not automate Modrinth or CurseForge publishing until the owner explicitly approves the platform account, project ID, and required repository secrets. Never commit publishing tokens.

## Canonical listing information

- Name: FPS Tune
- Author: Cinq
- Type: Mod
- Category: Optimization
- License: MIT
- Loader: Fabric
- Environment: Client required; server unsupported
- Minecraft versions: 1.21.1, 1.21.11, and 26.2, with one matching JAR per version
- Java versions: 21 for 1.21.1 and 1.21.11, and 25 for 26.2
- Required dependency: the matching Fabric API release
- Source and issue tracker: the canonical public GitHub repository

## Required description

Use the copy-ready listing in [docs/MODRINTH_DESCRIPTION.md](MODRINTH_DESCRIPTION.md). It is structured for a first-time reader: what FPS Tune changes, what it never changes, default behavior, controls, supported versions, measured results, compatibility, trade-offs, troubleshooting, and privacy.

Use the FPS Tune logo as the listing icon and upload a human-created gallery image only after the final artwork has passed the platform's content rules. The current replacement icon is an AI-generated draft and is suitable for repository review, but it is not yet cleared for Modrinth branding under the current AI policy; replace it with human-authored artwork before submission, or disclose it and confirm that the platform accepts it. The listing should link only to public support pages that ordinary players can open without authentication.

## Modrinth version upload plan

Create one Modrinth version for each exact Minecraft target. Keep the internal mod version at `1.1.1`, and use target-specific Modrinth version identifiers so the two files can coexist in one project:

| Minecraft | Modrinth version number | Primary file | Loader | Environment |
| --- | --- | --- | --- | --- |
| 1.21.1 | `1.1.1+mc1.21.1` | `fps-tune-mc1.21.1-1.1.1.jar` | Fabric | Client |
| 1.21.11 | `1.1.1+mc1.21.11` | `fps-tune-mc1.21.11-1.1.1.jar` | Fabric | Client |
| 26.2 | `1.1.1+mc26.2` | `fps-tune-1.1.1.jar` | Fabric | Client |

For each upload, mark the matching Fabric API as required, Mod Menu as optional, use the matching Minecraft version only, and upload only the tested primary JAR. Use `beta` for the first platform release until the graphical smoke test and moderation pass are complete. The changelog should identify the exact Minecraft target, state that FPS Tune is disabled by default and client-only, and mention the particle, Adaptive, and weather controls without implying a universal FPS increase.

For the Minecraft 1.21.1 patch, keep the internal version at `1.1.1` and publish the annotated GitHub tag `v1.1.1-mc1.21.1`. The verified 1.21.1 JARs are also attached to the existing `v1.1.1` GitHub release alongside the 1.21.11 and 26.2 artifacts; the historical `v1.1.1` tag and source remain unchanged.

Because the GitHub repository is public, verify that source, issue, license, release, and changelog links work without authentication before submitting the listing. Do not link to temporary raw-file URLs or private-only support pages.

### Measured before/after results for the listing

These real local stress-test results may be included in the project description. The compact summary should show both average FPS and p95 frame time before and after:

| Minecraft | Avg FPS before | Avg FPS after | FPS change | p95 frame time before | p95 frame time after | p95 change |
| --- | ---: | ---: | ---: | ---: | ---: | ---: |
| 1.21.11 | 210.10 FPS | 215.17 FPS | +2.4% | 6.65 ms | 5.63 ms | -15.3% |
| 26.2 | 78.69 FPS | 102.78 FPS | +30.6% | 17.61 ms | 14.39 ms | -18.3% |

Before means FPS Tune was off; after means it was on. The p95 improvement is the part most likely to be visible: it means the longer frames were less severe, which can make motion look and feel smoother during a heavy scene. Test method: Apple M2, macOS 26.6.2, Java 25, OpenGL/Metal compatibility driver, Fancy graphics, render distance 16, simulation distance 12, fullscreen off, VSync off, 240 FPS cap, no shaders/resource packs/companion mods. A fixed peaceful creative world (seed `123456789`) received one burst of 10,000 long-lived `FLAME` particles. Four phases ran in one client process (off/on/off/on), with 120 warm-up frames and 600 measured render-loop intervals per phase; enabled phases used the default 300-particle admission budget. These results are machine- and workload-specific, and the enabled case intentionally shows fewer particles. Full conditions and interpretation are in [docs/BENCHMARKING.md](BENCHMARKING.md).

Do not publish placeholder numbers, call these results a universal FPS guarantee, or transfer results from one Minecraft version to the other.

Do not advertise FPS Tune as an anti-cheat bypass, a DonutSMP-approved mod, a competitive advantage, or a guaranteed FPS increase.

## Modrinth checklist

- Use the clear English description above and a plain-text explanation of what the mod does, why it is useful, and the critical server-permission limitation.
- Upload only the tested release JAR as the primary file.
- Mark Fabric API as required and select only tested Minecraft and Fabric versions.
- Mark the project client-side only and choose the Optimization category.
- Link the public source, issue tracker, MIT license, and matching changelog.
- Submit the first release as beta until moderation and the final graphical smoke test are complete.

FPS Tune appears compatible with Modrinth's content rules because its disclosed function is a narrow performance limit and it contains none of the specifically prohibited cheat functions. Moderators make the final decision; do not alter or obscure the description to influence review.

## CurseForge checklist

- Use a unique project name and icon, English summary, complete English description, correct Mod category, Optimization category, and MIT license.
- Upload the same tested JAR used for GitHub and Modrinth.
- Tag only Fabric and Minecraft 1.21.1, 1.21.11, and 26.2 after each exact artifact has passed the project verification checklist.
- Keep screenshots and explanatory media on the project page rather than inside the mod JAR.
- Expect project and file moderation; answer requests accurately and do not claim server approval.

## Multiplayer and DonutSMP

DonutSMP's published rules prohibit unauthorized modifications that provide unfair advantages and describe real-time client-integrity checks. FPS Tune does not contain the listed movement, inventory, health, radar, automation, or packet features, but its particle suppression is still a client modification.

Therefore:

- Do not claim that FPS Tune is allowed on DonutSMP.
- Keep it disabled there unless current official rules clearly allow it or staff give written approval for the exact public release.
- Publish the source and precise behavior so staff and players can audit it.
- Never add detection avoidance, mod-list hiding, integrity-check interference, or server-specific bypasses.
