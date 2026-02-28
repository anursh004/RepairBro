# 🔧 RepairBro Platform

India's standardized hardware repair & franchise platform — built with a microservices architecture for scale.

## Architecture

```
Client Apps → Kong API Gateway → Microservices → Kafka Event Bus → PostgreSQL (per-service)
```

### Phase 1 Services (MVP)

| Service | Port | Database | Description |
|---------|------|----------|-------------|
| `auth-service` | 8081 | `auth_db` | JWT authentication, RBAC, user management |
| `repair-core` | 8082 | `repair_db` | Ticket lifecycle, diagnostics, timeline |
| `fix-bill` | 8083 | `billing_db` | Invoicing, GST, payments |

### Infrastructure

| Component | Port | Purpose |
|-----------|------|---------|
| Kong Gateway | 8000 | API routing, rate limiting, CORS |
| PostgreSQL | 5432 | Per-service databases |
| Kafka | 9094 | Event bus (domain events) |
| Kafka UI | 8090 | Kafka management dashboard |
| Redis | 6379 | Caching, session store |

## Quick Start

### Prerequisites

- **Java 21** (Eclipse Temurin recommended)
- **Docker & Docker Compose**
- **Gradle** (wrapper included)

### 1. Start Infrastructure

```bash
docker-compose up -d
```

### 2. Build All Services

```bash
./gradlew build
```

### 3. Run Services

```bash
# Terminal 1 — Auth Service
./gradlew :auth-service:bootRun

# Terminal 2 — Repair Core
./gradlew :repair-core:bootRun

# Terminal 3 — Fix Bill
./gradlew :fix-bill:bootRun
```

### 4. Access

- **API Gateway**: http://localhost:8000
- **Kafka UI**: http://localhost:8090

## Project Structure

```
RepairBro/
├── repairbro-commons/     # Shared DTOs, events, exceptions
├── auth-service/          # Authentication & user management
├── repair-core/           # Ticket lifecycle service
├── fix-bill/              # Billing & payments service
├── api-gateway/           # Kong declarative config
├── infra/                 # Infrastructure scripts
├── docker-compose.yml     # Local dev infrastructure
├── build.gradle           # Root Gradle build
└── settings.gradle        # Multi-project settings
```

## Tech Stack

- **Java 21** + Spring Boot 3.4
- **PostgreSQL 16** (database-per-service)
- **Apache Kafka** (event-driven communication)
- **Redis 7** (caching)
- **Kong** (API Gateway)
- **Flyway** (database migrations)
- **Docker** + Docker Compose

## API Endpoints

All APIs are accessible through the Kong Gateway at `http://localhost:8000`.

### Auth (`/api/v1/auth`)
- `POST /api/v1/auth/register` — Register user
- `POST /api/v1/auth/login` — Login (returns JWT)
- `POST /api/v1/auth/refresh` — Refresh token

### Tickets (`/api/v1/tickets`)
- `POST /api/v1/tickets` — Create repair ticket
- `GET /api/v1/tickets/{id}` — Get ticket details
- `POST /api/v1/tickets/{id}/status` — Update status
- `POST /api/v1/tickets/{id}/diagnose` — Add diagnosis step

### Invoices (`/api/v1/invoices`)
- `POST /api/v1/invoices` — Create invoice
- `GET /api/v1/invoices/{id}` — Get invoice
- `POST /api/v1/invoices/{id}/payments` — Record payment