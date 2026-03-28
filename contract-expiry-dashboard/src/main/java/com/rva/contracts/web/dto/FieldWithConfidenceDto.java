package com.rva.contracts.web.dto;

import com.fasterxml.jackson.annotation.JsonInclude;

@JsonInclude(JsonInclude.Include.NON_NULL)
public class FieldWithConfidenceDto {

  private String value;
  private double confidence;

  public FieldWithConfidenceDto() {}

  public FieldWithConfidenceDto(String value, double confidence) {
    this.value = value;
    this.confidence = confidence;
  }

  public String getValue() {
    return value;
  }

  public void setValue(String value) {
    this.value = value;
  }

  public double getConfidence() {
    return confidence;
  }

  public void setConfidence(double confidence) {
    this.confidence = confidence;
  }
}
