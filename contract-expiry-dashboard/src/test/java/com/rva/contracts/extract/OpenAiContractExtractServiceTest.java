package com.rva.contracts.extract;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;

class OpenAiContractExtractServiceTest {

  @Test
  void stripMarkdownFence_stripsCommonFence() {
    assertEquals(
        "{\"a\":1}",
        OpenAiContractExtractService.stripMarkdownFence("```json\n{\"a\":1}\n```"));
  }

  @Test
  void stripMarkdownFence_noFenceUnchanged() {
    assertEquals("{\"x\":true}", OpenAiContractExtractService.stripMarkdownFence("{\"x\":true}"));
  }

  @Test
  void extractOutputText_prefersTopLevelOutputText() {
    ObjectNode n = new ObjectMapper().createObjectNode();
    n.put("output_text", "hello");
    assertEquals("hello", OpenAiContractExtractService.extractOutputText(n));
  }

  @Test
  void extractOutputText_fromNestedOutput() throws Exception {
    String json =
        "{\"output\":[{\"type\":\"message\",\"content\":[{\"type\":\"output_text\",\"text\":\"{\\\"fields\\\":{}}\"}]}]}";
    ObjectMapper om = new ObjectMapper();
    String t = OpenAiContractExtractService.extractOutputText(om.readTree(json));
    assertEquals("{\"fields\":{}}", t);
  }

  @Test
  void extractOutputText_nullSafe() {
    assertNull(OpenAiContractExtractService.extractOutputText(null));
  }
}
