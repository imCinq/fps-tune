#!/usr/bin/env bash

set -euo pipefail

prohibited_pattern='ClientPlayNetworking|sendPacket|clickSlot|Serverbound|C2S|attack\(|swing\(|freecam|auto.?click|inventory.?automat|movement.?automat|rotation.?automat|java\.net\.|HttpClient|WebSocket|ProcessBuilder|Runtime\.getRuntime|System\.getenv|System\.load(Library)?|sun\.misc\.Unsafe'

if grep -RInE "$prohibited_pattern" src/main; then
	echo "Client-only audit failed: review the prohibited API reference(s) above." >&2
	exit 1
fi

jq -e '.environment == "client"' src/main/resources/fabric.mod.json >/dev/null
jq -e '.required == true and .client == ["ParticleEngineMixin"]' src/main/resources/coretune.mixins.json >/dev/null

echo "Client-only source audit passed."
