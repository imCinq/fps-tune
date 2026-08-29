# Contributing

FPS Tune targets Minecraft 26.2 and Java 25.

- Keep it client-only and limited to optional local rendering workloads and render scheduling.
- Do not add telemetry, custom networking, packet manipulation, combat logic, movement changes, inventory automation, targeting, or click simulation.
- Keep the optimization disabled by default.
- Keep Mod Menu integration optional and local; use native client widgets unless a reviewed dependency is necessary.
- Add tests for bug fixes and behavior changes.
- Update `PRIVACY.md` if any new data is read, stored, or transmitted.

Verify changes with:

```sh
./gradlew clean build
./scripts/audit-client-only.sh
./scripts/audit-repository.sh
```

Mixin changes must also be checked against the affected Minecraft 26.2 client bytecode and tested in a graphical client.
