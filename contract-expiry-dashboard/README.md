# Contract expiry dashboard (Shapes C + D)

Single Spring Boot application (REST + SQLite) serving a React Material UI app for Richmond **City Contracts** data.

- **Shape C** — contract expiry dashboard (charts, table, detail dialog): `07_mvp_doc/shape_c_contract_expiry_dashboard_tdd.md`
- **Shape D** — PDF contract extractor tab: `07_mvp_doc/shape_d_pdf_contract_extractor_tdd.md` (**Apache PDFBox** for PDF→text, then **Google Gemini** for extraction)

## Prerequisites

- JDK **17+**
- **Maven 3.9+** (full `mvn package` also installs a local Node via `frontend-maven-plugin`, so a system Node.js install is optional).
- **Shape D (PDF extraction):** you need a **Google AI Studio** API key ([create key](https://aistudio.google.com/apikey)). Without it, extraction returns HTTP **503**. Default model: **`gemini-2.5-flash`** (text-in; override with **`GEMINI_MODEL`**). PDFs are converted to text on the server with **Apache PDFBox**, then **`generateContent`** sends that text to Gemini (no raw PDF bytes to the model). Image-only/scanned PDFs may yield no text until OCR is added.

**Ways to provide the key (pick one):**

1. **Environment variable** (same terminal session as `java -jar`):
   - **PowerShell:** `$env:GEMINI_API_KEY="paste-your-key"` then `java -jar target/...`
   - **cmd.exe:** `set GEMINI_API_KEY=paste-your-key` then `java -jar ...`
   - **`GOOGLE_API_KEY`** also works (same value as in AI Studio).
2. **Config file:** from the `contract-expiry-dashboard` directory, copy `config/gemini-local.yml.example` to **`config/gemini-local.yml`**, set `gemini.api-key`, restart the app. That file is gitignored.
3. **Command line:** `java -jar target/...jar --gemini.api-key=paste-your-key`

If you set a variable in Windows **System** settings, **close and reopen** the terminal so the JVM sees it.

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
- `GEMINI_API_KEY` or `GOOGLE_API_KEY` — required for `POST /api/v1/extract/contract-pdf` (Shape D); never expose to the browser.
- `GEMINI_MODEL` — optional (default `gemini-2.5-flash`). Text-only input after local PDF→text extraction.
- `GEMINI_MAX_FILE_BYTES` — optional max upload size (default ~20MB).
- `GEMINI_MAX_EXTRACTED_TEXT_CHARS` — optional cap on characters sent to Gemini after PDFBox (default `800000`).

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
