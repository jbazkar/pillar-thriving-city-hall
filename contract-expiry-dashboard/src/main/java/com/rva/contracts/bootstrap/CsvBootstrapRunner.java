package com.rva.contracts.bootstrap;

import com.opencsv.CSVReader;
import com.opencsv.CSVReaderBuilder;
import com.opencsv.RFC4180ParserBuilder;
import com.rva.contracts.config.AppProperties;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Component;
import org.springframework.boot.CommandLineRunner;

import java.io.Reader;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.List;
import java.util.Locale;

@Component
@ConditionalOnProperty(name = "app.bootstrap", havingValue = "true")
public class CsvBootstrapRunner implements CommandLineRunner {

  private static final Logger log = LoggerFactory.getLogger(CsvBootstrapRunner.class);

  private static final DateTimeFormatter[] DATE_FORMATS =
      new DateTimeFormatter[] {
        DateTimeFormatter.ofPattern("M/d/yyyy h:mm:ss a", Locale.US),
        DateTimeFormatter.ofPattern("MM/dd/yyyy h:mm:ss a", Locale.US),
        DateTimeFormatter.ISO_LOCAL_DATE
      };

  private final AppProperties appProperties;
  private final JdbcTemplate jdbcTemplate;

  public CsvBootstrapRunner(AppProperties appProperties, JdbcTemplate jdbcTemplate) {
    this.appProperties = appProperties;
    this.jdbcTemplate = jdbcTemplate;
  }

  @Override
  public void run(String... args) throws Exception {
    Path csvPath = appProperties.resolvedCsvPath();
    if (!Files.isRegularFile(csvPath)) {
      throw new IllegalStateException("CSV not found: " + csvPath.toAbsolutePath());
    }

    jdbcTemplate.execute("DELETE FROM contract");
    String loadedAt = LocalDateTime.now().toString();
    String insert =
        """
        INSERT INTO contract (
          agency_department, contract_number, contract_value, contract_value_raw,
          supplier, procurement_type, description, type_of_solicitation,
          effective_from, effective_to, effective_from_raw, effective_to_raw,
          contract_summary, loaded_at
        ) VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, NULL, ?)
        """;

    int loaded = 0;
    int skipped = 0;
    int dateParseFailures = 0;
    int valueParseFailures = 0;

    try (Reader reader = Files.newBufferedReader(csvPath);
        CSVReader csvReader =
            new CSVReaderBuilder(reader)
                .withCSVParser(new RFC4180ParserBuilder().build())
                .build()) {
      List<String[]> all = csvReader.readAll();
      if (all.isEmpty()) {
        log.warn("CSV is empty");
        return;
      }
      String[] headerRow = all.get(0);
      var idx = indexHeaders(headerRow);
      for (int r = 1; r < all.size(); r++) {
        String[] row = all.get(r);
        if (row.length == 0 || rowIsBlank(row)) {
          skipped++;
          continue;
        }
        try {
          String agency = value(row, idx, "Agency/Department");
          String contractNumber = value(row, idx, "Contract Number");
          String valueRaw = value(row, idx, "Contract Value");
          Double valueNum = parseMoney(valueRaw);
          if (valueNum == null && valueRaw != null && !valueRaw.isBlank()) {
            valueParseFailures++;
          }
          String supplier = value(row, idx, "Supplier");
          String procurementType = value(row, idx, "Procurement Type");
          String description = value(row, idx, "Description");
          String solicitation = value(row, idx, "Type of Solicitation");
          String fromRaw = value(row, idx, "Effective From");
          String toRaw = value(row, idx, "Effective To");
          String fromIso = parseDate(fromRaw);
          String toIso = parseDate(toRaw);
          if (fromIso == null && fromRaw != null && !fromRaw.isBlank()) {
            dateParseFailures++;
          }
          if (toIso == null && toRaw != null && !toRaw.isBlank()) {
            dateParseFailures++;
          }

          jdbcTemplate.update(
              insert,
              emptyToNull(agency),
              emptyToNull(contractNumber),
              valueNum,
              valueRaw,
              emptyToNull(supplier),
              emptyToNull(procurementType),
              emptyToNull(description),
              emptyToNull(solicitation),
              fromIso,
              toIso,
              fromRaw,
              toRaw,
              loadedAt);
          loaded++;
        } catch (Exception e) {
          log.warn("Skipping row {}: {}", r + 1, e.getMessage());
          skipped++;
        }
      }
    }

    String sourceNote = csvPath.toAbsolutePath().toString();
    jdbcTemplate.update(
        "INSERT INTO import_metadata (source_url, loaded_at, row_count, notes) VALUES (?, ?, ?, ?)",
        sourceNote,
        loadedAt,
        loaded,
        null);

    log.info(
        "Bootstrap complete: loaded={}, skipped={}, dateParseFailures(row partial)={}, valueParseFailures={}",
        loaded,
        skipped,
        dateParseFailures,
        valueParseFailures);
  }

  private static boolean rowIsBlank(String[] row) {
    for (String c : row) {
      if (c != null && !c.isBlank()) {
        return false;
      }
    }
    return true;
  }

  private record HeaderIndex(java.util.Map<String, Integer> map) {
    int get(String name) {
      Integer i = map.get(name);
      return i == null ? -1 : i;
    }
  }

  private static HeaderIndex indexHeaders(String[] headerRow) {
    var m = new java.util.HashMap<String, Integer>();
    for (int i = 0; i < headerRow.length; i++) {
      m.put(headerRow[i].trim(), i);
    }
    return new HeaderIndex(m);
  }

  private static String value(String[] row, HeaderIndex idx, String col) {
    int i = idx.get(col);
    if (i < 0 || i >= row.length) {
      return null;
    }
    return row[i];
  }

  private static String emptyToNull(String s) {
    if (s == null || s.isBlank()) {
      return null;
    }
    return s;
  }

  private static String parseDate(String raw) {
    if (raw == null || raw.isBlank()) {
      return null;
    }
    String t = raw.trim();
    for (DateTimeFormatter fmt : DATE_FORMATS) {
      try {
        if (fmt == DateTimeFormatter.ISO_LOCAL_DATE) {
          return LocalDate.parse(t, fmt).toString();
        }
        LocalDateTime ldt = LocalDateTime.parse(t, fmt);
        return ldt.toLocalDate().toString();
      } catch (DateTimeParseException ignored) {
        // try next
      }
    }
    return null;
  }

  private static Double parseMoney(String raw) {
    if (raw == null || raw.isBlank()) {
      return null;
    }
    try {
      String cleaned = raw.trim().replace(",", "");
      return Double.parseDouble(cleaned);
    } catch (NumberFormatException e) {
      return null;
    }
  }
}
