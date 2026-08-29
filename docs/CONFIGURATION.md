# FPS Tune configuration

FPS Tune is disabled by default. Its settings control only local client rendering and are stored in the Minecraft instance, not on a server.

## Defaults

| Setting | Default | Meaning |
| --- | --- | --- |
| `enabled` | `false` | Master switch for FPS Tune's render controls. |
| `particleAdmissionEnabled` | `true` | Applies the per-tick particle admission budget while the master switch is enabled. |
| `maxParticlesPerTick` | `300` | Maximum number of particles admitted by the client particle engine in one client tick when limiting is active. |
| `weatherRenderingEnabled` | `true` | Keeps the vanilla weather render pass enabled while FPS Tune is active. |

The particle budget is clamped to `0..10000`. A value of `0` admits no new particles during a client tick; it does not remove existing particles. A value of `10000` is the highest accepted setting and is not an unlimited mode.

## In-game controls

With Mod Menu installed:

1. Open the Mods screen.
2. Select FPS Tune and choose Configure.
3. Change the master switch, particle admission, weather rendering, or particle budget.
4. Choose Done to apply and save the draft settings.

Cancel and Escape discard the draft and leave the active configuration unchanged. Mod Menu is optional; FPS Tune runs without it.

The default `F6` keybind toggles only the master `enabled` switch and saves that change immediately. It does not change the individual controller settings.

## Configuration file

The file is:

```text
config/fpstune.properties
```

A saved file has this shape:

```properties
configVersion=1
enabled=false
particleAdmissionEnabled=true
maxParticlesPerTick=300
weatherRenderingEnabled=true
```

The file is created when settings are saved. Writes use a temporary file and an atomic move when the filesystem supports it. A malformed or unreadable file falls back to safe defaults and must not prevent Minecraft from launching. Invalid boolean and integer values fall back to their previous defaults; particle budgets outside the allowed range are clamped.

## What the settings change

When `enabled=true` and `particleAdmissionEnabled=true`, FPS Tune counts successful particle admissions for the current client particle tick. Once the configured budget is reached, later particle additions for that tick are rejected by the local particle engine. This changes visual admission only; it does not change packets, world simulation, entity logic, or server state.

When `enabled=true` and `weatherRenderingEnabled=false`, FPS Tune skips the local weather render pass. Rain and snow still exist in the world and are still simulated; only their client-side rendering is suppressed.

## Renamed-build migration

If `config/fpstune.properties` does not exist and the older private build's `config/coretune.properties` is present, FPS Tune imports the compatible values into the new file. The old file is left untouched. This migration path exists only for upgrading that earlier build and is not part of the public mod identity.
