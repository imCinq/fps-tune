# Contributing

FPS Tune targets Minecraft 1.21.11 with Java 21 and Minecraft 26.2 with Java 25. Keep compatibility-sensitive work aligned with the matching version profile.

- Keep it client-only and limited to optional local rendering workloads and render scheduling.
- Do not add telemetry, custom networking, packet manipulation, combat logic, movement changes, inventory automation, targeting, or click simulation.
- Keep the optimization disabled by default.
- Keep Mod Menu integration optional and local; use native client widgets unless a reviewed dependency is necessary.
- Add tests for bug fixes and behavior changes.
- Update `PRIVACY.md` if any new data is read, stored, or transmitted.

Verification:

Open a pull request or trigger the GitHub Actions CI workflow manually. The hosted matrix builds both Minecraft targets, runs the unit tests and repository audits, and uploads the verified artifacts. Do not install, download, or run Java, any JDK, Gradle, the Gradle Wrapper distribution, or project dependencies on the owner's device.

Mixin changes must also be checked against the affected Minecraft client bytecode for each changed target and tested in a GitHub-hosted or other owner-approved remote graphical client environment.
