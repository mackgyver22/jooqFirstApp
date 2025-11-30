#!/bin/bash

set -e

echo "🔄 Regenerating database schema and jOOQ classes..."

# Stop the app (keep database running)
docker-compose stop app

# Recreate the database
docker-compose down -v
docker-compose up -d postgres

echo "⏳ Waiting for PostgreSQL to be ready..."
sleep 8

# Regenerate jOOQ classes
echo "🔧 Generating jOOQ classes..."
export JAVA_HOME=$(/usr/libexec/java_home -v 21)
mvn jooq-codegen:generate

echo "✅ jOOQ classes regenerated!"
echo ""
echo "Next steps:"
echo "1. Create DTOs in src/main/java/com/example/springjooqapi/dto/"
echo "2. Create Service in src/main/java/com/example/springjooqapi/service/"
echo "3. Create Controller in src/main/java/com/example/springjooqapi/controller/"
echo "4. Build and restart: mvn package -DskipTests && docker-compose up -d app"
