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
