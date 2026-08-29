# GitHub repository workflow

The canonical private repository is `https://github.com/imCinq/fps-tune`.

## First checkout

Clone the repository over HTTPS:

```sh
git clone https://github.com/imCinq/fps-tune.git
cd fps-tune
```

Use `Cinq` as the public author name and GitHub's no-reply identity for commit metadata. Never add a personal contact email, local machine path, token, or runtime configuration to the repository.

## Normal update flow

1. Create a focused branch from `main`:

   ```sh
   git switch main
   git pull --ff-only
   git switch -c fix/short-description
   ```

2. Make the smallest change that solves the issue. Keep FPS Tune client-only, disabled by default, and limited to optional local rendering workloads or render scheduling.
3. Add or update deterministic tests for the behavior.
4. Run the full local verification checklist for both supported targets:

   ```sh
   for target in 1.21.11 26.2; do
     ./gradlew clean build --no-daemon -Pmc_target="$target"
     ./scripts/audit-client-only.sh "$target"
   done
   ./scripts/audit-repository.sh
   ```

5. For mixin changes, inspect the target Minecraft bytecode and perform a graphical `runClient` smoke test.
6. Update `CHANGELOG.md` for user-visible changes, then commit and push the branch:

   ```sh
   git push -u origin fix/short-description
   ```

7. Open a pull request into `main`. Review the diff, confirm the CI workflow is green, and merge only after the change preserves the trust boundary.

Dependabot pull requests are review-only until compatibility, tests, bytecode targets, and release notes have been checked.

## Release flow

1. Update `mod_version` in `gradle.properties`; the target metadata files in `src/1.21.11/resources/` and `src/26.2/resources/` use the expanded version placeholder.
2. Update `CHANGELOG.md` and run the complete verification checklist.
3. Merge the release pull request into `main`.
4. Create and push an annotated tag matching the project version:

   ```sh
   git tag -a vX.Y.Z -m "FPS Tune X.Y.Z"
   git push origin vX.Y.Z
   ```

5. The release workflow rebuilds both target profiles, reruns the audits, checks that the tag matches the project version, and creates a GitHub Release containing one binary and one sources JAR per Minecraft target. Use only the matching verified JAR for any later manual Modrinth or CurseForge submission.

## Repository safeguards

- Keep `main` protected against force pushes and deletion.
- Require the `Build and test` status check before merging once branch protection is configured.
- Keep Actions permissions read-only by default; the release workflow requests write access only for tagged releases.
- Keep Actions pinned to full commit SHAs and review Dependabot changes individually.
- Keep the repository private until a separate public-release review confirms privacy, licensing, server-rule, and distribution requirements.

See [AGENTS.md](AGENTS.md), [docs/MAINTENANCE.md](docs/MAINTENANCE.md), and [docs/DISTRIBUTION.md](docs/DISTRIBUTION.md) for the detailed project contract and release rules.
