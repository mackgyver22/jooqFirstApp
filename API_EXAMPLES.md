# API Request Examples

This file contains example API requests you can use to test the application.

## Environment Setup

First, set your base URL:

```bash
export API_URL=http://localhost:8080
```

## 1. Register a New User

```bash
curl -X POST ${API_URL}/api/auth/register \
  -H "Content-Type: application/json" \
  -d '{
    "username": "alice",
    "email": "alice@example.com",
    "password": "password123",
    "firstName": "Alice",
    "lastName": "Smith"
  }'
```

## 2. Login

```bash
curl -X POST ${API_URL}/api/auth/login \
  -H "Content-Type: application/json" \
  -d '{
    "username": "alice",
    "password": "password123"
  }'
```

Save the token from the response:

```bash
export TOKEN="<paste-token-here>"
```

Or extract automatically (requires jq):

```bash
export TOKEN=$(curl -s -X POST ${API_URL}/api/auth/login \
  -H "Content-Type: application/json" \
  -d '{
    "username": "alice",
    "password": "password123"
  }' | jq -r '.token')

echo "Token: $TOKEN"
```

## 3. Test Public Endpoint

```bash
curl -X GET ${API_URL}/api/test/public
```

## 4. Test Protected Endpoint

```bash
curl -X GET ${API_URL}/api/test/protected \
  -H "Authorization: Bearer ${TOKEN}"
```

## 5. Validate Token

```bash
curl -X GET ${API_URL}/api/auth/validate \
  -H "Authorization: Bearer ${TOKEN}"
```

## 6. Create an Item

```bash
curl -X POST ${API_URL}/api/items \
  -H "Authorization: Bearer ${TOKEN}" \
  -H "Content-Type: application/json" \
  -d '{
    "name": "Laptop",
    "description": "MacBook Pro 16-inch"
  }'
```

## 7. Create Multiple Items

```bash
curl -X POST ${API_URL}/api/items \
  -H "Authorization: Bearer ${TOKEN}" \
  -H "Content-Type: application/json" \
  -d '{
    "name": "Keyboard",
    "description": "Mechanical keyboard with RGB"
  }'

curl -X POST ${API_URL}/api/items \
  -H "Authorization: Bearer ${TOKEN}" \
  -H "Content-Type: application/json" \
  -d '{
    "name": "Mouse",
    "description": "Wireless gaming mouse"
  }'
```

## 8. Get All Items

```bash
curl -X GET ${API_URL}/api/items \
  -H "Authorization: Bearer ${TOKEN}"
```

## 9. Get Item by ID

```bash
# Replace {id} with actual item ID from previous response
curl -X GET ${API_URL}/api/items/1 \
  -H "Authorization: Bearer ${TOKEN}"
```

## 10. Update an Item

```bash
# Replace {id} with actual item ID
curl -X PUT ${API_URL}/api/items/1 \
  -H "Authorization: Bearer ${TOKEN}" \
  -H "Content-Type: application/json" \
  -d '{
    "name": "Laptop - Updated",
    "description": "MacBook Pro 16-inch M3 Max"
  }'
```

## 11. Delete an Item

```bash
# Replace {id} with actual item ID
curl -X DELETE ${API_URL}/api/items/1 \
  -H "Authorization: Bearer ${TOKEN}"
```

## Testing Error Cases

### Invalid Login

```bash
curl -X POST ${API_URL}/api/auth/login \
  -H "Content-Type: application/json" \
  -d '{
    "username": "alice",
    "password": "wrongpassword"
  }'
```

### Access Protected Endpoint Without Token

```bash
curl -X GET ${API_URL}/api/test/protected
```

### Access Protected Endpoint With Invalid Token

```bash
curl -X GET ${API_URL}/api/test/protected \
  -H "Authorization: Bearer invalid-token-here"
```

### Create Item Without Required Fields

```bash
curl -X POST ${API_URL}/api/items \
  -H "Authorization: Bearer ${TOKEN}" \
  -H "Content-Type: application/json" \
  -d '{
    "description": "Missing name field"
  }'
```

### Register With Duplicate Username

```bash
# Try to register the same username again
curl -X POST ${API_URL}/api/auth/register \
  -H "Content-Type: application/json" \
  -d '{
    "username": "alice",
    "email": "different@example.com",
    "password": "password123"
  }'
```

## Complete Test Flow Script

Here's a complete script to test the entire flow:

```bash
#!/bin/bash

API_URL=http://localhost:8080

echo "1. Testing public endpoint..."
curl -s ${API_URL}/api/test/public | jq

echo -e "\n2. Registering a new user..."
curl -s -X POST ${API_URL}/api/auth/register \
  -H "Content-Type: application/json" \
  -d '{
    "username": "testuser",
    "email": "test@example.com",
    "password": "password123",
    "firstName": "Test",
    "lastName": "User"
  }' | jq

echo -e "\n3. Logging in..."
TOKEN=$(curl -s -X POST ${API_URL}/api/auth/login \
  -H "Content-Type: application/json" \
  -d '{
    "username": "testuser",
    "password": "password123"
  }' | jq -r '.token')

echo "Token received: ${TOKEN:0:20}..."

echo -e "\n4. Testing protected endpoint..."
curl -s ${API_URL}/api/test/protected \
  -H "Authorization: Bearer ${TOKEN}" | jq

echo -e "\n5. Creating an item..."
ITEM_ID=$(curl -s -X POST ${API_URL}/api/items \
  -H "Authorization: Bearer ${TOKEN}" \
  -H "Content-Type: application/json" \
  -d '{
    "name": "Test Item",
    "description": "This is a test item"
  }' | jq -r '.id')

echo "Item created with ID: ${ITEM_ID}"

echo -e "\n6. Getting all items..."
curl -s ${API_URL}/api/items \
  -H "Authorization: Bearer ${TOKEN}" | jq

echo -e "\n7. Getting item by ID..."
curl -s ${API_URL}/api/items/${ITEM_ID} \
  -H "Authorization: Bearer ${TOKEN}" | jq

echo -e "\n8. Updating the item..."
curl -s -X PUT ${API_URL}/api/items/${ITEM_ID} \
  -H "Authorization: Bearer ${TOKEN}" \
  -H "Content-Type: application/json" \
  -d '{
    "name": "Updated Test Item",
    "description": "This item has been updated"
  }' | jq

echo -e "\n9. Deleting the item..."
curl -s -X DELETE ${API_URL}/api/items/${ITEM_ID} \
  -H "Authorization: Bearer ${TOKEN}" | jq

echo -e "\nTest flow completed!"
```

Save this as `test-api.sh`, make it executable (`chmod +x test-api.sh`), and run it (`./test-api.sh`).
