#!/usr/bin/env bash

set -euo pipefail

target="${1:-${MC_TARGET:-26.2}}"
case "$target" in
	26.2-neoforge)
		resource_directory="src/26.2-neoforge/resources"
		;;
	26.2)
		resource_directory="src/26.2/resources"
		;;
	1.21.1)
		resource_directory="src/1.21.1/resources"
		;;
	1.21.11)
		resource_directory="src/1.21.11/resources"
		;;
	*)
		echo "Unsupported target '$target'." >&2
		exit 2
		;;
esac

if [[ "$target" == "26.2-neoforge" ]]; then
	metadata_file="$resource_directory/META-INF/neoforge.mods.toml"
else
	metadata_file="$resource_directory/fabric.mod.json"
fi
mixin_file="$resource_directory/fpstune.mixins.json"
prohibited_pattern='ClientPlayNetworking|sendPacket|clickSlot|Serverbound|C2S|attack\(|swing\(|freecam|auto.?click|inventory.?automat|movement.?automat|rotation.?automat|java\.net\.|HttpClient|WebSocket|ProcessBuilder|Runtime\.getRuntime|System\.getenv|System\.load(Library)?|sun\.misc\.Unsafe'

if grep -RInE --include='*.java' "$prohibited_pattern" src; then
	echo "Client-only audit failed: review the prohibited API reference(s) above." >&2
	exit 1
fi

if [[ "$target" == "26.2-neoforge" ]]; then
	grep -Fq 'modId="fpstune"' "$metadata_file"
	grep -Fq 'displayURL="https://github.com/imCinq/fps-tune"' "$metadata_file"
	grep -Fq 'issueTrackerURL="https://github.com/imCinq/fps-tune/issues"' "$metadata_file"
	grep -Fq 'versionRange="[26.2]"' "$metadata_file"
	grep -Fq 'logoFile="assets/fpstune/icon.png"' "$metadata_file"
	grep -Fq 'config="fpstune.mixins.json"' "$metadata_file"
else
	jq -e --arg target "$target" '.environment == "client" and .depends.minecraft == $target' "$metadata_file" >/dev/null
	jq -e '(.contact.homepage | type) == "string" and (.contact.homepage | startswith("https://")) and (.contact.issues | type) == "string" and (.contact.issues | startswith("https://")) and (.contact.sources | type) == "string" and (.contact.sources | startswith("https://"))' "$metadata_file" >/dev/null
fi
jq -e '.required == true and (.client | type == "array") and (.client | length >= 1) and (.client | all(.[]; endswith("Mixin")))' "$mixin_file" >/dev/null

echo "Client-only source audit passed for Minecraft $target."