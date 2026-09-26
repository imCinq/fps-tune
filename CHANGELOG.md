# Changelog

## FPS Tune v1.4.0 - 2026-09-26

- ⚙️ **New settings page.** The main screen now has a profile with a one-line explanation, and quick switches for particles, rain and snow, and the overlay. Advanced settings are in vanilla-style **Particles / Weather / Display** tabs, each with a Reset button, and rarely used values sit behind *Show more*. Clearer wording and icons throughout.
- 🎯 **Max particles on screen** (off by default): new particles are skipped once this many are showing. Particles already on screen are never removed.
- 🔭 **Hide far-away particles** (off by default): new particles beyond the chosen distance are skipped.
- 🚀 New **Maximum FPS** profile: a lower per-tick limit, a 2000-particle cap, and particles beyond 32 blocks skipped.
- 🌦️ **Rain and snow: Normal / Lighter / Hidden.** Lighter draws precipitation in a smaller circle around you and skips half the landing splashes. Your old on/off setting carries over.
- ⌨️ **F6 message:** a colored ON/OFF with a one-line summary of what changes, shown above the hotbar by default. It can also go to chat or be turned off.
- 🔊 Fixed hiding rain and snow on **1.21.11** so rain sounds keep playing, as they already did on 26.3.
- 🎨 Every setting now has its own icon, so the settings pages look consistent.
- 🔁 Profile button cycles through every profile again; Custom only appears when your settings are custom.
- ⎋ Escape on the main settings screen now saves, like vanilla option screens; Cancel still discards.
- 👁️ Far-away particle distance is measured from the camera view, so spectating a mob or player works as expected.
- 🗂️ Config version 5. Older settings files are upgraded automatically, and FPS Tune stays off by default.

## FPS Tune v1.3.0 - 2026-09-25

- 🧩 Added Minecraft **1.21.11 NeoForge** alongside Fabric 1.21.11 and Fabric 26.3. NeoForge 26.3 remains a beta preview and is deferred from the stable release until its loader reaches stable.
- 🎨 Refreshed the settings screen with grouped Performance and Visual options; scoped icon fonts to glyphs so labels stay readable, and centered Back below Advanced settings.
- 🧪 Added target-specific regression coverage and hosted build, test, audit, packaging, bytecode, and client-startup checks for the three-target stable release candidate; NeoForge 26.3 retains preview build and smoke coverage.

## FPS Tune v1.2.5 - 2026-09-17

- 🌧️ Fixed weather-off behavior on 1.21.1 so rain and snow landing splash particles are suppressed while weather sounds continue ([#42](https://github.com/imCinq/fps-tune/pull/42)).
- 🌧️ Applied the weather splash fix to 1.21.11/26.3 and documented the target-specific behavior ([#43](https://github.com/imCinq/fps-tune/pull/43)).
- 🌧️ Applied the weather splash fix to the 26.2 family while preserving rain and snow sounds ([#44](https://github.com/imCinq/fps-tune/pull/44)).
- 📦 Published the weather splash fix in the v1.2.5 release ([#45](https://github.com/imCinq/fps-tune/pull/45)).
- 🔊 Weather simulation and rain/snow sounds continue while weather-off hides local precipitation streaks and landing splash particles.

## FPS Tune v1.2.5 for Minecraft 26.3 Fabric - 2026-09-17

- 🧩 Published the separate target-specific release [`v1.2.5-mc26.3`](https://github.com/imCinq/fps-tune/releases/tag/v1.2.5-mc26.3) for Minecraft 26.3 using Java 25, Fabric Loader 0.19.5+, and Fabric API `0.160.6+26.3`.
- 📦 Published `fps-tune-mc26.3-1.2.5.jar`, its sources JAR, and `SHA256SUMS.txt`.
- 🧪 Hosted client smoke coverage exercises startup with Mod Menu present and absent; Mod Menu remains optional.


## FPS Tune v1.2.4

- 🧩 Added a Minecraft **1.21.1 NeoForge** target using Java 21 and NeoForge 21.1.250.
- 🛠️ Added native NeoForge startup, F6 keybinding, configuration screen, diagnostics HUD, resource-reload handling, and the existing 1.21.1 particle/weather mixins.
- 🧪 Added hosted build, packaging, metadata, and client-only audit coverage while keeping graphical smoke testing required before release.


## FPS Tune v1.2.3

- 🧩 Added a parallel Minecraft 26.2 NeoForge target while preserving the existing 1.21.1 Fabric, 1.21.11 Fabric, and 26.2 Fabric targets.
- 🛠️ Added NeoForge-native client startup, F6 keybinding, diagnostics HUD, configuration-screen integration, resource-reload cache invalidation, and mixin registration.
- 📦 Added a fourth 1.2.3 release artifact: `fps-tune-neoforge-26.2-1.2.3.jar`.
- 🧪 Added hosted CI, client-only audits, packaged metadata checks, and release wiring for all four target artifacts.
- ✅ Kept the NeoForge build client-only, disabled by default, and limited to the same optional particle and precipitation rendering controls.

## FPS Tune v1.2.2

- 🌧️ Fixed disabling Rain and Snow so Map/World Border effects remain visible on Minecraft 1.21.11 and 26.2.
- 🎯 Moved the modern weather gate onto the precipitation renderer instead of cancelling the full weather pass.
- 🧩 Added separate version-specific weather mixins for the 1.21.11 and 26.2 render signatures.
- ✅ Kept the Minecraft 1.21.1 weather bridge unchanged.

## FPS Tune v1.2.1

- 🐛 Fixed disabling Rain and Snow on current targets from also hiding Map/World Border effects by limiting the weather gate to precipitation rendering only.

## FPS Tune v1.2.0 - 2026-09-01

- ⚡ Cached per-tick particle-controller state to reduce repeated configuration work during admissions.
- 🚫 Rejected particles immediately once the current total budget was full, avoiding unnecessary nearby classification.
- 🎯 Made the nearby reserve follow the current budget so low Adaptive budgets retain general particle capacity.
- 🛠️ Fixed Advanced settings reset behavior so it no longer overwrites the master switch, weather, or diagnostics choices.
- 📊 Skipped detailed admission counters unless the diagnostics overlay is enabled.
- 🧠 Made Adaptive mode lower the budget only when slow frames coincide with particle pressure.
- 📈 Added an Auto Adaptive target that follows Minecraft’s configured FPS cap, with the numeric target retained as a fallback.
- 🚨 Added a pressure-gated emergency response that cuts the particle budget by 25% after sustained severe frame times.
- 🧹 Removed the temporary center-vector allocation from nearby-particle classification while preserving existing bounding-box semantics.
- 🧰 Simplified Advanced Mod Menu organization with grouped, dependency-aware controls and a compact Auto target selector.
- ✅ Added regression coverage for cached admission state, dynamic reserves, and scoped settings reset behavior.

## FPS Tune v1.1.1 for Minecraft 1.21.1 - 2026-08-31

- 🧩 Added the Minecraft 1.21.1 compatibility profile using remapping Loom, Java 21, Fabric Loader 0.16.14, Fabric API 0.116.15+1.21.1, and Mod Menu 11.0.4.
- 🛠️ Added the 1.21.1 keybinding, HUD, and weather-render bridges while keeping the particle controller shared with the other targets.
- 📦 Added the 1.21.1 binary and sources JARs to the existing v1.1.1 GitHub release alongside the 1.21.11 and 26.2 artifacts; the historical v1.1.1 tag and source remain unchanged.

## FPS Tune v1.1.1 - 2026-08-30

- 🎨 Fixed the packaged mod icon so the classic FPS Tune logo is used in released JARs and launcher metadata.
- 🧾 Preserved escaped metadata text during packaging so the generated Fabric metadata remains valid JSON.

## FPS Tune v1.1 - 2026-08-29

- 🎯 Added optional nearby-particle prioritization with a reserved portion of the existing admission budget.
- 📊 Added an opt-in local diagnostics HUD with current-tick admission counters and controller status.
- 🧰 Added Advanced Mod Menu controls for nearby priority, reserve, distance, diagnostics visibility, and automatic particle-limit adjustment.
- 🧠 Added optional Adaptive particle budgeting with bounded frame-time feedback and cooldowns.
- 🧹 Simplified the Mod Menu into a profile-first screen with plain-language labels and an optional Advanced settings screen.
- 📖 Added a long-form Mod Menu details description explaining the workload trade-off, dynamic adjustment, setup, and client-only boundaries.
- 📐 Made the settings screen compact and safe at Minecraft’s minimum automatic GUI height.
- 📝 Added player-facing configuration, compatibility, and benchmarking references.
- 🔧 Improved the README install/configuration flow and bug-report triage fields.
- 📦 Added separate build profiles and artifacts for Minecraft 1.21.11 and 26.2.
- 🧩 Ported the client keybinding/chat bridge for Minecraft 1.21.11 while keeping the render controllers shared.

## FPS Tune v1.0.0 - 2026-08-29

- 🎛️ Added independent particle-admission and weather-render controllers behind the disabled-by-default master switch.
- 🧰 Added an optional Mod Menu settings screen for the master switch, controller toggles, and particle budget.
- 🔗 Added clickable homepage, source, and issue-tracker links to the Mod Menu details page.
- 💾 Added versioned configuration persistence with safe legacy defaults.
- 🪪 Renamed the public mod identity to FPS Tune and added a non-destructive configuration-path migration.
- 🧪 Added boundary and 100,000-particle simulation tests.
- 🔒 Added private-repository CI, release checksums, and client-only auditing.
- 🧹 Removed personal names from package, metadata, and license ownership fields.
- 📌 Pinned GitHub Actions to reviewed immutable commit SHAs.
- 🤖 Added agent instructions and a documented maintenance/release process.
- ✅ Standardized public attribution on Cinq and prohibited personal emails.
- 🌐 Added a reviewed GitHub, Modrinth, and CurseForge distribution plan.
