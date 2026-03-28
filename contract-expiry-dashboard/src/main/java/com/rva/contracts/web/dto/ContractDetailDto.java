package com.rva.contracts.web.dto;

public record ContractDetailDto(
    long id,
    String agencyDepartment,
    String contractNumber,
    Double contractValue,
    String contractValueRaw,
    String supplier,
    String procurementType,
    String description,
    String typeOfSolicitation,
    String effectiveFrom,
    String effectiveTo,
    String effectiveFromRaw,
    String effectiveToRaw,
    String contractSummary,
    String loadedAt) {}
