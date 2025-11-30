#!/bin/bash

set -e

echo "🚀 Setting up Spring Boot jOOQ API..."

# Colors for output
GREEN='\033[0;32m'
YELLOW='\033[1;33m'
NC='\033[0m' # No Color

echo -e "${YELLOW}Step 1: Starting PostgreSQL...${NC}"
docker-compose up -d postgres

echo -e "${YELLOW}Step 2: Waiting for PostgreSQL to be ready...${NC}"
sleep 5

# Wait for PostgreSQL to be healthy
for i in {1..30}; do
    if docker-compose exec -T postgres pg_isready -U apiuser -d apidb > /dev/null 2>&1; then
        echo -e "${GREEN}✓ PostgreSQL is ready${NC}"
        break
    fi
    echo "Waiting for PostgreSQL... ($i/30)"
    sleep 2
done

echo -e "${YELLOW}Step 3: Generating jOOQ classes from database schema...${NC}"
export JAVA_HOME=$(/usr/libexec/java_home -v 21)
mvn jooq-codegen:generate

if [ $? -eq 0 ]; then
    echo -e "${GREEN}✓ jOOQ classes generated successfully${NC}"
else
    echo "❌ Failed to generate jOOQ classes"
    exit 1
fi

echo -e "${YELLOW}Step 4: Building the application...${NC}"
mvn package -DskipTests

if [ $? -eq 0 ]; then
    echo -e "${GREEN}✓ Application built successfully${NC}"
else
    echo "❌ Failed to build application"
    exit 1
fi

echo -e "${YELLOW}Step 5: Starting the application...${NC}"
docker-compose up -d app

echo ""
echo -e "${GREEN}========================================${NC}"
echo -e "${GREEN}✓ Setup complete!${NC}"
echo -e "${GREEN}========================================${NC}"
echo ""
echo "API is starting at: http://localhost:8080"
echo "PostgreSQL is running at: localhost:5432"
echo ""
echo "Check logs with: docker-compose logs -f app"
echo "Stop services with: docker-compose down"
echo ""
echo "Test the API with:"
echo "  curl http://localhost:8080/api/test/public"
echo ""
