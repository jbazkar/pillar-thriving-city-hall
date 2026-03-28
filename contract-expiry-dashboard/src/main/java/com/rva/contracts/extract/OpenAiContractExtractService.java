package com.rva.contracts.extract;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.rva.contracts.config.OpenAiProperties;
import com.rva.contracts.web.dto.ContractExtractResponseDto;
import com.rva.contracts.web.dto.ExtractMetaDto;
import com.rva.contracts.web.dto.FieldWithConfidenceDto;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.HttpStatusCodeException;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.server.ResponseStatusException;

import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@Service
public class OpenAiContractExtractService {

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

  private static final String FILES_URL = "https://api.openai.com/v1/files";
  private static final String RESPONSES_URL = "https://api.openai.com/v1/responses";

  private final OpenAiProperties openAiProperties;
  private final RestTemplate openAiRestTemplate;
  private final ObjectMapper objectMapper;

  public OpenAiContractExtractService(
      OpenAiProperties openAiProperties,
      @Qualifier("openAiRestTemplate") RestTemplate openAiRestTemplate,
      ObjectMapper objectMapper) {
    this.openAiProperties = openAiProperties;
    this.openAiRestTemplate = openAiRestTemplate;
    this.objectMapper = objectMapper;
  }

  public ContractExtractResponseDto extract(MultipartFile file) throws IOException {
    if (openAiProperties.getApiKey() == null || openAiProperties.getApiKey().isBlank()) {
      throw new ResponseStatusException(
          HttpStatus.SERVICE_UNAVAILABLE,
          "Extraction is not configured (missing OPENAI_API_KEY on the server).");
    }
    if (file == null || file.isEmpty()) {
      throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Missing file part `file`.");
    }
    validatePdf(file);

    byte[] bytes = file.getBytes();
    String originalFilename = Optional.ofNullable(file.getOriginalFilename()).orElse("contract.pdf");

    String fileId = uploadPdf(bytes, originalFilename);
    try {
      String outputJson = callResponsesApi(fileId);
      return mapToResponse(outputJson);
    } catch (ResponseStatusException e) {
      throw e;
    } catch (Exception e) {
      throw new ResponseStatusException(
          HttpStatus.BAD_GATEWAY, "OpenAI extraction failed: " + e.getMessage(), e);
    } finally {
      deleteFileQuietly(fileId);
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
    long max = openAiProperties.getMaxFileBytes();
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

  private String uploadPdf(byte[] bytes, String filename) {
    HttpHeaders headers = new HttpHeaders();
    headers.setBearerAuth(openAiProperties.getApiKey());
    headers.setContentType(MediaType.MULTIPART_FORM_DATA);

    MultiValueMap<String, Object> body = new LinkedMultiValueMap<>();
    body.add("purpose", "user_data");
    body.add(
        "file",
        new ByteArrayResource(bytes) {
          @Override
          public String getFilename() {
            return filename;
          }
        });

    HttpEntity<MultiValueMap<String, Object>> entity = new HttpEntity<>(body, headers);
    try {
      ResponseEntity<String> resp =
          openAiRestTemplate.exchange(FILES_URL, HttpMethod.POST, entity, String.class);
      JsonNode root = objectMapper.readTree(resp.getBody());
      if (root.hasNonNull("error")) {
        throw upstreamError(root.get("error"));
      }
      String id = root.path("id").asText(null);
      if (id == null || id.isBlank()) {
        throw new ResponseStatusException(HttpStatus.BAD_GATEWAY, "OpenAI Files API returned no file id.");
      }
      return id;
    } catch (HttpStatusCodeException e) {
      throw mapOpenAiHttpError(e, "upload");
    } catch (ResponseStatusException e) {
      throw e;
    } catch (Exception e) {
      throw new ResponseStatusException(HttpStatus.BAD_GATEWAY, "OpenAI file upload failed: " + e.getMessage(), e);
    }
  }

  private void deleteFileQuietly(String fileId) {
    if (fileId == null || fileId.isBlank()) {
      return;
    }
    try {
      HttpHeaders headers = new HttpHeaders();
      headers.setBearerAuth(openAiProperties.getApiKey());
      HttpEntity<Void> entity = new HttpEntity<>(headers);
      openAiRestTemplate.exchange(FILES_URL + "/" + fileId, HttpMethod.DELETE, entity, String.class);
    } catch (Exception ignored) {
      // best-effort cleanup
    }
  }

  private String callResponsesApi(String fileId) throws Exception {
    ObjectNode root = objectMapper.createObjectNode();
    root.put("model", openAiProperties.getModel());

    ObjectNode textFormat = objectMapper.createObjectNode();
    textFormat.putObject("format").put("type", "json_object");
    root.set("text", textFormat);

    ArrayNode input = root.putArray("input");
    ObjectNode userMsg = input.addObject();
    userMsg.put("role", "user");
    ArrayNode content = userMsg.putArray("content");

    ObjectNode filePart = content.addObject();
    filePart.put("type", "input_file");
    filePart.put("file_id", fileId);

    ObjectNode textPart = content.addObject();
    textPart.put("type", "input_text");
    textPart.put("text", buildExtractionPrompt());

    HttpHeaders headers = new HttpHeaders();
    headers.setBearerAuth(openAiProperties.getApiKey());
    headers.setContentType(MediaType.APPLICATION_JSON);
    HttpEntity<String> entity = new HttpEntity<>(objectMapper.writeValueAsString(root), headers);

    ResponseEntity<String> resp;
    try {
      resp = openAiRestTemplate.exchange(RESPONSES_URL, HttpMethod.POST, entity, String.class);
    } catch (HttpStatusCodeException e) {
      throw mapOpenAiHttpError(e, "responses");
    }

    JsonNode body = objectMapper.readTree(resp.getBody());
    if (body.hasNonNull("error")) {
      throw upstreamError(body.get("error"));
    }
    String outputText = extractOutputText(body);
    if (outputText == null || outputText.isBlank()) {
      throw new ResponseStatusException(HttpStatus.UNPROCESSABLE_ENTITY, "Model returned no JSON output.");
    }
    return outputText;
  }

  private static ResponseStatusException upstreamError(JsonNode errorNode) {
    String msg = errorNode.path("message").asText("OpenAI error");
    return new ResponseStatusException(HttpStatus.BAD_GATEWAY, msg);
  }

  private ResponseStatusException mapOpenAiHttpError(HttpStatusCodeException e, String phase) {
    HttpStatus status = HttpStatus.resolve(e.getStatusCode().value());
    String body = e.getResponseBodyAsString(StandardCharsets.UTF_8);
    try {
      JsonNode n = objectMapper.readTree(body);
      if (n.hasNonNull("error")) {
        String msg = n.get("error").path("message").asText(e.getMessage());
        if (status == HttpStatus.PAYLOAD_TOO_LARGE) {
          return new ResponseStatusException(HttpStatus.PAYLOAD_TOO_LARGE, msg, e);
        }
        if (status != null && status.is4xxClientError()) {
          return new ResponseStatusException(HttpStatus.UNPROCESSABLE_ENTITY, msg, e);
        }
        return new ResponseStatusException(HttpStatus.BAD_GATEWAY, phase + ": " + msg, e);
      }
    } catch (Exception ignored) {
      // fall through
    }
    if (status == HttpStatus.TOO_MANY_REQUESTS) {
      return new ResponseStatusException(HttpStatus.SERVICE_UNAVAILABLE, "OpenAI rate limit or overload.", e);
    }
    return new ResponseStatusException(HttpStatus.BAD_GATEWAY, phase + ": " + e.getMessage(), e);
  }

  static String extractOutputText(JsonNode responseRoot) {
    if (responseRoot == null) {
      return null;
    }
    if (responseRoot.hasNonNull("output_text")) {
      return responseRoot.get("output_text").asText();
    }
    JsonNode output = responseRoot.get("output");
    if (output != null && output.isArray()) {
      for (JsonNode item : output) {
        String t = extractFromContentNode(item);
        if (t != null) {
          return t;
        }
      }
    }
    return null;
  }

  private static String extractFromContentNode(JsonNode node) {
    if (node == null) {
      return null;
    }
    JsonNode content = node.get("content");
    if (content != null && content.isArray()) {
      for (JsonNode part : content) {
        if ("output_text".equals(part.path("type").asText()) && part.hasNonNull("text")) {
          return part.get("text").asText();
        }
      }
    }
    return null;
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
    meta.setModel(openAiProperties.getModel());
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

  private String buildExtractionPrompt() {
    return """
        You are assisting procurement staff. Read the attached contract PDF and extract these fields.
        Respond with JSON only (no markdown). The JSON must have a top-level object with:
        - "fields": an object whose keys are exactly:
          agencyDepartment, contractNumber, contractValue, supplier, procurementType, description,
          typeOfSolicitation, effectiveFrom, effectiveTo, synopsis
        Each field must be an object: { "value": string or null, "confidence": number between 0 and 1 }.
        If a value is unknown or not in the document, set "value" to null and use a low confidence (e.g. 0.1–0.3).
        Use ISO dates YYYY-MM-DD for effectiveFrom and effectiveTo when you can infer them; otherwise null.
        For contractValue, use a concise string as printed in the document (e.g. currency + amount).
        The synopsis should summarize the contract scope in plain text; it may be long.
        - "meta": optional { "warnings": string[] } for issues (e.g. unreadable scan).

        The word JSON must appear in your reasoning context — output valid JSON only.
        """;
  }
}
