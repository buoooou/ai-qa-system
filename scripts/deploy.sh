#!/usr/bin/env bash
set -e
NAMESPACE=$1
TAG=${2:-latest}
export TAG NAMESPACE

# 拉取最新镜像
docker-compose -f /home/ubuntu/docker-compose.yml pull
# 滚动重启
docker-compose -f /home/ubuntu/docker-compose.yml up -d --remove-orphans
# 清理旧镜像
docker image prune -f