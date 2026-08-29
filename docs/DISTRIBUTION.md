# Distribution plan

## Recommended order

1. **GitHub Releases** — canonical source, audit history, signed tag workflow, source JAR, binary JAR, and SHA-256 checksums.
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
- Minecraft version: 26.2
- Java version: 25
- Required dependency: Fabric API
- Source and issue tracker: the canonical GitHub repository, with visibility controlled by the owner

## Required description

FPS Tune is a client-side Fabric FPS and frame-time stability toolkit that applies opt-in controls to optional local rendering workloads. Its current controllers limit particle admission and can disable the weather render pass during extreme visual scenes, helping reduce stutter during heavy visual workloads. It starts disabled, uses a configurable default limit of 300 particles per tick, changes only local rendering, and does not modify packets, movement, combat, inventory, targeting, or clicks.

The listing must also state:

- F6 enables or disables the optimization.
- The configuration file is `config/fpstune.properties`.
- There is no telemetry, update checker, or custom networking.
- Server permission is not guaranteed; players must check current server rules.
- The project is not affiliated with Mojang, Microsoft, DonutSMP, or any server.

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
- Tag only Fabric and Minecraft 26.2 unless additional combinations are actually tested.
- Keep screenshots and explanatory media on the project page rather than inside the mod JAR.
- Expect project and file moderation; answer requests accurately and do not claim server approval.

## Multiplayer and DonutSMP

DonutSMP's published rules prohibit unauthorized modifications that provide unfair advantages and describe real-time client-integrity checks. FPS Tune does not contain the listed movement, inventory, health, radar, automation, or packet features, but its particle suppression is still a client modification.

Therefore:

- Do not claim that FPS Tune is allowed on DonutSMP.
- Keep it disabled there unless current official rules clearly allow it or staff give written approval for the exact public release.
- Publish the source and precise behavior so staff and players can audit it.
- Never add detection avoidance, mod-list hiding, integrity-check interference, or server-specific bypasses.
