# Changelog

## 1.1.1 - 2026-08-30

- Fixed the packaged mod icon so the classic FPS Tune logo is used in released JARs and launcher metadata.
- Preserved escaped metadata text during packaging so the generated Fabric metadata remains valid JSON.

## 1.1 - 2026-08-29

- Added optional nearby-particle prioritization with a reserved portion of the existing admission budget.
- Added an opt-in local diagnostics HUD with current-tick admission counters and controller status.
- Added Advanced Mod Menu controls for nearby priority, reserve, distance, diagnostics visibility, and automatic particle-limit adjustment.
- Added optional Adaptive particle budgeting with bounded frame-time feedback and cooldowns.
- Simplified the Mod Menu into a profile-first screen with plain-language labels and an optional Advanced settings screen.
- Added a long-form Mod Menu details description explaining the workload trade-off, dynamic adjustment, setup, and client-only boundaries.
- Made the settings screen compact and safe at Minecraft's minimum automatic GUI height.
- Added player-facing configuration, compatibility, and benchmarking references.
- Improved the README install/configuration flow and bug-report triage fields.
- Added separate build profiles and artifacts for Minecraft 1.21.11 and 26.2.
- Ported the client keybinding/chat bridge for Minecraft 1.21.11 while keeping the render controllers shared.

## 1.0.0 - 2026-08-29

- Added independent particle-admission and weather-render controllers behind the disabled-by-default master switch.
- Added an optional Mod Menu settings screen for the master switch, controller toggles, and particle budget.
- Added clickable homepage, source, and issue-tracker links to the Mod Menu details page.
- Added versioned configuration persistence with safe legacy defaults.
- Renamed the public mod identity to FPS Tune and added a non-destructive configuration-path migration.
- Generalized client-only auditing and documentation around optional local render workloads.

- Added a configurable per-tick particle-admission budget.
- Kept optimization disabled by default.
- Counted particles only when they reach vanilla queue insertion points.
- Added resilient, atomic configuration storage.
- Added boundary and 100,000-particle simulation tests.
- Added private-repository CI, release checksums, and client-only auditing.
- Removed personal names from package, metadata, and license ownership fields.
- Pinned GitHub Actions to reviewed immutable commit SHAs.
- Added agent instructions and a documented maintenance/release process.
- Standardized public attribution on `Cinq` and prohibited personal emails.
- Added a reviewed GitHub, Modrinth, and CurseForge distribution plan.
