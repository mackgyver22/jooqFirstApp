#!/bin/bash

set -e

echo "🛑 Stopping all services..."

docker-compose down

if [ "$1" == "-v" ] || [ "$1" == "--volumes" ]; then
    echo "🗑️  Removing volumes (database data will be deleted)..."
    docker-compose down -v
fi

echo "✓ All services stopped"
