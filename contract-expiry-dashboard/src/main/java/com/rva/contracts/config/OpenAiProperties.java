package com.rva.contracts.config;

import org.springframework.boot.context.properties.ConfigurationProperties;

import java.time.Duration;

@ConfigurationProperties(prefix = "openai")
public class OpenAiProperties {

  /** Server-side only; leave empty to disable extraction (503). */
  private String apiKey = "";

  /** Must support PDF via Files API + Responses API (e.g. gpt-4o). */
  private String model = "gpt-4o";

  private Duration connectTimeout = Duration.ofSeconds(30);
  private Duration readTimeout = Duration.ofMinutes(5);

  /** Guardrail for upload size (align with multipart and OpenAI limits). */
  private long maxFileBytes = 32L * 1024 * 1024;

  public String getApiKey() {
    return apiKey;
  }

  public void setApiKey(String apiKey) {
    this.apiKey = apiKey;
  }

  public String getModel() {
    return model;
  }

  public void setModel(String model) {
    this.model = model;
  }

  public Duration getConnectTimeout() {
    return connectTimeout;
  }

  public void setConnectTimeout(Duration connectTimeout) {
    this.connectTimeout = connectTimeout;
  }

  public Duration getReadTimeout() {
    return readTimeout;
  }

  public void setReadTimeout(Duration readTimeout) {
    this.readTimeout = readTimeout;
  }

  public long getMaxFileBytes() {
    return maxFileBytes;
  }

  public void setMaxFileBytes(long maxFileBytes) {
    this.maxFileBytes = maxFileBytes;
  }
}
