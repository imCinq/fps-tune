#!/usr/bin/env python3
"""Inspect decompressed release contents without printing sensitive matches."""
import hashlib, re, sys, zipfile
from pathlib import Path
private_token_digest = "e7462a4f5295b5001cdb93eb3d6c65775910324ce38faacdf9e19403f4a3ca43"
patterns = {
    "private path": rb"/Users/[^/\s]+|/home/[^/\s]+|[A-Za-z]:[\\/]Users[\\/]",
    "credential": rb"-----BEGIN (?:[A-Z ]+ )?PRIVATE KEY-----|gh[pousr]_[A-Za-z0-9_]{20,}|github_pat_[A-Za-z0-9_]{20,}|AKIA[0-9A-Z]{16}|AIza[0-9A-Za-z_-]{35}",
    "email": rb"[A-Za-z0-9._%+-]+@[A-Za-z0-9.-]+\.[A-Za-z]{2,}",
}
files = [Path(p) for p in sys.argv[1:]]
if not files: raise SystemExit("No release JARs supplied")
for path in files:
    with zipfile.ZipFile(path) as jar:
        assert jar.testzip() is None, "Corrupt archive"
        for item in jar.infolist():
            assert not item.filename.startswith(("/", "\\")) and ".." not in Path(item.filename).parts, "Unsafe archive entry"
            data = item.filename.encode() + b"\n" + jar.read(item)
            for label, pattern in patterns.items():
                if re.search(pattern, data): raise SystemExit(f"Release audit failed: {label} in {path.name}:{item.filename}")
            for token in re.findall(rb"[A-Za-z][A-Za-z0-9_.-]*", data):
                if hashlib.sha256(token.lower()).hexdigest() == private_token_digest:
                    raise SystemExit(f"Release audit failed: private identifier in {path.name}:{item.filename}")
            if item.filename.endswith('.class') and re.search(rb'java/net/|java/net/http/|java/lang/ProcessBuilder|sun/misc/Unsafe', data):
                raise SystemExit(f"Release audit failed: prohibited runtime API in {path.name}:{item.filename}")
    print(f"PASS: {path.name}: decompressed filenames/content, private paths/identifier, emails, credentials and runtime API checks")
print("Java SourceFile attributes contain ordinary project source filenames; these are expected debug metadata.")
