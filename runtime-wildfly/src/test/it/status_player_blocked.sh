#!/usr/bin/env bash
set -euo pipefail

BASE_URL="${BASE_URL:-http://localhost:28080/statusapi/api/status/player}"

echo "== IT: status_player_blocked"
echo "URL: $BASE_URL"

resp="$(curl -sS -i \
  -H "Content-Type: application/xml" \
  -H "X-Api-Version: 1" \
  -H "X-User: demo" \
  -H "X-Correlation-Id: corr-123" \
  --data '<statusRequest><playerId>9123</playerId><reason>demo</reason></statusRequest>' \
  "$BASE_URL" || true)"

assert_contains () {
  local needle="$1"
  if ! printf "%s" "$resp" | grep -q "$needle"; then
    echo "FAIL: expected to find: $needle"
    echo "---- response dump ----"
    printf "%s\n" "$resp"
    echo "-----------------------"
    exit 1
  fi
}

assert_contains "HTTP/1.1 200"
assert_contains "<status>BLOCKED</status>"
assert_contains "<correlationId>corr-123</correlationId>"

echo "PASS"
