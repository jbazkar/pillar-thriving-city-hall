package com.rva.contracts.web;

import com.rva.contracts.domain.EndDateScope;
import com.rva.contracts.domain.RenewalBucket;
import com.rva.contracts.repo.ContractRepository;
import com.rva.contracts.web.dto.ContractDetailDto;
import com.rva.contracts.web.dto.ContractRowDto;
import com.rva.contracts.web.dto.PageResponse;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;

@RestController
@RequestMapping("/api/v1/contracts")
public class ContractController {

  private final ContractRepository contractRepository;

  public ContractController(ContractRepository contractRepository) {
    this.contractRepository = contractRepository;
  }

  @GetMapping
  public PageResponse<ContractRowDto> list(
      @RequestParam(required = false) String department,
      @RequestParam(required = false) String renewalBucket,
      @RequestParam(required = false, defaultValue = "ALL") String endDateScope,
      @RequestParam(defaultValue = "0") int page,
      @RequestParam(defaultValue = "20") int size,
      @RequestParam(defaultValue = "effectiveTo,asc") String sort) {
    if (page < 0 || size < 1 || size > 100) {
      throw new IllegalArgumentException("Invalid pagination: page must be >= 0, size 1–100");
    }
    RenewalBucket rb = renewalBucket == null || renewalBucket.isBlank() ? null : RenewalBucket.fromParam(renewalBucket);
    EndDateScope eds = EndDateScope.fromParam(endDateScope);
    long total = contractRepository.countContracts(department, rb, eds);
    int totalPages = size == 0 ? 0 : (int) Math.ceil((double) total / size);
    List<ContractRowDto> content =
        contractRepository.findContracts(department, rb, eds, page, size, sort);
    return new PageResponse<>(content, total, totalPages, page, size);
  }

  @GetMapping("/{id}")
  public ContractDetailDto get(@PathVariable long id) {
    return contractRepository
        .findById(id)
        .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Unknown id"));
  }
}
