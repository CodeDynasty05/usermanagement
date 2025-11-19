# User Management Service

A backend service to manage users, built with Spring Boot 3, PostgreSQL, and Kafka. The service provides CRUD operations for users and publishes user events to Kafka.

Deployed URL: https://usermanagement-h33u.onrender.com/

## Features

- Create, retrieve, update, delete users
- Kafka event streaming for user events
- Dockerized with PostgreSQL
- Swagger / OpenAPI documentation

## Table of Contents

1. [Setup & Run](#setup--run)
2. [Docker Compose](#docker-compose)
3. [Environment Variables](#environment-variables)
4. [API Endpoints](#api-endpoints)
5. [Example API Calls](#example-api-calls)
6. [Health Check](#health-check)
7. [Swagger UI](#swagger-ui)

---

## Setup & Run

### Prerequisites

- Java 17+
- Docker & Docker Compose
- Maven (optional if building locally)
- Kafka credentials (Redpanda Cloud)

### Local Setup

1. Clone the repo:

```bash
git clone https://github.com/your-username/user-management-service.git
cd user-management-service
```

Create a .env file with environment variables:

```
DB_HOST=localhost
DB_PORT=5432
DB_NAME=userdb
DB_USERNAME=postgres
DB_PASSWORD=postgres

KAFKA_USERNAME=nazim
KAFKA_PASSWORD=vId63yno2dix4gCQ3aHfYSgbTB63DK
KAFKA_CONSUMER_GROUP=user-management-group
SERVER_PORT=8080
SHOW_SQL=false
LOG_LEVEL=INFO
```

Start the service with Docker Compose:

```
docker-compose up --build
```

The app will be accessible at http://localhost:8080.

### Running Without Docker

Ensure PostgreSQL and Kafka are running.

Set environment variables (as above).

Build and run:

```
./mvnw clean install
./mvnw spring-boot:run
```

## Docker Compose

`docker-compose.yml` includes:

- user-management-service (Spring Boot app)
- postgres (PostgreSQL database)

## Environment Variables

| Variable | Description |
|---------|-------------|
| DB_HOST | PostgreSQL hostname |
| DB_PORT | PostgreSQL port |
| DB_NAME | Database name |
| DB_USERNAME | Database username |
| DB_PASSWORD | Database password |
| KAFKA_USERNAME | Kafka username |
| KAFKA_PASSWORD | Kafka password |
| KAFKA_CONSUMER_GROUP | Kafka consumer group ID |
| SERVER_PORT | Application port |
| SHOW_SQL | Show SQL logs (true/false) |
| LOG_LEVEL | Logging level (INFO, DEBUG, etc.) |

## API Endpoints

Base URL: `https://usermanagement-h33u.onrender.com/api/v1/users`

| Method | Endpoint | Description |
|--------|-----------|-------------|
| GET | /health | Service health check |
| POST | / | Create new user |
| GET | /{id} | Get user by ID |
| GET | / | List all users with optional pagination, sorting, filtering |
| PUT | /{id} | Update user |
| DELETE | /{id} | Delete user |

## Example API Calls

### Create User

```
curl -X POST https://usermanagement-h33u.onrender.com/api/v1/users -H "Content-Type: application/json" -d '{
  "name": "John Doe",
  "email": "john@example.com",
  "phone": "+1234567890",
  "role": "USER",
  "active": true
}'
```

### Get User by ID

```
curl https://usermanagement-h33u.onrender.com/api/v1/users/1
```

### List All Users

```
curl https://usermanagement-h33u.onrender.com/api/v1/users?page=0&size=10
```

### Update User

```
curl -X PUT https://usermanagement-h33u.onrender.com/api/v1/users/1 -H "Content-Type: application/json" -d '{
  "name": "Jane Doe",
  "email": "jane@example.com"
}'
```

### Delete User

```
curl -X DELETE https://usermanagement-h33u.onrender.com/api/v1/users/1
```

## Health Check

```
curl https://usermanagement-h33u.onrender.com/api/v1/users/health
```

Response:

```
User Management Service is running with Kafka!
```

## Swagger UI

Accessible at: https://usermanagement-h33u.onrender.com/swagger-ui.html

## Notes

- Kafka Redpanda credentials are required for event streaming.
- HikariCP is used for database connection pooling.
- All configuration is managed via environment variables.
