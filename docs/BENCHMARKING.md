# FPS Tune benchmarking

FPS Tune is intended to improve frame-time behavior during unusually heavy local visual workloads. A higher average FPS is not guaranteed, and a benchmark from one machine must not be presented as a universal result.

## What to measure

Record these metrics when the tool used supports them:

| Metric | Why it matters |
| --- | --- |
| Average FPS | Overall throughput, useful but incomplete. |
| 1% low and 0.1% low FPS | Captures sustained slow frames that average FPS hides. |
| p95/p99 frame time | Shows the long tail of stutter. |
| Hitch count | Count frames above a stated threshold, such as frame time over 50 ms. |
| Visual behavior | Record whether particles or weather were intentionally suppressed. |

Use the same measurement tool and frame-time threshold across every comparison. If only the in-game FPS counter is available, report that limitation.

## Keep comparisons fair

Record the following for every run:

- Minecraft, Fabric Loader, Fabric API, Java, and FPS Tune versions;
- operating system, CPU, GPU, memory, display resolution, refresh rate, and power mode;
- render distance, simulation distance, graphics settings, VSync/FPS cap, shaders, and resource packs;
- the complete companion-mod list;
- world seed or test world, player position, camera direction, weather, time of day, and test duration.

Keep FPS Tune installed for both baselines. Compare it disabled with `F6` against the same configuration enabled, rather than comparing unrelated instances.

## Observed local before/after run

The following measurements were captured on 2026-08-29 with a temporary local harness. The harness created a fixed peaceful creative overworld with seed `123456789`, warmed up the client, injected one burst of 10,000 long-lived `FLAME` particles into the client particle engine, and alternated four phases in one process: disabled, enabled, disabled, enabled. Each phase discarded 120 render frames, then measured 600 render-loop frame-start intervals. The enabled phases used the default `maxParticlesPerTick=300`; the disabled phases admitted the full burst. The table reports the median of the two phases for each state.

| Target | Before | After | Change |
| --- | ---: | ---: | ---: |
| Minecraft 1.21.11 | 210.10 | 215.17 | +2.4% |
| Minecraft 26.2 | 78.69 | 102.78 | +30.6% |

Before means FPS Tune was off; after means it was on. The secondary frame-time stability result was a p95 frame time of 6.65 ms off versus 5.63 ms on for Minecraft 1.21.11 (-15.3%), and 17.61 ms off versus 14.39 ms on for Minecraft 26.2 (-18.3%).

Test environment: Apple M2, 8 cores, 16 GB RAM, macOS 26.6.2, arm64, OpenGL through the Metal compatibility driver, Java 25, no shaders, no resource packs, no Sodium/Iris or other companion mods, Fancy graphics, render distance 16, simulation distance 12, fullscreen off, VSync off, 240 FPS cap. The 1.21.11 run used Fabric Loader 0.18.6, Fabric API 0.141.6+1.21.11, and Mod Menu 17.0.0. The 26.2 run used Fabric Loader 0.19.3, Fabric API 0.158.0+26.2, and Mod Menu 20.0.0-beta.4.

This is a deliberately extreme admission/render stress test, not a normal-gameplay promise. The enabled case visibly renders fewer particles by design, and the result is specific to this machine, driver, client build, and workload. It must not be presented as a universal FPS multiplier. The logged per-phase results are retained in the local client log used for the release review.

## Suggested scenarios

| Scenario | Baseline | Enabled case |
| --- | --- | --- |
| Normal exploration | FPS Tune disabled | FPS Tune enabled with default settings |
| Particle-heavy scene | Disabled, same particle source | Particle admission enabled at a recorded budget |
| Weather-heavy scene | Disabled with weather visible | Weather rendering disabled, if that visual trade-off is acceptable |
| Combined stress | Disabled | Test the particle and weather controllers separately, then together |

Warm up each run before recording. Use at least three repeated runs when practical and report the median, range, and any visible regression. Do not hide a visual trade-off behind an FPS number.

## Release-report template

```text
Minecraft / loader / API / Java:
FPS Tune version:
Hardware and display:
Companion mods:
World and scenario:
Graphics, render distance, shaders, resource packs:
Measurement tool and duration:

Disabled: average FPS / 1% low / p95 frame time / hitch count
Enabled:  average FPS / 1% low / p95 frame time / hitch count

Visual changes observed:
Crashes or warnings:
```

## Safety and interpretation

The default disabled state is the baseline for compatibility. During ordinary scenes, enabling FPS Tune should not be described as a guaranteed performance increase. During an extreme visual workload, a lower particle admission budget or disabled weather pass can reduce visual work at the cost of seeing fewer effects.

Keep benchmark captures free of private server information, personal filesystem paths, account names, and unrelated client telemetry. Performance evidence should explain the tested conditions rather than imply a universal FPS multiplier.
