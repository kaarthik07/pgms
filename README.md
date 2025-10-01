# PGMS Backend — Fixed Build

- Java 21 / Spring Boot 3.3.x / Postgres
- Tenants CRUD, Rooms/Beds, global exceptions, SLF4J logging, validation.

## Run

1) Set env: PGMS_DB_URL, PGMS_DB_USER, PGMS_DB_PASSWORD
2) `mvn spring-boot:run`
3) Bootstrap org: `POST /api/v1/bootstrap/org/v2-colive`
4) Swagger: `/swagger-ui/index.html`
