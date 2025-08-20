package com.bank_dki.be_dms.controller;

import com.bank_dki.be_dms.dto.CustomerStatusCountDto;
import com.bank_dki.be_dms.service.CustomerStatsService;
import com.bank_dki.be_dms.util.ApiResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/customer-stats")
@RequiredArgsConstructor
@CrossOrigin(origins = "*", maxAge = 3600)
public class CustomerStatsController {
    
    private final CustomerStatsService customerStatsService;
    
    @GetMapping("/status-count")
    @PreAuthorize("hasRole('ADMIN') or hasRole('OPERATOR')")
    public ResponseEntity<ApiResponse<CustomerStatusCountDto>> getCustomerStatusCountGroupByTasks(@RequestParam String filter) {

        CustomerStatusCountDto custStatsCount = customerStatsService.getCustomerStatusCountGroupByTasks(filter);
        ApiResponse<CustomerStatusCountDto> response = ApiResponse.success("Success", custStatsCount);

        return ResponseEntity.ok(response);
    }
}