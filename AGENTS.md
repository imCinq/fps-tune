# CoreTune agent instructions

These instructions apply to every automated coding agent working in this repository.

This file is intended to remain in the public repository. Keep it limited to non-secret project guidance. Private owner instructions, credentials, account details, and unpublished security information must never be added here or anywhere else in the repository.

## Public identity

- Use `Cinq` as the default public project and author name.
- A verified GitHub username supplied directly by the owner may be used where GitHub requires an account reference.
- `Codex` may be named only when attribution for automated assistance is explicitly requested.
- Never infer or publish the owner's legal name, personal email, location, device paths, account identifiers, or other personal information.
- Never add a personal contact email. Keep security and conduct reporting within GitHub or direct private contact initiated by the owner.

## Project contract

CoreTune is a narrow, client-only Fabric optimization for Minecraft 26.2. Its only gameplay-adjacent behavior is limiting new local particles admitted during each client tick. It must remain disabled by default.

Never add packet handling, custom networking, telemetry, analytics, update checks, remote configuration, combat logic, movement changes, inventory automation, targeting, rotation changes, click simulation, or server-rule evasion.

## Current baseline

- Mod version: `1.0.0`
- Minecraft: `26.2`
- Java: `25`
- Fabric Loader: `0.19.3` or newer
- Fabric API: `0.158.0+26.2`
- Gradle: `9.5.1`
- Main package: `dev.coretune`

Treat `gradle.properties` and `src/main/resources/fabric.mod.json` as the authoritative machine-readable versions.

## Required workflow

1. Read `README.md`, `PRIVACY.md`, `SECURITY.md`, and `docs/MAINTENANCE.md` before changing behavior or dependencies.
2. Inspect the relevant source and existing tests before editing.
3. Keep changes minimal and preserve the trust boundary.
4. Add or update deterministic tests for every behavior or bug fix.
5. Run `./gradlew clean build`, `./scripts/audit-client-only.sh`, and `./scripts/audit-repository.sh`.
6. For mixin changes, inspect the target Minecraft bytecode and perform a graphical `runClient` smoke test.
7. Update `CHANGELOG.md` for user-visible changes.

Do not push commits, create releases, publish artifacts, change repository visibility, or contact external services unless the repository owner explicitly requests it.

## Mixin rules

- Keep mixins client-only and listed explicitly in `coretune.mixins.json`.
- Preserve `ParticleEngine.add` cancellation at `HEAD` and count admission only at vanilla `Queue.add` invocation points.
- Do not use broad redirects, ordinal-dependent hooks without bytecode evidence, or silent fallback behavior.
- If the number or shape of vanilla queue insertion points changes, stop and redesign the hook with tests instead of forcing the old injection.

## Update rules

Never assume a new Minecraft, Fabric, Loom, Java, Gradle, or GitHub Action version is compatible. Verify official release metadata, update one compatibility layer at a time, build from a clean state, inspect mixin targets, and follow `docs/MAINTENANCE.md`.

GitHub Actions must remain pinned to full commit SHAs with a version comment. Dependabot updates require review and a green CI run before merge.

Use the checked-in Gradle Wrapper for every build. When upgrading Gradle, regenerate all Wrapper files, add the official binary-distribution SHA-256 value to `gradle-wrapper.properties`, and verify the Wrapper JAR against Gradle's published checksum.

## Privacy

Do not commit real names, personal emails, unapproved usernames, local filesystem paths, server addresses, coordinates, tokens, cookies, webhooks, crash reports, logs, configuration files, or generated runtime data. Use `Cinq`, verified project-owned identifiers, and sanitized minimal log excerpts.

## Code review rules

- Reject any change that expands behavior outside local particle admission without explicit owner approval and a documented design review.
- Reject networking, telemetry, gameplay automation, server-rule evasion, hidden behavior, or weakened audit checks.
- Require tests for configuration, admission-budget, and mixin-related behavior changes.
- Treat personal information or credentials anywhere in source, documentation, artifacts, or history as a release blocker.
