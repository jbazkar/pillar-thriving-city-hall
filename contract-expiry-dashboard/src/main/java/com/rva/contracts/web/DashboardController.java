package com.rva.contracts.web;

import com.rva.contracts.domain.RenewalBucket;
import com.rva.contracts.repo.ContractRepository;
import com.rva.contracts.web.dto.CountByBucketDto;
import com.rva.contracts.web.dto.CountByDepartmentDto;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/v1/dashboard")
public class DashboardController {

  private final ContractRepository contractRepository;

  public DashboardController(ContractRepository contractRepository) {
    this.contractRepository = contractRepository;
  }

  @GetMapping("/by-department")
  public List<CountByDepartmentDto> byDepartment(
      @RequestParam(required = false) String department,
      @RequestParam(required = false) String renewalBucket) {
    RenewalBucket rb = renewalBucket == null ? null : RenewalBucket.fromParam(renewalBucket);
    return contractRepository.countByDepartment(department, rb);
  }

  @GetMapping("/by-renewal-bucket")
  public List<CountByBucketDto> byRenewalBucket(
      @RequestParam(required = false) String department,
      @RequestParam(required = false) String renewalBucket) {
    RenewalBucket rb = renewalBucket == null ? null : RenewalBucket.fromParam(renewalBucket);
    return contractRepository.countByRenewalBucket(department, rb);
  }
}
