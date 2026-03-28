package com.rva.contracts.config;

import org.springframework.boot.context.properties.ConfigurationProperties;

import java.time.Duration;

@ConfigurationProperties(prefix = "gemini")
public class GeminiProperties {

  /** Server-side only; Google AI Studio / Gemini API key. Leave empty to disable extraction (503). */
  private String apiKey = "";

  /** Model id for {@code :generateContent} (text-in only after local PDF→text). Default suits Gemini 2.5 Flash. */
  private String model = "gemini-2.5-flash";

  /**
   * Max characters of extracted PDF text sent to Gemini (truncate with warning if longer).
   */
  private int maxExtractedTextChars = 800_000;

  private Duration connectTimeout = Duration.ofSeconds(30);
  private Duration readTimeout = Duration.ofMinutes(5);

  /** Inline PDF limit; Gemini recommends ~20MB for inline payloads (align with multipart). */
  private long maxFileBytes = 20L * 1024 * 1024;

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

  public int getMaxExtractedTextChars() {
    return maxExtractedTextChars;
  }

  public void setMaxExtractedTextChars(int maxExtractedTextChars) {
    this.maxExtractedTextChars = maxExtractedTextChars;
  }
}
