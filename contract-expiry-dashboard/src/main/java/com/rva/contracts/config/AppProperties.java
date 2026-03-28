package com.rva.contracts.config;

import org.springframework.boot.context.properties.ConfigurationProperties;

import java.nio.file.Path;

@ConfigurationProperties(prefix = "app")
public class AppProperties {

  /** Repository root containing `06_contracts_data/...` (parent of this module when running locally). */
  private String dataRoot = "..";
  private String csvRelativePath = "06_contracts_data/City_Contracts.csv";
  private String provenanceUrl = "";
  private String sqlitePath = "./data/contracts.db";
  private boolean bootstrap = false;

  public Path resolvedCsvPath() {
    return Path.of(dataRoot).normalize().resolve(csvRelativePath).normalize();
  }

  public String getDataRoot() {
    return dataRoot;
  }

  public void setDataRoot(String dataRoot) {
    this.dataRoot = dataRoot;
  }

  public String getCsvRelativePath() {
    return csvRelativePath;
  }

  public void setCsvRelativePath(String csvRelativePath) {
    this.csvRelativePath = csvRelativePath;
  }

  public String getProvenanceUrl() {
    return provenanceUrl;
  }

  public void setProvenanceUrl(String provenanceUrl) {
    this.provenanceUrl = provenanceUrl;
  }

  public String getSqlitePath() {
    return sqlitePath;
  }

  public void setSqlitePath(String sqlitePath) {
    this.sqlitePath = sqlitePath;
  }

  public boolean isBootstrap() {
    return bootstrap;
  }

  public void setBootstrap(boolean bootstrap) {
    this.bootstrap = bootstrap;
  }
}
