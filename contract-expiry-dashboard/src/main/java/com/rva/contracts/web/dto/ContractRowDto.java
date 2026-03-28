package com.rva.contracts.web.dto;

public record ContractRowDto(
    long id,
    String agencyDepartment,
    String contractNumber,
    Double contractValue,
    String supplier,
    String procurementType,
    String effectiveFrom,
    String effectiveTo) {}
