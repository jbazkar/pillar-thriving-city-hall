package com.rva.contracts.extract;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;

class GeminiContractExtractServiceTest {

  @Test
  void stripMarkdownFence_stripsCommonFence() {
    assertEquals(
        "{\"a\":1}",
        GeminiContractExtractService.stripMarkdownFence("```json\n{\"a\":1}\n```"));
  }

  @Test
  void stripMarkdownFence_noFenceUnchanged() {
    assertEquals("{\"x\":true}", GeminiContractExtractService.stripMarkdownFence("{\"x\":true}"));
  }

  @Test
  void extractGeminiResponseText_fromCandidates() throws Exception {
    String json =
        "{\"candidates\":[{\"content\":{\"parts\":[{\"text\":\"{\\\"fields\\\":{}}\"}]}}]}";
    ObjectMapper om = new ObjectMapper();
    String t = GeminiContractExtractService.extractGeminiResponseText(om.readTree(json));
    assertEquals("{\"fields\":{}}", t);
  }

  @Test
  void extractGeminiResponseText_nullSafe() {
    assertNull(GeminiContractExtractService.extractGeminiResponseText(null));
    ObjectNode empty = new ObjectMapper().createObjectNode();
    assertNull(GeminiContractExtractService.extractGeminiResponseText(empty));
  }
}
