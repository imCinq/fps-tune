# Contributing

CoreTune targets Minecraft 26.2 and Java 25.

- Keep it client-only and limited to particle rendering performance.
- Do not add telemetry, custom networking, packet manipulation, combat logic, movement changes, inventory automation, targeting, or click simulation.
- Keep the optimization disabled by default.
- Add tests for bug fixes and behavior changes.
- Update `PRIVACY.md` if any new data is read, stored, or transmitted.

Verify changes with:

```sh
./gradlew clean build
./scripts/audit-client-only.sh
./scripts/audit-repository.sh
```

Mixin changes must also be checked against Minecraft 26.2's `ParticleEngine.add` bytecode and tested in a graphical client.
