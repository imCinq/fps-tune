#!/usr/bin/env bash

set -euo pipefail

secret_pattern='-----BEGIN ([A-Z ]+ )?PRIVATE KEY-----|gh[pousr]_[A-Za-z0-9_]{20,}|github_pat_[A-Za-z0-9_]{20,}|AKIA[0-9A-Z]{16}|AIza[0-9A-Za-z_-]{35}|https://(discord(app)?\.com/api/webhooks|hooks\.slack\.com/services)/'
private_path_pattern='(/Users/[^/[:space:]]+|/home/[^/[:space:]]+|[A-Za-z]:\\Users\\[^\\[:space:]]+)'
email_pattern='[[:alnum:]._%+-]+@[[:alnum:].-]+\.[[:alpha:]]{2,}'

search_repository() {
	local pattern=$1
	grep -RInEI \
		--exclude-dir=.git \
		--exclude-dir=.gradle \
		--exclude-dir=.gradle-local \
		--exclude-dir=build \
		--exclude=audit-repository.sh \
		-e "$pattern" .
}

if search_repository "$secret_pattern"; then
	echo "Repository audit failed: possible credential or webhook found." >&2
	exit 1
fi

if search_repository "$private_path_pattern"; then
	echo "Repository audit failed: private local path found." >&2
	exit 1
fi

if search_repository "$email_pattern"; then
	echo "Repository audit failed: email address found." >&2
	exit 1
fi

echo "Repository privacy audit passed."
