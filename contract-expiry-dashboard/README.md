# Contract expiry dashboard (Shape C)

Single Spring Boot application (REST + SQLite) serving a React Material UI dashboard for Richmond **City Contracts** data. Matches the technical design in `pillar-thriving-city-hall/07_mvp_doc/shape_c_contract_expiry_dashboard_tdd.md`.

## Prerequisites

- JDK **17+**
- **Maven 3.9+** (full `mvn package` also installs a local Node via `frontend-maven-plugin`, so a system Node.js install is optional).

## One-time data load (bootstrap)

From this directory (`contract-expiry-dashboard`), with `app.data-root` defaulting to `..` (the **pillar-thriving-city-hall** repo root):

```bash
mvn -Pbackend-only -DskipTests package
java -jar target/contract-expiry-dashboard-0.1.0-SNAPSHOT.jar bootstrap
```

This reads `pillar-thriving-city-hall/06_contracts_data/City_Contracts.csv`, recreates rows in SQLite, and writes `import_metadata`.

Override paths if needed:

- `APP_DATA_ROOT` — directory that contains `06_contracts_data/` (default: `..` relative to the current working directory).
- `APP_SQLITE_PATH` — SQLite file (default: `./data/contracts.db`).

## Run the app

Full build (compiles the UI into the JAR):

```bash
mvn -DskipTests package
java -jar target/contract-expiry-dashboard-0.1.0-SNAPSHOT.jar
```

Open **http://localhost:8080**

### UI-only dev server (optional)

If you have Node.js installed:

```bash
cd frontend
npm install
npm run dev
```

Run the Spring Boot app on port 8080 in another terminal; Vite proxies `/api` to it.

## API (summary)

| Method | Path |
|--------|------|
| GET | `/api/v1/dashboard/by-department` — pie data (`department`, `renewalBucket`) |
| GET | `/api/v1/dashboard/by-renewal-bucket` — bar data (`department`) |
| GET | `/api/v1/contracts` — paginated table (`department`, `renewalBucket`, `endDateScope`, `page`, `size`, `sort`) |
| GET | `/api/v1/contracts/{id}` — dialog payload |
| GET | `/api/v1/metadata` — provenance and last load |

Backend-only build (skip frontend):

```bash
mvn -Pbackend-only -DskipTests package
```
