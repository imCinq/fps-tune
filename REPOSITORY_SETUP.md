# Private GitHub repository setup

1. Create a new GitHub repository named `coretune` and set visibility to **Private**.
2. Do not initialize it with another README, license, or `.gitignore`.
3. Enable GitHub's **Keep my email addresses private** setting before the first commit, and configure Git to use the GitHub-provided no-reply identity rather than a personal email.
4. From this directory, run:

```sh
git init -b main
git add .
git commit -m "Initial CoreTune release"
git remote add origin https://github.com/YOUR_USERNAME/coretune.git
git push -u origin main
```

Recommended settings:

- Block force pushes and deletion of `main`.
- Require the `Build and test` status check before merging.
- Enable Dependabot alerts and security updates.
- Enable secret scanning and push protection if they are available for the account and private repository.
- Restrict Actions to trusted actions and require full-length commit SHAs where that setting is available.
- Keep Actions permissions read-only by default; the release workflow declares `contents: write` only for tagged releases.

After CI passes, create the first release:

```sh
git tag -a v1.0.0 -m "CoreTune 1.0.0"
git push origin v1.0.0
```

Private vulnerability reporting is intended for public repositories. While this repository is private, security concerns should go directly to the owner. Before making it public, inspect the full history for secrets and personal identity metadata, confirm release checksums, perform a graphical Minecraft test, re-check server rules, and then enable private vulnerability reporting.
