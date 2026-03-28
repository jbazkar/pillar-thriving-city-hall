package com.rva.contracts.config;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.DependsOn;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Component;

@Component
@DependsOn("sqliteDirectoryEnsurer")
public class DatabaseInitializer {

  private static final Logger log = LoggerFactory.getLogger(DatabaseInitializer.class);

  public DatabaseInitializer(JdbcTemplate jdbcTemplate) {
    jdbcTemplate.execute(
        """
        CREATE TABLE IF NOT EXISTS contract (
          id INTEGER PRIMARY KEY AUTOINCREMENT,
          agency_department TEXT,
          contract_number TEXT,
          contract_value REAL,
          contract_value_raw TEXT,
          supplier TEXT,
          procurement_type TEXT,
          description TEXT,
          type_of_solicitation TEXT,
          effective_from TEXT,
          effective_to TEXT,
          effective_from_raw TEXT,
          effective_to_raw TEXT,
          contract_summary TEXT,
          loaded_at TEXT NOT NULL
        )
        """);
    jdbcTemplate.execute(
        "CREATE INDEX IF NOT EXISTS idx_contract_agency_department ON contract(agency_department)");
    jdbcTemplate.execute("CREATE INDEX IF NOT EXISTS idx_contract_effective_to ON contract(effective_to)");
    jdbcTemplate.execute("CREATE INDEX IF NOT EXISTS idx_contract_loaded_at ON contract(loaded_at)");
    jdbcTemplate.execute(
        """
        CREATE TABLE IF NOT EXISTS import_metadata (
          id INTEGER PRIMARY KEY AUTOINCREMENT,
          source_url TEXT,
          loaded_at TEXT NOT NULL,
          row_count INTEGER,
          notes TEXT
        )
        """);
    log.debug("SQLite schema ensured");
  }
}
