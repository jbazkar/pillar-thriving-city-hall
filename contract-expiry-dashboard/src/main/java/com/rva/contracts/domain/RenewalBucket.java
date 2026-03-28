package com.rva.contracts.domain;

import com.fasterxml.jackson.annotation.JsonCreator;

import java.util.Arrays;

public enum RenewalBucket {
  DAYS_0_30,
  DAYS_31_60,
  DAYS_61_90,
  DAYS_91_180,
  DAYS_181_PLUS,
  UNKNOWN_END;

  @JsonCreator
  public static RenewalBucket fromParam(String raw) {
    if (raw == null || raw.isBlank()) {
      return null;
    }
    return Arrays.stream(values())
        .filter(v -> v.name().equalsIgnoreCase(raw.trim()))
        .findFirst()
        .orElseThrow(() -> new IllegalArgumentException("Unknown renewalBucket: " + raw));
  }
}
