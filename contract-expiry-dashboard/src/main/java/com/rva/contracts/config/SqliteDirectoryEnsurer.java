package com.rva.contracts.config;

import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

@Component
@Order(Ordered.HIGHEST_PRECEDENCE)
public class SqliteDirectoryEnsurer {

  public SqliteDirectoryEnsurer(AppProperties appProperties) throws IOException {
    Path path = Path.of(appProperties.getSqlitePath()).toAbsolutePath().normalize();
    if (path.getParent() != null) {
      Files.createDirectories(path.getParent());
    }
  }
}
