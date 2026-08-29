# FPS Tune

FPS Tune is a client-side Fabric FPS and frame-time stability mod for Minecraft. It helps reduce long frame-time spikes during unusually heavy particle and weather scenes by limiting optional local rendering work.

FPS Tune is disabled by default. It changes only what your client renders; it does not change the world, server, gameplay, or network traffic.

## What it does

- Limits new particle admissions per client tick when enabled. The default budget is 300 particles per client tick.
- Can disable the local rain and snow render pass when you explicitly choose that visual trade-off.
- Provides a vanilla settings screen through the optional Mod Menu integration.
- Provides `F6` as a quick toggle for the master switch.

## What it never does

- No custom networking, packet changes, telemetry, analytics, update checker, or remote configuration.
- No movement, combat, inventory, targeting, rotation, click automation, or other gameplay logic.
- No server simulation, world-state changes, entity behavior changes, or anti-cheat evasion.

Client-only does not mean server-approved. Check the current rules of every multiplayer server before enabling any client modification.

## Installation

1. Install Fabric Loader and the Fabric API version matching your Minecraft version.
2. Download the FPS Tune file for the exact Minecraft version you are running.
3. Put that one file in the instance's `mods` folder and launch Minecraft.

Supported targets currently have separate files:

| Minecraft | File |
| --- | --- |
| 1.21.11 | `fps-tune-mc1.21.11-<version>.jar` |
| 26.2 | `fps-tune-<version>.jar` |

Do not install both target files in the same instance. Mod Menu is optional; FPS Tune runs without it.

## Settings

FPS Tune starts disabled. Press `F6` to toggle the master switch, or open `FPS Tune → Configure` from Mod Menu.

| Setting | Default | Meaning |
| --- | --- | --- |
| Master switch | Off | Enables or disables FPS Tune's local render controls. |
| Particle admission | On | Applies the particle budget while the master switch is on. |
| Particle budget | 300 | Maximum new particles admitted per client tick, clamped to `0..10000`. |
| Weather rendering | On | Keeps the vanilla rain and snow render pass enabled. |

Settings are stored locally in `config/fpstune.properties`. The master switch, particle admission, and weather rendering controls are independent, so you can keep weather visible while limiting particles or choose to suppress weather during an especially heavy scene.

## Performance results

These are real measurements from one controlled local stress test, not a universal FPS guarantee. The test used an Apple M2, macOS 26.6.2, Java 25, no shaders, resource packs, Sodium, or other companion mods, Fancy graphics, render distance 16, simulation distance 12, VSync off, and a 240 FPS cap. A fixed peaceful creative world received one burst of 10,000 long-lived `FLAME` particles. Each target alternated FPS Tune off/on/off/on in one client process, with 120 warm-up frames and 600 measured render-loop intervals per phase. Enabled phases used the default 300-particle budget.

| Minecraft | Avg FPS before | Avg FPS after | FPS change | p95 frame time before | p95 frame time after | p95 change |
| --- | ---: | ---: | ---: | ---: | ---: | ---: |
| 1.21.11 | 210.10 | 215.17 | +2.4% | 6.65 ms | 5.63 ms | -15.3% |
| 26.2 | 78.69 | 102.78 | +30.6% | 17.61 ms | 14.39 ms | -18.3% |

Before means FPS Tune was off; after means it was on. Average FPS is useful, but p95 frame time is the metric most directly related to perceived smoothness: it describes the longer frames at the tail of the run. In this stress test, p95 improved by 15.3% and 18.3%, meaning those longer frames were substantially less severe and motion could look noticeably smoother even when the average-FPS gain was smaller. The enabled case intentionally renders fewer particles.

Results depend on hardware, drivers, Minecraft version, companion mods, and workload. Do not treat them as a guaranteed multiplier or transfer one version's result to another.

## Compatibility

FPS Tune is designed to coexist with broad rendering optimizers, but a project-owned compatibility matrix for Sodium, Iris, ImmediatelyFast, Entity Culling, MoreCulling, and other companion mods is still being established. If you report a problem, include the exact Minecraft target, Fabric Loader, Fabric API, Java, FPS Tune, Mod Menu, and companion-mod versions.

## Troubleshooting

- Reproduce the issue with FPS Tune disabled using `F6`.
- If the issue disappears, re-enable only the relevant controller and lower the particle budget if necessary.
- Confirm that the installed file matches your Minecraft version and that only one FPS Tune file is installed.
- Report reproducible issues with a sanitized log and exact versions. Do not include account credentials, private server details, or personal filesystem paths.

## License and privacy

FPS Tune is released under the MIT License. It has no telemetry, analytics, custom networking, or in-mod updater. It is not affiliated with Mojang, Microsoft, or any multiplayer server.
