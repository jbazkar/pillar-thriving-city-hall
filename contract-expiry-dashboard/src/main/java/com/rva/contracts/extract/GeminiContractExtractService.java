package com.rva.contracts.extract;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.rva.contracts.config.GeminiProperties;
import com.rva.contracts.web.dto.ContractExtractResponseDto;
import com.rva.contracts.web.dto.ExtractMetaDto;
import com.rva.contracts.web.dto.FieldWithConfidenceDto;
import org.apache.pdfbox.Loader;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.text.PDFTextStripper;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.HttpStatusCodeException;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.server.ResponseStatusException;
import org.springframework.web.util.UriComponentsBuilder;

import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

/**
 * PDF field extraction: text is extracted locally with Apache PDFBox, then sent to Google Gemini
 * ({@code generateContent}) as plain text — suitable for text-only models (e.g. Gemini 2.5 Flash).
 */
@Service
public class GeminiContractExtractService {

  private static final byte[] PDF_MAGIC = "%PDF".getBytes(StandardCharsets.US_ASCII);

  static final String[] FIELD_KEYS = {
      "agencyDepartment",
      "contractNumber",
      "contractValue",
      "supplier",
      "procurementType",
      "description",
      "typeOfSolicitation",
      "effectiveFrom",
      "effectiveTo",
      "synopsis",
  };

  private static final String GENERATE_BASE =
      "https://generativelanguage.googleapis.com/v1beta/models/";

  private final GeminiProperties geminiProperties;
  private final RestTemplate geminiRestTemplate;
  private final ObjectMapper objectMapper;

  public GeminiContractExtractService(
      GeminiProperties geminiProperties,
      @Qualifier("geminiRestTemplate") RestTemplate geminiRestTemplate,
      ObjectMapper objectMapper) {
    this.geminiProperties = geminiProperties;
    this.geminiRestTemplate = geminiRestTemplate;
    this.objectMapper = objectMapper;
  }

  public ContractExtractResponseDto extract(MultipartFile file) throws IOException {
    if (geminiProperties.getApiKey() == null || geminiProperties.getApiKey().isBlank()) {
      throw new ResponseStatusException(
          HttpStatus.SERVICE_UNAVAILABLE,
          "Extraction is not configured. Set GEMINI_API_KEY or GOOGLE_API_KEY in the environment, "
              + "or add gemini.api-key in config/gemini-local.yml (see config/gemini-local.yml.example), "
              + "or pass --gemini.api-key=YOUR_KEY when starting the JAR.");
    }
    if (file == null || file.isEmpty()) {
      throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Missing file part `file`.");
    }
    validatePdf(file);

    byte[] bytes = file.getBytes();

    try {
      String extracted = extractTextFromPdf(bytes);
      if (extracted == null || extracted.isBlank()) {
        throw new ResponseStatusException(
            HttpStatus.UNPROCESSABLE_ENTITY,
            "No text could be extracted from this PDF. Scanned or image-only PDFs may need OCR outside this demo.");
      }

      List<String> truncationWarnings = new ArrayList<>();
      String toModel = truncateExtractedText(extracted, truncationWarnings);

      String outputJson = callGeminiGenerateContent(toModel);
      ContractExtractResponseDto dto = mapToResponse(outputJson);
      for (int i = truncationWarnings.size() - 1; i >= 0; i--) {
        dto.getMeta().getWarnings().add(0, truncationWarnings.get(i));
      }
      return dto;
    } catch (ResponseStatusException e) {
      throw e;
    } catch (Exception e) {
      throw new ResponseStatusException(
          HttpStatus.BAD_GATEWAY, "Gemini extraction failed: " + e.getMessage(), e);
    }
  }

  void validatePdf(MultipartFile file) throws IOException {
    String name = Optional.ofNullable(file.getOriginalFilename()).orElse("");
    if (!name.toLowerCase().endsWith(".pdf")) {
      throw new ResponseStatusException(HttpStatus.UNSUPPORTED_MEDIA_TYPE, "Only .pdf files are accepted.");
    }
    String ct = file.getContentType();
    if (ct != null
        && !ct.isBlank()
        && !MediaType.APPLICATION_PDF_VALUE.equals(ct)
        && !"application/octet-stream".equalsIgnoreCase(ct)) {
      throw new ResponseStatusException(
          HttpStatus.UNSUPPORTED_MEDIA_TYPE, "Content-Type must be application/pdf (or octet-stream for some clients).");
    }
    long max = geminiProperties.getMaxFileBytes();
    if (file.getSize() > max) {
      throw new ResponseStatusException(
          HttpStatus.PAYLOAD_TOO_LARGE, "File exceeds maximum size of " + max + " bytes.");
    }
    try (InputStream in = file.getInputStream()) {
      byte[] head = new byte[PDF_MAGIC.length];
      int n = in.readNBytes(head, 0, PDF_MAGIC.length);
      if (n < PDF_MAGIC.length) {
        throw new ResponseStatusException(HttpStatus.UNSUPPORTED_MEDIA_TYPE, "File is not a valid PDF.");
      }
      for (int i = 0; i < PDF_MAGIC.length; i++) {
        if (head[i] != PDF_MAGIC[i]) {
          throw new ResponseStatusException(HttpStatus.UNSUPPORTED_MEDIA_TYPE, "File is not a valid PDF.");
        }
      }
    }
  }

  private String extractTextFromPdf(byte[] pdfBytes) {
    try (PDDocument doc = Loader.loadPDF(pdfBytes)) {
      PDFTextStripper stripper = new PDFTextStripper();
      stripper.setSortByPosition(true);
      return stripper.getText(doc);
    } catch (IOException e) {
      throw new ResponseStatusException(
          HttpStatus.UNPROCESSABLE_ENTITY, "Could not read PDF for text extraction: " + e.getMessage(), e);
    }
  }

  private String truncateExtractedText(String text, List<String> warningsOut) {
    int max = geminiProperties.getMaxExtractedTextChars();
    if (max <= 0 || text.length() <= max) {
      return text;
    }
    warningsOut.add(
        "Extracted text was truncated from "
            + text.length()
            + " to "
            + max
            + " characters for the model request.");
    return text.substring(0, max);
  }

  private String callGeminiGenerateContent(String extractedContractText) throws Exception {
    String userMessage = buildUserMessage(extractedContractText);

    ObjectNode root = objectMapper.createObjectNode();
    ObjectNode content = objectMapper.createObjectNode();
    ArrayNode parts = objectMapper.createArrayNode();
    ObjectNode textPart = objectMapper.createObjectNode();
    textPart.put("text", userMessage);
    parts.add(textPart);
    content.set("parts", parts);
    ArrayNode contents = objectMapper.createArrayNode();
    contents.add(content);
    root.set("contents", contents);

    ObjectNode gen = objectMapper.createObjectNode();
    gen.put("responseMimeType", "application/json");
    gen.put("temperature", 0.2);
    root.set("generationConfig", gen);

    String url =
        UriComponentsBuilder.fromHttpUrl(GENERATE_BASE + geminiProperties.getModel() + ":generateContent")
            .queryParam("key", geminiProperties.getApiKey())
            .build(true)
            .toUriString();

    HttpHeaders headers = new HttpHeaders();
    headers.setContentType(MediaType.APPLICATION_JSON);
    HttpEntity<String> entity = new HttpEntity<>(objectMapper.writeValueAsString(root), headers);

    ResponseEntity<String> resp;
    try {
      resp = geminiRestTemplate.exchange(url, HttpMethod.POST, entity, String.class);
    } catch (HttpStatusCodeException e) {
      throw mapGeminiHttpError(e);
    }

    JsonNode body = objectMapper.readTree(resp.getBody());
    if (body.hasNonNull("error")) {
      throw geminiErrorToException(body.get("error"));
    }

    String outputText = extractGeminiResponseText(body);
    if (outputText == null || outputText.isBlank()) {
      if (body.path("promptFeedback").path("blockReason").isTextual()) {
        throw new ResponseStatusException(
            HttpStatus.UNPROCESSABLE_ENTITY,
            "Request blocked: " + body.path("promptFeedback").path("blockReason").asText());
      }
      throw new ResponseStatusException(HttpStatus.UNPROCESSABLE_ENTITY, "Model returned no JSON output.");
    }
    return outputText;
  }

  private static ResponseStatusException geminiErrorToException(JsonNode errorNode) {
    String msg = errorNode.path("message").asText("Gemini API error");
    int code = errorNode.path("code").asInt(0);
    if (code == 400 || "INVALID_ARGUMENT".equals(errorNode.path("status").asText())) {
      return new ResponseStatusException(HttpStatus.UNPROCESSABLE_ENTITY, msg);
    }
    if (code == 429) {
      return new ResponseStatusException(HttpStatus.SERVICE_UNAVAILABLE, "Gemini rate limit or quota: " + msg);
    }
    return new ResponseStatusException(HttpStatus.BAD_GATEWAY, msg);
  }

  private ResponseStatusException mapGeminiHttpError(HttpStatusCodeException e) {
    HttpStatus status = HttpStatus.resolve(e.getStatusCode().value());
    String raw = e.getResponseBodyAsString(StandardCharsets.UTF_8);
    try {
      JsonNode n = objectMapper.readTree(raw);
      if (n.hasNonNull("error")) {
        throw geminiErrorToException(n.get("error"));
      }
    } catch (ResponseStatusException rse) {
      return rse;
    } catch (Exception ignored) {
      // fall through
    }
    if (status == HttpStatus.PAYLOAD_TOO_LARGE) {
      return new ResponseStatusException(HttpStatus.PAYLOAD_TOO_LARGE, raw, e);
    }
    if (status == HttpStatus.TOO_MANY_REQUESTS) {
      return new ResponseStatusException(HttpStatus.SERVICE_UNAVAILABLE, "Gemini rate limit.", e);
    }
    return new ResponseStatusException(HttpStatus.BAD_GATEWAY, e.getMessage(), e);
  }

  /** Gemini {@code generateContent} response: {@code candidates[0].content.parts[0].text}. */
  static String extractGeminiResponseText(JsonNode responseRoot) {
    if (responseRoot == null) {
      return null;
    }
    JsonNode candidates = responseRoot.path("candidates");
    if (!candidates.isArray() || candidates.isEmpty()) {
      return null;
    }
    JsonNode parts = candidates.get(0).path("content").path("parts");
    if (!parts.isArray() || parts.isEmpty()) {
      return null;
    }
    JsonNode text = parts.get(0).path("text");
    return text.isMissingNode() || !text.isTextual() ? null : text.asText();
  }

  private ContractExtractResponseDto mapToResponse(String modelJson) throws IOException {
    String trimmed = stripMarkdownFence(modelJson.trim());
    JsonNode root = objectMapper.readTree(trimmed);
    JsonNode fieldsNode = root.path("fields");

    Map<String, FieldWithConfidenceDto> fields = new LinkedHashMap<>();
    for (String key : FIELD_KEYS) {
      fields.put(key, defaultField());
    }

    if (fieldsNode.isObject()) {
      fieldsNode
          .fields()
          .forEachRemaining(
              e -> {
                String k = e.getKey();
                if (!fields.containsKey(k)) {
                  return;
                }
                JsonNode fv = e.getValue();
                if (fv != null && fv.isObject()) {
                  String val = textOrNull(fv.get("value"));
                  double conf = clampConfidence(fv.get("confidence"));
                  fields.put(k, new FieldWithConfidenceDto(val, conf));
                }
              });
    }

    ContractExtractResponseDto dto = new ContractExtractResponseDto();
    dto.setFields(fields);

    ExtractMetaDto meta = new ExtractMetaDto();
    meta.setModel(geminiProperties.getModel());
    List<String> warnings = new ArrayList<>();
    JsonNode metaNode = root.path("meta");
    if (metaNode.isObject() && metaNode.path("warnings").isArray()) {
      for (JsonNode w : metaNode.path("warnings")) {
        if (w.isTextual()) {
          warnings.add(w.asText());
        }
      }
    }
    meta.setWarnings(warnings);
    dto.setMeta(meta);
    return dto;
  }

  private static FieldWithConfidenceDto defaultField() {
    return new FieldWithConfidenceDto(null, 0.0);
  }

  private static String textOrNull(JsonNode n) {
    if (n == null || n.isNull()) {
      return null;
    }
    if (n.isTextual()) {
      String s = n.asText();
      return s.isBlank() ? null : s;
    }
    if (n.isNumber()) {
      return n.toString();
    }
    return n.asText(null);
  }

  private static double clampConfidence(JsonNode n) {
    if (n == null || !n.isNumber()) {
      return 0.0;
    }
    double v = n.doubleValue();
    if (v < 0) {
      return 0.0;
    }
    if (v > 1) {
      return 1.0;
    }
    return v;
  }

  static String stripMarkdownFence(String s) {
    if (s.startsWith("```")) {
      int firstNl = s.indexOf('\n');
      if (firstNl > 0) {
        int end = s.lastIndexOf("```");
        if (end > firstNl) {
          return s.substring(firstNl + 1, end).trim();
        }
      }
    }
    return s;
  }

  private String buildUserMessage(String extractedContractText) {
    return """
        You are assisting procurement staff. The text below was extracted from a contract PDF on the server (not the raw PDF). Infer fields from this text only.

        Respond with JSON only (no markdown). The JSON must have a top-level object with:
        - "fields": an object whose keys are exactly:
          agencyDepartment, contractNumber, contractValue, supplier, procurementType, description,
          typeOfSolicitation, effectiveFrom, effectiveTo, synopsis
        Each field must be an object: { "value": string or null, "confidence": number between 0 and 1 }.
        If a value is unknown or not in the text, set "value" to null and use a low confidence (e.g. 0.1–0.3).
        Use ISO dates YYYY-MM-DD for effectiveFrom and effectiveTo when you can infer them; otherwise null.
        For contractValue, use a concise string as in the document (e.g. currency + amount).
        The synopsis should summarize the contract scope in plain text; it may be long.
        - "meta": optional { "warnings": string[] } for issues (e.g. missing sections in the extract).

        Output valid JSON only.

        --- Extracted contract text ---

        """
        + extractedContractText;
  }
}
