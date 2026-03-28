package com.rva.contracts.domain;

import com.fasterxml.jackson.annotation.JsonCreator;

import java.util.Arrays;

public enum EndDateScope {
  ALL,
  PAST,
  UPCOMING,
  UNKNOWN;

  @JsonCreator
  public static EndDateScope fromParam(String raw) {
    if (raw == null || raw.isBlank()) {
      return ALL;
    }
    return Arrays.stream(values())
        .filter(v -> v.name().equalsIgnoreCase(raw.trim()))
        .findFirst()
        .orElseThrow(() -> new IllegalArgumentException("Unknown endDateScope: " + raw));
  }
}
