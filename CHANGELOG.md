# Changelog

## Unreleased

- Added player-facing configuration, compatibility, and benchmarking references.
- Improved the README install/configuration flow and bug-report triage fields.

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
