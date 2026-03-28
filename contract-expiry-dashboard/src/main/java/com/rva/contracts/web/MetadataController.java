package com.rva.contracts.web;

import com.rva.contracts.config.AppProperties;
import com.rva.contracts.repo.ContractRepository;
import com.rva.contracts.web.dto.ImportMetadataDto;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.time.ZoneId;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/v1")
public class MetadataController {

  private final JdbcTemplate jdbcTemplate;
  private final AppProperties appProperties;

  public MetadataController(JdbcTemplate jdbcTemplate, AppProperties appProperties) {
    this.jdbcTemplate = jdbcTemplate;
    this.appProperties = appProperties;
  }

  @GetMapping("/metadata")
  public ImportMetadataDto metadata() {
    List<Map<String, Object>> rows =
        jdbcTemplate.queryForList("SELECT source_url, loaded_at, row_count FROM import_metadata ORDER BY id DESC LIMIT 1");
    String source = null;
    String loadedAt = null;
    Integer rowCount = null;
    if (!rows.isEmpty()) {
      Map<String, Object> r = rows.get(0);
      Object su = r.get("source_url");
      Object la = r.get("loaded_at");
      Object rc = r.get("row_count");
      source = su == null ? null : su.toString();
      loadedAt = la == null ? null : la.toString();
      if (rc instanceof Number n) {
        rowCount = n.intValue();
      }
    }
    ZoneId z = ZoneId.systemDefault();
    return new ImportMetadataDto(
        source,
        loadedAt,
        rowCount,
        appProperties.getProvenanceUrl(),
        z.getId(),
        ContractRepository.referenceDate().toString());
  }
}
