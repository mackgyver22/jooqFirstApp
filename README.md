# Spring Boot API Template with jOOQ, PostgreSQL & JWT

A production-ready backend API template using Spring Boot 3.x, jOOQ for type-safe SQL queries, PostgreSQL database, and JWT authentication.

## 🚀 Features

- **Spring Boot 3.2** - Latest Spring Boot framework
- **jOOQ** - Type-safe SQL query builder
- **PostgreSQL** - Robust relational database
- **JWT Authentication** - Secure token-based authentication
- **Docker & Docker Compose** - Containerized deployment
- **Role-Based Access** - Infrastructure for role-based authorization (ready to extend)
- **RESTful API** - Clean REST endpoints with CRUD operations
- **Validation** - Request validation using Bean Validation
- **Lombok** - Reduced boilerplate code

## 📋 Prerequisites

- Java 17 or higher (Java 21 recommended)
- Maven 3.6+
- Docker & Docker Compose
- PostgreSQL (for local development without Docker)

## 🛠️ Project Structure

```
src/
├── main/
│   ├── java/com/example/springjooqapi/
│   │   ├── controller/          # REST Controllers
│   │   ├── dto/                 # Data Transfer Objects
│   │   ├── model/               # Domain models
│   │   ├── security/            # JWT & Security config
│   │   ├── service/             # Business logic
│   │   └── Application.java     # Main application
│   └── resources/
│       ├── db/
│       │   └── init.sql         # Database schema
│       └── application.properties
```

## 🚀 Quick Start with Docker

### Option 1: Automated Setup (Recommended)

Run the setup script which will:
1. Start PostgreSQL
2. Generate jOOQ classes from the database schema
3. Build the application
4. Start all services

```bash
./setup.sh
```

The API will be available at `http://localhost:8080`

### Option 2: Manual Setup

```bash
# 1. Start PostgreSQL
docker-compose up -d postgres

# 2. Wait for PostgreSQL to be ready (about 5-10 seconds)
docker-compose logs -f postgres
# Press Ctrl+C when you see "database system is ready to accept connections"

# 3. Generate jOOQ classes
mvn jooq-codegen:generate

# 4. Build the application
mvn clean package -DskipTests

# 5. Start the application
docker-compose up -d app

# 6. Check logs
docker-compose logs -f app
```

### Stop the application

```bash
# Stop all services
./stop.sh

# Stop and remove all data (including database)
./stop.sh -v
```

## 💻 Local Development (without Docker)

### 1. Start PostgreSQL

```bash
# Using Docker for PostgreSQL only
docker run --name postgres-api \
  -e POSTGRES_DB=apidb \
  -e POSTGRES_USER=apiuser \
  -e POSTGRES_PASSWORD=apipassword \
  -p 5432:5432 \
  -d postgres:16-alpine
```

### 2. Initialize the database

```bash
# Connect to PostgreSQL and run the init script
docker exec -i postgres-api psql -U apiuser -d apidb < src/main/resources/db/init.sql
```

### 3. Generate jOOQ classes

```bash
# This generates Java classes from your database schema
mvn clean generate-sources
```

### 4. Run the application

```bash
mvn spring-boot:run
```

## 🔑 API Endpoints

### Authentication Endpoints (Public)

#### Register a new user
```bash
POST /api/auth/register
Content-Type: application/json

{
  "username": "johndoe",
  "email": "john@example.com",
  "password": "password123",
  "firstName": "John",
  "lastName": "Doe"
}
```

#### Login
```bash
POST /api/auth/login
Content-Type: application/json

{
  "username": "johndoe",
  "password": "password123"
}

Response:
{
  "token": "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9...",
  "type": "Bearer",
  "username": "johndoe",
  "email": "john@example.com"
}
```

#### Validate Token
```bash
GET /api/auth/validate
Authorization: Bearer <your-jwt-token>
```

### Test Endpoints

#### Public endpoint (no auth required)
```bash
GET /api/test/public
```

#### Protected endpoint (auth required)
```bash
GET /api/test/protected
Authorization: Bearer <your-jwt-token>
```

### Item Endpoints (Protected - Requires Authentication)

#### Create an item
```bash
POST /api/items
Authorization: Bearer <your-jwt-token>
Content-Type: application/json

{
  "name": "My Item",
  "description": "Item description"
}
```

#### Get all items (for authenticated user)
```bash
GET /api/items
Authorization: Bearer <your-jwt-token>
```

#### Get item by ID
```bash
GET /api/items/{id}
Authorization: Bearer <your-jwt-token>
```

#### Update an item
```bash
PUT /api/items/{id}
Authorization: Bearer <your-jwt-token>
Content-Type: application/json

{
  "name": "Updated Item",
  "description": "Updated description"
}
```

#### Delete an item
```bash
DELETE /api/items/{id}
Authorization: Bearer <your-jwt-token>
```

## 🧪 Testing the API

### Using curl

```bash
# 1. Register a user
curl -X POST http://localhost:8080/api/auth/register \
  -H "Content-Type: application/json" \
  -d '{
    "username": "testuser",
    "email": "test@example.com",
    "password": "password123",
    "firstName": "Test",
    "lastName": "User"
  }'

# 2. Login and get token
TOKEN=$(curl -X POST http://localhost:8080/api/auth/login \
  -H "Content-Type: application/json" \
  -d '{
    "username": "testuser",
    "password": "password123"
  }' | jq -r '.token')

# 3. Use the token to access protected endpoint
curl -X GET http://localhost:8080/api/test/protected \
  -H "Authorization: Bearer $TOKEN"

# 4. Create an item
curl -X POST http://localhost:8080/api/items \
  -H "Authorization: Bearer $TOKEN" \
  -H "Content-Type: application/json" \
  -d '{
    "name": "Test Item",
    "description": "This is a test item"
  }'

# 5. Get all items
curl -X GET http://localhost:8080/api/items \
  -H "Authorization: Bearer $TOKEN"
```

## 🔧 Configuration

### Environment Variables

You can override the default configuration using environment variables:

- `SPRING_DATASOURCE_URL` - Database URL
- `SPRING_DATASOURCE_USERNAME` - Database username
- `SPRING_DATASOURCE_PASSWORD` - Database password
- `JWT_SECRET` - JWT signing secret (base64 encoded, at least 256 bits)
- `JWT_EXPIRATION` - Token expiration time in milliseconds (default: 24 hours)

### Generating a secure JWT secret

```bash
# Generate a secure random key (macOS/Linux)
openssl rand -base64 64

# Use this value for JWT_SECRET environment variable
```

## 📦 Building for Production

### Build JAR file

```bash
mvn clean package -DskipTests
```

### Build Docker image

```bash
docker build -t spring-jooq-api .
```

### Run with custom environment

```bash
docker run -p 8080:8080 \
  -e SPRING_DATASOURCE_URL=jdbc:postgresql://your-db-host:5432/apidb \
  -e JWT_SECRET=your-secure-secret-key \
  spring-jooq-api
```

## 🔐 Security Notes

1. **Change the JWT secret** in production! Use a strong, randomly generated key.
2. **Use HTTPS** in production to encrypt data in transit.
3. **Update database credentials** - Don't use default credentials in production.
4. **Enable CORS properly** - Update CORS configuration for your frontend domain.
5. **Add rate limiting** - Consider adding rate limiting for authentication endpoints.

## 🚧 Extending with Role-Based Authentication

The database schema already includes `roles` and `user_roles` tables. To implement role-based access:

1. Add role checks in SecurityConfig:
```java
.requestMatchers("/api/admin/**").hasRole("ADMIN")
```

2. Use method-level security:
```java
@PreAuthorize("hasRole('ADMIN')")
public ResponseEntity<?> adminOnlyEndpoint() { }
```

## 🛠️ Regenerating jOOQ Classes

Whenever you modify the database schema:

```bash
# 1. Update init.sql with your schema changes

# 2. Recreate the database
docker-compose down -v
docker-compose up -d postgres

# 3. Wait for PostgreSQL to be ready (5-10 seconds)

# 4. Regenerate jOOQ classes
mvn jooq-codegen:generate

# 5. Rebuild and restart the app
mvn clean package -DskipTests
docker-compose up -d app
```

Or simply run the setup script again:
```bash
./stop.sh -v  # Stop and remove all data
./setup.sh    # Complete fresh setup
```

## 📝 Development Tips

1. **IDE Setup**: Import as Maven project. IntelliJ IDEA and VS Code work great.
2. **Database Tools**: Use DBeaver, pgAdmin, or IntelliJ's database tools to inspect the database.
3. **API Testing**: Use Postman, Insomnia, or the REST Client VS Code extension.
4. **Logging**: Check application logs for debugging. Set `logging.level` in application.properties.

## 🐛 Troubleshooting

### jOOQ classes not found
```bash
# Generate jOOQ classes
mvn generate-sources
```

### Database connection issues
```bash
# Check if PostgreSQL is running
docker ps

# Check database logs
docker logs postgres-api
```

### Port already in use
```bash
# Change port in application.properties
server.port=8081
```

## 📄 License

This is a template project - use it however you like!

## 🤝 Contributing

Feel free to customize this template for your needs!
