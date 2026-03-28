package com.rva.contracts.web.dto;

import com.fasterxml.jackson.annotation.JsonInclude;

import java.util.LinkedHashMap;
import java.util.Map;

@JsonInclude(JsonInclude.Include.NON_NULL)
public class ContractExtractResponseDto {

  private Map<String, FieldWithConfidenceDto> fields = new LinkedHashMap<>();
  private ExtractMetaDto meta = new ExtractMetaDto();

  public Map<String, FieldWithConfidenceDto> getFields() {
    return fields;
  }

  public void setFields(Map<String, FieldWithConfidenceDto> fields) {
    this.fields = fields != null ? fields : new LinkedHashMap<>();
  }

  public ExtractMetaDto getMeta() {
    return meta;
  }

  public void setMeta(ExtractMetaDto meta) {
    this.meta = meta != null ? meta : new ExtractMetaDto();
  }
}
