#!/bin/sh
echo "Starting BenMAP UI App..."

/docker-entrypoint.sh

exec "$@"