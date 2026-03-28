# Contract expiry dashboard (Shapes C + D)

Single Spring Boot application (REST + SQLite) serving a React Material UI app for Richmond **City Contracts** data.

- **Shape C** — contract expiry dashboard (charts, table, detail dialog): `07_mvp_doc/shape_c_contract_expiry_dashboard_tdd.md`
- **Shape D** — PDF contract extractor tab: `07_mvp_doc/shape_d_pdf_contract_extractor_tdd.md` (OpenAI **only** for PDF understanding; **no** server-side PDF text libraries such as PDFBox)

## Prerequisites

- JDK **17+**
- **Maven 3.9+** (full `mvn package` also installs a local Node via `frontend-maven-plugin`, so a system Node.js install is optional).
- **Shape D (PDF extraction):** set **`OPENAI_API_KEY`** on the server. Without it, the **PDF contract extractor** tab returns HTTP **503** with a clear message. Default model: **`gpt-4o`** (override with **`OPENAI_MODEL`**). Extraction uses OpenAI **Files API** (`purpose=user_data`) plus the **Responses API** (`POST /v1/responses`) with PDF attached by `file_id` — the document is read inside OpenAI’s stack, not via local PDF parsing.

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
- `OPENAI_API_KEY` — required for `POST /api/v1/extract/contract-pdf` (Shape D); never expose to the browser.
- `OPENAI_MODEL` — optional (default `gpt-4o`). Must support PDF input via the Responses API path above.
- `OPENAI_MAX_FILE_BYTES` — optional upload size cap aligned with `spring.servlet.multipart` (default `33554432`).

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

Frontend unit tests (confidence thresholds helper):

```bash
cd frontend
npm run test
```

**PDF preview (Shape D):** the UI embeds the selected file with a **blob URL** in an `<iframe>` (browser-native PDF viewer). The blob URL is revoked when you switch away from the tab or replace the file.

## API (summary)

| Method | Path |
|--------|------|
| GET | `/api/v1/dashboard/by-department` — pie data (`department`, `renewalBucket`) |
| GET | `/api/v1/dashboard/by-renewal-bucket` — bar data (`department`) |
| GET | `/api/v1/contracts` — paginated table (`department`, `renewalBucket`, `endDateScope`, `page`, `size`, `sort`) |
| GET | `/api/v1/contracts/{id}` — dialog payload |
| GET | `/api/v1/metadata` — provenance and last load |
| POST | `/api/v1/extract/contract-pdf` — multipart `file` (PDF only); JSON with per-field `value` + `confidence` (Shape D) |

Backend-only build (skip frontend):

```bash
mvn -Pbackend-only -DskipTests package
```
