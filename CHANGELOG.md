# Changelog

## FPS Tune v1.2.4

-ø>é Added a Minecraft **1.21.1 NeoForge** target using Java 21 and NeoForge 21.1.250.
-ø=þ Added native NeoForge startup, F6 keybinding, configuration screen, diagnostics HUD, resource-reload handling, and the existing 1.21.1 particle/weather mixins.
- ÿê Added hosted build, packaging, metadata, and client-only audit coverage while keeping graphical smoke testing required before release.


## FPS Tune v1.2.3

- ÿé Added a parallel Minecraft 26.2 NeoForge target while preserving the existing 1.21.1 Fabric, 1.21.11 Fabric, and 26.2 Fabric targets.
-øÿà Added NeoForge-native client startup, F6 keybinding, diagnostics HUD, configuration-screen integration, resource-reload cache invalidation, and mixin registration.
- ýæ Added a fourth 1.2.3 release artifact: `fps-tune-neoforge-26.2-1.2.3.jar`.
-ø>ê Added hosted CI, client-only audits, packaged metadata checks, and release wiring for all four target artifacts.
-  Kept the NeoForge build client-only, disabled by default, and limited to the same optional particle and precipitation rendering controls.

## FPS Tune v1.2.2

-øÿ' Fixed disabling Rain and Snow so Map/World Border effects remain visible on Minecraft 1.21.11 and 26.2.
- ÿ¯ Moved the modern weather gate onto the precipitation renderer instead of cancelling the full weather pass.
-øÿé Added separate version-specific weather mixins for the 1.21.11 and 26.2 render signatures.
-' Kept the Minecraft 1.21.1 weather bridge unchanged.

## FPS Tune v1.2.1

- ý Fixed disabling Rain and Snow on current targets from also hiding Map/World Border effects by limiting the weather gate to precipitation rendering only.

## FPS Tune v1.2.0 - 2026-09-01

- ¡ Cached per-tick particle-controller state to reduce repeated configuration work during admissions.
-øÿ« Rejected particles immediately once the current total budget was full, avoiding unnecessary nearby classification.
-øÿ¯ Made the nearby reserve follow the current budget so low Adaptive budgets retain general particle capacity.
- ÿþ Fixed Advanced settings reset behavior so it no longer overwrites the master switch, weather, or diagnostics choices.
-øýÊ Skipped detailed admission counters unless the diagnostics overlay is enabled.
-øÿà Made Adaptive mode lower the budget only when slow frames coincide with particle pressure.
-øýÈ Added an Auto Adaptive target that follows Minecrafts configured FPS cap, with the numeric target retained as a fallback.
- ÿ¨ Added a pressure-gated emergency response that cuts the particle budget by 25% after sustained severe frame times.
- ÿù Removed the temporary center-vector allocation from nearby-particle classification while preserving existing bounding-box semantics.
- ÿð Simplified Advanced Mod Menu organization with grouped, dependency-aware controls and a compact Auto target selector.
-  Added regression coverage for cached admission state, dynamic reserves, and scoped settings reset behavior.

## FPS Tune v1.1.1 for Minecraft 1.21.1 - 2026-08-31

-ø>é Added the Minecraft 1.21.1 compatibility profile using remapping Loom, Java 21, Fabric Loader 0.16.14, Fabric API 0.116.15+1.21.1, and Mod Menu 11.0.4.
- ÿþ Added the 1.21.1 keybinding, HUD, and weather-render bridges while keeping the particle controller shared with the other targets.
-øýæ Added the 1.21.1 binary and sources JARs to the existing v1.1.1 GitHub release alongside the 1.21.11 and 26.2 artifacts; the historical v1.1.1 tag and source remain unchanged.

## FPS Tune v1.1.1 - 2026-08-30

- ÿ¨ Fixed the packaged mod icon so the classic FPS Tune logo is used in released JARs and launcher metadata.
-ø>þ Preserved escaped metadata text during packaging so the generated Fabric metadata remains valid JSON.

## FPS Tune v1.1 - 2026-08-29

- ÿ¯ Added optional nearby-particle prioritization with a reserved portion of the existing admission budget.
-øýÊ Added an opt-in local diagnostics HUD with current-tick admission counters and controller status.
-ø>ð Added Advanced Mod Menu controls for nearby priority, reserve, distance, diagnostics visibility, and automatic particle-limit adjustment.
-øÿà Added optional Adaptive particle budgeting with bounded frame-time feedback and cooldowns.
-øÿù Simplified the Mod Menu into a profile-first screen with plain-language labels and an optional Advanced settings screen.
-øýÖ Added a long-form Mod Menu details description explaining the workload trade-off, dynamic adjustment, setup, and client-only boundaries.
-ø=Ð Made the settings screen compact and safe at Minecrafts minimum automatic GUI height.
-øýÝ Added player-facing configuration, compatibility, and benchmarking references.
-øý' Improved the README install/configuration flow and bug-report triage fields.
-ø=æ Added separate build profiles and artifacts for Minecraft 1.21.11 and 26.2.
-ø>é Ported the client keybinding/chat bridge for Minecraft 1.21.11 while keeping the render controllers shared.

## FPS Tune v1.0.0 - 2026-08-29

-ø<ÿ Added independent particle-admission and weather-render controllers behind the disabled-by-default master switch.
-ø>ð Added an optional Mod Menu settings screen for the master switch, controller toggles, and particle budget.
- ý Added clickable homepage, source, and issue-tracker links to the Mod Menu details page.
- ý¾ Added versioned configuration persistence with safe legacy defaults.
-ø>ª Renamed the public mod identity to FPS Tune and added a non-destructive configuration-path migration.
-øÿê Added boundary and 100,000-particle simulation tests.
- ý Added private-repository CI, release checksums, and client-only auditing.
-øÿù Removed personal names from package, metadata, and license ownership fields.
-ø=Ì Pinned GitHub Actions to reviewed immutable commit SHAs.
-øÿ Added agent instructions and a documented maintenance/release process.
-' Standardized public attribution on Cinq and prohibited personal emails.
- ÿ Added a reviewed GitHub, Modrinth, and CurseForge distribution plan.