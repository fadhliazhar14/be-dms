package com.bank_dki.be_dms.controller;

import com.bank_dki.be_dms.dto.CustomerStatusCountDto;
import com.bank_dki.be_dms.service.CustomerStatsService;
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
    public ResponseEntity<List<CustomerStatusCountDto>> getCustomerStatusCountGroupByTasks() {
        List<CustomerStatusCountDto> result = customerStatsService.getCustomerStatusCountGroupByTasks();
        return ResponseEntity.ok(result);
    }
}