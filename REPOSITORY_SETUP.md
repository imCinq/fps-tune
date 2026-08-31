# GitHub repository workflow

The canonical public repository is `https://github.com/imCinq/fps-tune`.

## Remote-only workflow

All future work must be performed through GitHub. Use GitHub branches, pull requests, GitHub Actions, hosted artifacts, and releases. Do not clone or edit a local checkout on the owner's device, and do not install, download, or run Java, any JDK, Gradle, the Gradle Wrapper distribution, or project dependencies there. If a required task cannot be done with GitHub-hosted tooling, stop and ask the owner before proceeding locally.

## First checkout

Clone the repository over HTTPS:

```sh
git clone https://github.com/imCinq/fps-tune.git
cd fps-tune
```

Use `Cinq` as the public author name and GitHub's no-reply identity for commit metadata. Never add a personal contact email, local machine path, token, or runtime configuration to the repository.

## Normal update flow

1. Create a focused branch from `main` using GitHub's branch or pull-request interface; do not check out the repository locally.
2. Make the smallest change that solves the issue. Keep FPS Tune client-only, disabled by default, and limited to optional local rendering workloads or render scheduling.
3. Add or update deterministic tests for the behavior in the same pull request.
4. Open or update the pull request and let `.github/workflows/ci.yml` run the hosted matrix on GitHub.
5. For mixin changes, use the required bytecode inspection and graphical smoke test in a GitHub-hosted or other owner-approved remote environment.
6. Update `CHANGELOG.md` for user-visible changes.
7. Review the remote diff and confirm the hosted CI workflow is green before merging.

Dependabot pull requests are review-only until compatibility, tests, bytecode targets, and release notes have been checked.

## Release flow

1. Update `mod_version` in `gradle.properties`; the target metadata files in `src/1.21.1/resources/`, `src/1.21.11/resources/`, and `src/26.2/resources/` use the expanded version placeholder. Make this change through a GitHub pull request.
2. Update `CHANGELOG.md` in the same pull request and wait for hosted verification.
3. Merge the release pull request into `main` after the required GitHub Actions checks pass.
4. Create and push an annotated tag matching the project version using GitHub's release/tag interface. For a target-specific patch, use the target suffix documented below.
5. The release workflows rebuild the supported target profiles, rerun the audits, check that the tag matches the project version, and create GitHub Releases containing one binary and one sources JAR per Minecraft target. Use only the matching verified JAR for any later manual Modrinth or CurseForge submission.

For the Minecraft 1.21.1 patch release, keep `mod_version` at `1.1.1` and create the annotated target tag `v1.1.1-mc1.21.1`. The dedicated `release-1.21.1.yml` workflow publishes only that target and leaves the historical `v1.1.1` release untouched.

## Repository safeguards

- Keep `main` protected against force pushes and deletion.
- Require the `Build and test` status check before merging once branch protection is configured.
- Keep Actions permissions read-only by default; the release workflow requests write access only for tagged releases.
- Keep Actions pinned to full commit SHAs and review Dependabot changes individually.
- Treat the repository, issues, Actions logs, releases, and artifacts as public. Before each release, review them for private information, correct licensing, server-rule language, and matching distribution artifacts.

See [AGENTS.md](AGENTS.md), [docs/MAINTENANCE.md](docs/MAINTENANCE.md), and [docs/DISTRIBUTION.md](docs/DISTRIBUTION.md) for the detailed project contract and release rules.
