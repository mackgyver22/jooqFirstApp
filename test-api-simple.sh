#!/bin/bash

API_URL=http://localhost:8080

echo "=== Testing Spring Boot API ==="
echo ""

echo "1. Testing public endpoint..."
curl -s ${API_URL}/api/test/public
echo -e "\n"

echo "2. Registering a new user..."
curl -s -X POST ${API_URL}/api/auth/register \
  -H "Content-Type: application/json" \
  -d '{
    "username": "testuser",
    "email": "test@example.com",
    "password": "password123",
    "firstName": "Test",
    "lastName": "User"
  }'
echo -e "\n"

echo "3. Logging in..."
RESPONSE=$(curl -s -X POST ${API_URL}/api/auth/login \
  -H "Content-Type: application/json" \
  -d '{
    "username": "testuser",
    "password": "password123"
  }')

# Extract token using grep and sed (no jq needed)
TOKEN=$(echo $RESPONSE | grep -o '"token":"[^"]*' | sed 's/"token":"//')

echo "Response: $RESPONSE"
echo "Token: ${TOKEN:0:50}..."
echo ""

echo "4. Testing protected endpoint..."
curl -s ${API_URL}/api/test/protected \
  -H "Authorization: Bearer ${TOKEN}"
echo -e "\n"

echo "5. Creating an item..."
curl -s -X POST ${API_URL}/api/items \
  -H "Authorization: Bearer ${TOKEN}" \
  -H "Content-Type: application/json" \
  -d '{
    "name": "Test Item",
    "description": "This is a test item"
  }'
echo -e "\n"

echo "6. Getting all items..."
curl -s ${API_URL}/api/items \
  -H "Authorization: Bearer ${TOKEN}"
echo -e "\n"

echo "=== Test Complete ==="
