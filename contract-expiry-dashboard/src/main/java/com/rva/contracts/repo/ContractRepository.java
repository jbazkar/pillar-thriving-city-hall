package com.rva.contracts.repo;

import com.rva.contracts.domain.EndDateScope;
import com.rva.contracts.domain.RenewalBucket;
import com.rva.contracts.web.dto.ContractDetailDto;
import com.rva.contracts.web.dto.ContractRowDto;
import com.rva.contracts.web.dto.CountByBucketDto;
import com.rva.contracts.web.dto.CountByDepartmentDto;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Repository;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDate;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.EnumMap;
import java.util.List;
import java.util.Optional;

@Repository
public class ContractRepository {

  private final JdbcTemplate jdbcTemplate;

  public ContractRepository(JdbcTemplate jdbcTemplate) {
    this.jdbcTemplate = jdbcTemplate;
  }

  public static LocalDate referenceDate() {
    return LocalDate.now(ZoneId.systemDefault());
  }

  public List<CountByDepartmentDto> countByDepartment(String departmentFilter, RenewalBucket renewalBucket) {
    String ref = referenceDate().toString();
    StringBuilder sql = new StringBuilder("SELECT agency_department AS d, COUNT(*) AS c FROM contract WHERE 1=1");
    List<Object> params = new ArrayList<>();
    appendDepartmentClause(sql, params, departmentFilter);
    appendRenewalBucketClause(sql, params, renewalBucket, ref);
    sql.append(" GROUP BY agency_department ORDER BY agency_department");
    return jdbcTemplate.query(
        sql.toString(),
        (rs, rowNum) -> new CountByDepartmentDto(rs.getString("d"), rs.getLong("c")),
        params.toArray());
  }

  public List<CountByBucketDto> countByRenewalBucket(
      String departmentFilter, RenewalBucket renewalBucketFilter) {
    String ref = referenceDate().toString();
    List<Object> params = new ArrayList<>();
    for (int i = 0; i < 6; i++) {
      params.add(ref);
    }
    StringBuilder extra = new StringBuilder();
    if (departmentFilter != null && !departmentFilter.isBlank()) {
      extra.append(" AND agency_department = ?");
      params.add(departmentFilter);
    }
    appendRenewalBucketClause(extra, params, renewalBucketFilter, ref);

    String inner =
        """
        SELECT CASE
          WHEN effective_to IS NULL THEN 'UNKNOWN_END'
          WHEN CAST((julianday(effective_to) - julianday(?)) AS INTEGER) BETWEEN 0 AND 30 THEN 'DAYS_0_30'
          WHEN CAST((julianday(effective_to) - julianday(?)) AS INTEGER) BETWEEN 31 AND 60 THEN 'DAYS_31_60'
          WHEN CAST((julianday(effective_to) - julianday(?)) AS INTEGER) BETWEEN 61 AND 90 THEN 'DAYS_61_90'
          WHEN CAST((julianday(effective_to) - julianday(?)) AS INTEGER) BETWEEN 91 AND 180 THEN 'DAYS_91_180'
          WHEN CAST((julianday(effective_to) - julianday(?)) AS INTEGER) >= 181 THEN 'DAYS_181_PLUS'
        END AS bucket_key
        FROM contract
        WHERE (effective_to IS NULL OR CAST((julianday(effective_to) - julianday(?)) AS INTEGER) >= 0)
        """
            + extra;

    String sql = "SELECT bucket_key, COUNT(*) AS c FROM (" + inner + ") WHERE bucket_key IS NOT NULL GROUP BY bucket_key";
    List<CountByBucketDto> rows =
        jdbcTemplate.query(
            sql,
            (rs, rn) -> new CountByBucketDto(rs.getString("bucket_key"), rs.getLong("c")),
            params.toArray());

    EnumMap<RenewalBucket, Long> map = new EnumMap<>(RenewalBucket.class);
    for (RenewalBucket b : RenewalBucket.values()) {
      map.put(b, 0L);
    }
    for (CountByBucketDto row : rows) {
      RenewalBucket b = RenewalBucket.valueOf(row.bucket());
      map.put(b, row.count());
    }
    List<CountByBucketDto> out = new ArrayList<>();
    for (RenewalBucket b : RenewalBucket.values()) {
      out.add(new CountByBucketDto(b.name(), map.get(b)));
    }
    return out;
  }

  public long countContracts(
      String departmentFilter, RenewalBucket renewalBucket, EndDateScope endDateScope) {
    String ref = referenceDate().toString();
    StringBuilder sql = new StringBuilder("SELECT COUNT(*) FROM contract WHERE 1=1");
    List<Object> params = new ArrayList<>();
    appendDepartmentClause(sql, params, departmentFilter);
    appendRenewalBucketClause(sql, params, renewalBucket, ref);
    appendEndDateScopeClause(sql, params, endDateScope, ref);
    Long c = jdbcTemplate.queryForObject(sql.toString(), Long.class, params.toArray());
    return c == null ? 0 : c;
  }

  public List<ContractRowDto> findContracts(
      String departmentFilter,
      RenewalBucket renewalBucket,
      EndDateScope endDateScope,
      int page,
      int size,
      String sort) {
    String ref = referenceDate().toString();
    StringBuilder sql = new StringBuilder("SELECT * FROM contract WHERE 1=1");
    List<Object> params = new ArrayList<>();
    appendDepartmentClause(sql, params, departmentFilter);
    appendRenewalBucketClause(sql, params, renewalBucket, ref);
    appendEndDateScopeClause(sql, params, endDateScope, ref);
    sql.append(" ").append(orderByClause(sort));
    sql.append(" LIMIT ? OFFSET ?");
    params.add(size);
    params.add(page * size);
    return jdbcTemplate.query(sql.toString(), ROW_MAPPER_LIST, params.toArray());
  }

  public Optional<ContractDetailDto> findById(long id) {
    List<ContractDetailDto> list =
        jdbcTemplate.query("SELECT * FROM contract WHERE id = ?", DETAIL_MAPPER, id);
    return list.stream().findFirst();
  }

  private static void appendDepartmentClause(StringBuilder sql, List<Object> params, String department) {
    if (department != null && !department.isBlank()) {
      sql.append(" AND agency_department = ?");
      params.add(department);
    }
  }

  private static void appendRenewalBucketClause(
      StringBuilder sql, List<Object> params, RenewalBucket bucket, String refIso) {
    if (bucket == null) {
      return;
    }
    switch (bucket) {
      case UNKNOWN_END -> sql.append(" AND effective_to IS NULL");
      case DAYS_0_30 -> {
        sql.append(
            " AND effective_to IS NOT NULL AND CAST((julianday(effective_to) - julianday(?)) AS INTEGER)"
                + " BETWEEN 0 AND 30");
        params.add(refIso);
      }
      case DAYS_31_60 -> {
        sql.append(
            " AND effective_to IS NOT NULL AND CAST((julianday(effective_to) - julianday(?)) AS INTEGER)"
                + " BETWEEN 31 AND 60");
        params.add(refIso);
      }
      case DAYS_61_90 -> {
        sql.append(
            " AND effective_to IS NOT NULL AND CAST((julianday(effective_to) - julianday(?)) AS INTEGER)"
                + " BETWEEN 61 AND 90");
        params.add(refIso);
      }
      case DAYS_91_180 -> {
        sql.append(
            " AND effective_to IS NOT NULL AND CAST((julianday(effective_to) - julianday(?)) AS INTEGER)"
                + " BETWEEN 91 AND 180");
        params.add(refIso);
      }
      case DAYS_181_PLUS -> {
        sql.append(
            " AND effective_to IS NOT NULL AND CAST((julianday(effective_to) - julianday(?)) AS INTEGER) >= 181");
        params.add(refIso);
      }
    }
  }

  private static void appendEndDateScopeClause(
      StringBuilder sql, List<Object> params, EndDateScope scope, String refIso) {
    if (scope == null || scope == EndDateScope.ALL) {
      return;
    }
    switch (scope) {
      case PAST ->
          sql.append(
              " AND effective_to IS NOT NULL AND CAST((julianday(effective_to) - julianday(?)) AS INTEGER) < 0");
      case UPCOMING ->
          sql.append(
              " AND effective_to IS NOT NULL AND CAST((julianday(effective_to) - julianday(?)) AS INTEGER) >= 0");
      case UNKNOWN -> sql.append(" AND effective_to IS NULL");
    }
    if (scope == EndDateScope.PAST || scope == EndDateScope.UPCOMING) {
      params.add(refIso);
    }
  }

  private static String orderByClause(String sort) {
    boolean desc = true;
    if (sort != null && sort.contains(",")) {
      String[] p = sort.split(",", 2);
      if ("effectiveTo".equalsIgnoreCase(p[0].trim())) {
        desc = !"asc".equalsIgnoreCase(p[1].trim());
      }
    }
    if (desc) {
      return "ORDER BY CASE WHEN effective_to IS NULL THEN 1 ELSE 0 END, effective_to DESC, id DESC";
    }
    return "ORDER BY CASE WHEN effective_to IS NULL THEN 1 ELSE 0 END, effective_to ASC, id ASC";
  }

  private static final RowMapper<ContractRowDto> ROW_MAPPER_LIST =
      (rs, rowNum) ->
          new ContractRowDto(
              rs.getLong("id"),
              rs.getString("agency_department"),
              rs.getString("contract_number"),
              rs.getObject("contract_value") != null ? rs.getDouble("contract_value") : null,
              rs.getString("supplier"),
              rs.getString("procurement_type"),
              rs.getString("effective_from"),
              rs.getString("effective_to"));

  private static final RowMapper<ContractDetailDto> DETAIL_MAPPER =
      new RowMapper<>() {
        @Override
        public ContractDetailDto mapRow(ResultSet rs, int rowNum) throws SQLException {
          return new ContractDetailDto(
              rs.getLong("id"),
              rs.getString("agency_department"),
              rs.getString("contract_number"),
              rs.getObject("contract_value") != null ? rs.getDouble("contract_value") : null,
              rs.getString("contract_value_raw"),
              rs.getString("supplier"),
              rs.getString("procurement_type"),
              rs.getString("description"),
              rs.getString("type_of_solicitation"),
              rs.getString("effective_from"),
              rs.getString("effective_to"),
              rs.getString("effective_from_raw"),
              rs.getString("effective_to_raw"),
              rs.getString("contract_summary"),
              rs.getString("loaded_at"));
        }
      };
}
