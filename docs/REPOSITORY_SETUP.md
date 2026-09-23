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

1. Update `mod_version` in `gradle.properties`; the target metadata under `src/<target>/resources/` uses the expanded version placeholder. Make this change through a GitHub pull request.
2. Update `CHANGELOG.md` in the same pull request and wait for hosted verification.
3. Merge the release pull request into `main` after the required GitHub Actions checks pass.
4. Create and push an annotated tag matching the project version using GitHub's release/tag interface. For a target-specific patch, use the target suffix documented below.
5. The release workflow rebuilds and audits the exact target set for the tag, verifies packaged metadata and checksums, then stages a draft GitHub Release with individual binary/source JARs. Review and publish that draft manually only after the release gates pass. Use only the matching verified primary JAR for later Modrinth or CurseForge submission.

For the Minecraft 1.21.1 patch release, keep `mod_version` at `1.1.1` and create the annotated target tag `v1.1.1-mc1.21.1`. The dedicated `release-1.21.1.yml` workflow publishes that target, and its hosted promotion mode attaches the verified 1.21.1 binary and sources JARs to the existing `v1.1.1` release alongside the other target artifacts. The historical `v1.1.1` tag and source remain unchanged.

## v1.3.0 release target plan

The planned stable v1.3.0 release contains exactly three targets: Minecraft 26.3 Fabric, 1.21.11 Fabric, and 1.21.11 NeoForge. NeoForge 26.3 remains a beta preview and is deferred until its loader reaches stable. Quilt 26.3 is also deferred; existing 1.21.1 and 26.2 artifacts remain historical downloads outside the v1.3.0 matrix.

The branch retains implementation, isolated build, and hosted startup-smoke coverage for NeoForge 26.3, but that beta preview is not a v1.3.0 release target. Its current build baseline is `26.3.0.7-beta`; the manual client check used `26.3.0.8-beta`. The unified release workflow packages the three stable targets and stages a draft only when an authorized version tag is pushed.

## Fabric 26.3 target-specific release

Fabric 26.3 was released under internal version `1.2.5`, when the default was `mc_target=26.2`, with annotated target tag `v1.2.5-mc26.3`. During v1.3.0 development, `mc_target=26.3` is the default. The current exact release is published at [`v1.2.5-mc26.3`](https://github.com/imCinq/fps-tune/releases/tag/v1.2.5-mc26.3); it remains separate from the five-artifact root `v1.2.5` release. This describes the historical v1.2.5 release only; stable v1.3.0 adds NeoForge 1.21.11 and retains Fabric 26.3. NeoForge 26.3 is deferred until its loader reaches stable.

`.github/workflows/release-26.3.yml` uses the protected `release` environment, checks tag provenance and version consistency, builds and audits only Fabric 26.3, verifies packaged metadata, and stages a **draft**, non-latest GitHub Release containing its binary, sources, and SHA-256 checksums. The full-release workflow excludes these tags. The draft is a staging step; publish the exact assets only after the required remote client validation and maintainer review.

The release workflow does **not** itself enforce graphical testing. The separate `.github/workflows/client-26.3.yml` workflow runs the real-client smoke matrix with Mod Menu present and absent. Follow [TESTING.md](TESTING.md) for the complete validation sequence, and [DISTRIBUTION.md](DISTRIBUTION.md) for the exact assets and reuse those validated bytes for later distribution.

## Repository safeguards

- Keep `main` protected against force pushes and deletion.
- Require the `Build and test` status check before merging once branch protection is configured.
- Keep Actions permissions read-only by default; release publishing requests write access only inside the protected `release` environment.
- Keep Actions pinned to full commit SHAs and review Dependabot changes individually.
- Protect `v*` release tags from updates and deletion, and keep release workflows verifying annotated tags, verified target commits, and ancestry from `main`.
- Treat the repository, issues, Actions logs, releases, and artifacts as public. Before each release, review them for private information, correct licensing, server-rule language, and matching distribution artifacts.

See [AGENTS.md](../AGENTS.md), [MAINTENANCE.md](MAINTENANCE.md), and [DISTRIBUTION.md](DISTRIBUTION.md) for the detailed project contract and release rules.
