package com.rva.contracts.web.dto;

public record ImportMetadataDto(
    String sourceUrl,
    String loadedAt,
    Integer rowCount,
    String provenanceUrl,
    String referenceTimezone,
    String referenceDate) {}
