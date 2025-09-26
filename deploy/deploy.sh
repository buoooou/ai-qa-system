#!/bin/bash
set -euo pipefail

REPO_DIR=${REPO_DIR:-~/ai-qa-system}
COMPOSE_FILE=${COMPOSE_FILE:-docker-compose.prod.yml}

cd "$REPO_DIR"

git pull origin main

docker compose -f "$COMPOSE_FILE" pull

docker compose -f "$COMPOSE_FILE" up -d --remove-orphans

docker image prune -f
