# Changelog

## FPS Tune v1.2.1

- 🧱 Fixed disabling Rain and Snow on current targets from also hiding Map/World Border effects by limiting the weather gate to precipitation rendering only.

## FPS Tune v1.2.0 - 2026-09-01

- ⚡ Cached per-tick particle-controller state to reduce repeated configuration work during admissions.
- 🚫 Rejected particles immediately once the current total budget is full, avoiding unnecessary nearby classification.
- 🛡️ Made the nearby reserve follow the current budget so low Adaptive budgets retain general particle capacity.
- 🧰 Fixed Advanced settings reset behavior so it no longer overwrites the master switch, weather, or diagnostics choices.
- 📊 Skipped detailed admission counters unless the diagnostics overlay is enabled.
- 🎯 Made Adaptive mode lower the budget only when slow frames coincide with particle pressure; unrelated slow frames now hold the budget.
- 📈 Added lightweight pressure tracking for Adaptive mode without enabling detailed HUD counters.
- 🎯 Added an Auto Adaptive target that follows Minecraft's configured FPS cap, with the numeric target retained as a fallback.
- 🚨 Added a pressure-gated emergency response that cuts the particle budget by 25% after sustained frame times above 2x the target.
- 🧮 Removed the temporary center-vector allocation from nearby-particle classification while preserving the existing bounding-box semantics.
- 🧭 Simplified Advanced Mod Menu organization with grouped, dependency-aware controls and a compact Auto target selector.

- ✅ Added regression coverage for cached admission state, dynamic reserves, and scoped settings reset behavior.

## 1.1.1 for Minecraft 1.21.1 - 2026-08-31

- Added the Minecraft 1.21.1 compatibility profile using the remapping Loom setup, Java 21, Fabric Loader 0.16.14, Fabric API 0.116.15+1.21.1, and Mod Menu 11.0.4.
- Added the 1.21.1 keybinding, HUD, and weather-render bridges while keeping the particle controller shared with the other targets.
- Added the Minecraft 1.21.1 binary and sources JARs to the existing `v1.1.1` GitHub release alongside the 1.21.11 and 26.2 artifacts; the historical `v1.1.1` tag and source remain unchanged.

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
