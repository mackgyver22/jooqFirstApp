#!/bin/bash

# Test script for enhanced user profile endpoint with relationships

echo "🔐 Logging in..."
LOGIN_RESPONSE=$(curl -s -X POST http://localhost:8080/api/auth/login \
  -H "Content-Type: application/json" \
  -d '{"username":"testuser4","password":"password123"}')

TOKEN=$(echo $LOGIN_RESPONSE | grep -o '"token":"[^"]*"' | cut -d'"' -f4)

if [ -z "$TOKEN" ]; then
    echo "❌ Login failed!"
    echo "$LOGIN_RESPONSE"
    exit 1
fi

echo "✅ Login successful!"
echo ""

echo "📝 Creating/Updating user profile..."
curl -s -X POST http://localhost:8080/api/profile \
  -H "Authorization: Bearer $TOKEN" \
  -H "Content-Type: application/json" \
  -d '{
    "bio": "Full-stack developer with expertise in Java and Spring Boot",
    "avatarUrl": "https://example.com/avatars/testuser4.jpg",
    "phone": "+1-555-0123",
    "dateOfBirth": "1995-06-15",
    "country": "United States",
    "city": "San Francisco"
  }' | python3 -m json.tool

echo ""
echo ""

echo "👤 Fetching user profile with relationships (user + items)..."
echo "This demonstrates:"
echo "  - user_profile (main table)"
echo "  - → users (many-to-one relationship)"
echo "  - → items (one-to-many relationship from users)"
echo ""

curl -s -X GET http://localhost:8080/api/profile \
  -H "Authorization: Bearer $TOKEN" | python3 -m json.tool

echo ""
echo "✅ Done! Notice how the response includes:"
echo "   1. Profile data (bio, phone, etc.)"
echo "   2. Nested 'user' object with user details"
echo "   3. Array of 'items' belonging to that user"
