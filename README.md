# Monthly Income & Expense Tracker (MIT)

Monorepo:
- `backend/` Spring Boot 3 (Java 21)
- `frontend/` Angular (latest LTS) *(WIP — will start after backend sign-off)*
- `docker-compose.yml` runs PostgreSQL + backend

## Prerequisites
- Docker + Docker Compose

## Run (Backend + DB)
From repo root:

```bash
docker compose up -d --build
```

Backend:
- API base: http://localhost:8080/api
- Swagger UI: http://localhost:8080/swagger-ui/index.html

Stop:
```bash
docker compose down
```

## Run tests (Backend)
```bash
cd backend
mvn test
```

Notes:
- Tests use **H2**.
- Runtime uses **PostgreSQL** in Docker Compose.
