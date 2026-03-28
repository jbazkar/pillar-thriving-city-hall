package com.rva.contracts.web;

import com.rva.contracts.extract.OpenAiContractExtractService;
import com.rva.contracts.web.dto.ContractExtractResponseDto;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

@RestController
@RequestMapping("/api/v1/extract")
public class ContractPdfExtractController {

  private final OpenAiContractExtractService extractService;

  public ContractPdfExtractController(OpenAiContractExtractService extractService) {
    this.extractService = extractService;
  }

  @PostMapping(value = "/contract-pdf", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
  public ContractExtractResponseDto extract(@RequestPart("file") MultipartFile file) throws java.io.IOException {
    return extractService.extract(file);
  }
}
