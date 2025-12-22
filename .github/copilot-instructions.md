# Copilot / AI Agent Instructions for merbsconnect 🚀

Purpose: give an AI coding agent the minimum, high-value knowledge to be productive in this repo.

## Quick facts ✅
- Tech: **Spring Boot 3.4.4**, **Java 21** (see `pom.xml` property `java.version`).
- Build: **Maven** with included wrapper (`mvnw`, `mvnw.cmd`).
- Main app: `com.merbsconnect.MerbsconnectApplication` (entry point).
- Static front-end: `src/main/resources/static/index.html` (served at `/`).
- Key dependencies (in `pom.xml`): Actuator, Spring Data JPA, Mail, OAuth2 client, Quartz, Security, Validation, Web, Postgres driver, Lombok (optional).

## How to run & test (concrete) 🛠️
- Run locally (Linux/macOS): `./mvnw spring-boot:run`
- Run locally (Windows): `mvnw.cmd spring-boot:run`
- Run tests: `./mvnw test` (or `mvnw.cmd test` on Windows)
- Build jar: `./mvnw clean package` → run: `java -jar target/*.jar`

Environment / DB hints:
- Postgres is a runtime dependency (only driver present). Configure DB via `spring.datasource.*` properties in `application.properties`/`application.yaml` or environment variables (e.g. `SPRING_DATASOURCE_URL`, `SPRING_DATASOURCE_USERNAME`, `SPRING_DATASOURCE_PASSWORD`).
- No migration tool (Flyway/Liquibase) is present — adding migrations is a manual step if DB schema is needed.

## Project structure & conventions 📁
- Package root: `com.merbsconnect`. Keep new code under this package to match the Spring Boot component scan.
- Static assets: put static HTML/CSS/JS in `src/main/resources/static/`. `index.html` is a complete, self-contained Tailwind-based front-end.
- Tests: existing test is a context-load smoke test at `src/test/java/com/merbsconnect/MerbsconnectApplicationTests.java`.
- Lombok is declared optional; enable annotation processing in the IDE if using Lombok.

## Integration points and what needs explicit config 🔗
- Postgres (runtime DB) — requires datasource config to actually connect.
- OAuth2 client, Mail, Quartz, Security — present as dependencies but **not** necessarily configured in properties. If you enable them, update `application.properties`/`application.yaml` and add environment variable examples in PR description.
- Actuator is included: useful for health/metrics once proper management endpoints are configured.

## Examples for common AI tasks (copy-paste prompts) ✍️
- Add a simple health endpoint with test:
  - "Create `src/main/java/com/merbsconnect/web/HealthController.java` with a GET `/health` that returns `ok` and add a unit test `src/test/java/com/merbsconnect/web/HealthControllerTest.java` using MockMvc. Use package `com.merbsconnect.web` and register with Spring." 
- Add database-backed entity and repo:
  - "Create `User` JPA entity in `com.merbsconnect.model`, `UserRepository` in `com.merbsconnect.repository` (extends `JpaRepository`), and a small integration test that uses an H2 in-memory DB for verification." (Note: update `pom.xml` only if adding a DB-testing dependency, and add configuration to `test` scope.)

## Safety & minimal-impact guidance ⚠️
- Don't change the top-level package name (`com.merbsconnect`). It is referenced by Spring Boot and tests.
- Prefer using the Maven wrapper (`mvnw` / `mvnw.cmd`) instead of system Maven to avoid environment mismatch.
- When adding third-party tools (migrations, test DBs), add them in `pom.xml` with appropriate scopes and record the change in the commit message.

## Where to look for the following examples 🔎
- Main: `src/main/java/com/merbsconnect/MerbsconnectApplication.java`
- App properties: `src/main/resources/application.properties` and `application.yaml` (currently empty)
- Static front-end example: `src/main/resources/static/index.html`
- Build definition & deps: `pom.xml`
- Smoke test: `src/test/java/com/merbsconnect/MerbsconnectApplicationTests.java`

---
If any of the above items are unclear or you'd like additional examples (controller examples, testing patterns, DB migration suggestions), tell me which area to expand and I will update this file. ✅
