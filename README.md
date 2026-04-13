# travel-card-api-standalone

A Kotlin/JVM service in Agoda's travel-card / wallet domain — a backend API
that issues and manages travel debit cards for Agoda customers, integrating
with the Wise payments platform.

## Stack
- **Language/runtime**: Kotlin on Eclipse Temurin OpenJDK 17
- **Framework**: Ktor 2.3 (Netty) + Koin DI
- **Plugins**: CallLogging, OpenApi, HealthCheck, AutoDoc, Json/ContentNegotiation, WhitelabelContext, StatusPages
- **Main class**: `com.agoda.travelcard.api.ApplicationKt`
- **Container**: listens on `0.0.0.0:8080`, host `travel-card-api-standalone`, cluster `AP`
- **Observability**: OpenTelemetry Java agent 2.1.0 (OTLP → `localhost:4317/4318`)
- **Secrets**: Agoda Vault client (`http://localhost:8200`)
- **Database**: PostgreSQL via HikariCP pool `agoda-wallet-postgres\pgsql` (wired by `DatabaseConnectionModule`)
- **Proxy egress**: routes outbound through `sisproxy.hkg.agoda.local:3128` when `PROXY_ENABLED=true`

## Wise integration
Three logical Wise clients — each with its own OAuth client-credentials token flow via `WiseTokenManager`:

| Client   | Program scope                                    |
|----------|--------------------------------------------------|
| Physical | `VISA_DEBIT_CONSUMER_SG_1_PHYSICAL_CARDS_API`    |
| Virtual  | `VISA_DEBIT_CONSUMER_SG_1_CARDS_API`             |
| Subsidy  | Separate client-id/secret, shared `baseUrl`       |

Base URL: `https://api.wise-sandbox.com`.

## Endpoints
- `GET /index` — liveness probe (hit every ~10s)
- `GET /healthcheck` — readiness (reports DB pool status)
- `GET /openapi.json`, `GET /docs` — API spec
- `POST /account/transactions` — primary business endpoint; paginated list of travel-card transactions for an account

### Request body for `POST /account/transactions`
```json
{ "accountId": "acct_123", "limit": 50, "offset": 0 }
```

Request is whitelabel-aware — send `X-Agoda-Whitelabel` (default `AGODA`) to segment by partner brand.

## Run locally
```bash
docker compose up -d postgres
./gradlew run
```

Or fully containerized:
```bash
./gradlew shadowJar
docker compose up --build
```

## Notes
- If the Postgres pool fails to initialize, the server still boots and serves `/index`, but `POST /account/transactions` returns 500 — matching the Keploy-replayed behaviour.
